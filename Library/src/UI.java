import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bpress.Documents;
import org.bpress.ObjectFactory;
import org.bpress.Documents.Document;

import au.org.intersect.bpress.DocumentUpdater;
import au.org.intersect.bpress.PublicationInfo;
import au.org.intersect.bpress.UpdateDispatcher;
import au.org.intersect.dspace.DSpaceParser;
import au.org.intersect.dspace.Entry;

/**
 * Basic drag and drop UI.
 *
 * @author joe
 */
public class UI extends JFrame implements DropTargetListener
{
    public static void main(String []args)
    {
        SwingUtilities.invokeLater
        (
            new Runnable()
            {
                public void run()
                {
                    JFrame frame = new UI();
                    
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    

                }
            }
        );
    }
    
    private void processFiles
    (
        List<File>   processMeOrig,
        PrintWriter  notes
    ) throws IOException
    {
        ArrayList<File> processMe = new ArrayList<File>(processMeOrig);
        ArrayList<File> xmlFiles = new ArrayList<File>();
        ArrayList<List<File>> pdfFiles = new ArrayList<List<File>>();
        
        int pdfs = 0;
        int xml  = 0;
        
        ObjectFactory of = new ObjectFactory();
        //Sort of a breadth-first traversal of the files...
        while (processMe.size() > 0)
        {
            //Run through what's there, and if it is a directroy, add its contents to the end of the list...
            File first = processMe.remove(0);
            if (first.isDirectory())
            {
                processMe.addAll(Arrays.asList(first.listFiles()));
            }
            else
            {
                if (first.getName().endsWith(".xml"))
                {
                    xml++;
                    xmlFiles.add(first);
                    
                    //Then check if the pdf looks like an associated one....
                    String []files = first.getParentFile().list(new FilenameFilter() {
                        public boolean accept(File dir, String name)
                        {
                            return name.endsWith(".pdf");
                        }
                    });

                    List<File> toAdd = new ArrayList<File>();
                    pdfFiles.add(toAdd);
                    for (String fileName : files)
                    {
                        pdfs++;
                        toAdd.add(new File(first.getParentFile(), fileName));
                    }
                }
                else
                {
                    //we just ignore everything else...
                }
            }
        }
        
        notes.println("Queueing " + xml + " xml files, of which " + pdfs + " have pdfs");
        
        Map<String, Documents> byDepartment = new HashMap<String, Documents>();
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File nextOutputDir = new File(tempDir, "bpress-output");
        int i = 1;
        while (nextOutputDir.exists())
        {
            nextOutputDir = new File(tempDir, "bpress-output-" + i);
            i++;
        }
        nextOutputDir.mkdirs();

        File pdfDir = new File(nextOutputDir, "pfds");
        pdfDir.mkdirs();
        
        for (i = 0; i < xmlFiles.size(); i++)
        {
            File dcFile = xmlFiles.get(i);
            List<File> pdfFile = pdfFiles.get(i);
            
            notes.println("Processing: " + dcFile.getAbsolutePath());
            try
            {
                FileReader r = new FileReader(dcFile);
                DSpaceParser parser = new DSpaceParser();
                List<Entry> entries = parser.parse(r);
                
                PublicationInfo pi = new PublicationInfo(entries);
                if (pi.department == null)
                {
                    notes.println("Doesn't seem valid - no department");
                    continue;
                }
                
                for (String s : pi.issues)
                {
                    notes.println(s);
                }
                if (!byDepartment.containsKey(pi.department))
                {
                    byDepartment.put(pi.department, of.createDocuments());
                }
                
                Documents docs = byDepartment.get(pi.department);
                Document  doc = of.createDocumentsDocument();
                docs.getDocument().add(doc);

                UpdateDispatcher ud = new UpdateDispatcher();
                ud.init(pi);
                List<String> issues = ud.updateDocument(doc, pi, of);
                for (String issue : issues)
                {
                    notes.println(issue);
                }
                
                issues = copyAssociatedPDF(pdfFile, pdfDir, doc, of);
                for (String issue : issues)
                {
                    notes.println(issue);
                }
                
                for (Entry e : entries)
                {
                    String err = null;
                    if (e.isValid &&  pi.publicationType.expectedEntries().contains(e.key))
                    {
                        err = ud.dispatch(doc, e, of, pi);
                        if (err != null)
                        {
                            notes.print("\t");
                            notes.print(String.format("Entry <%20s, %20s>: ", e.element, e.qualifier));
                            notes.println(err);
                        }
                    }
                }
                
                List<String> warns = ud.cleanUp(doc, of);
                for (String s : warns)
                {
                    notes.println("\t" + s);
                }
            } catch (Exception e)
            {
                notes.println("\tHad problem processing " + dcFile.getName());
                e.printStackTrace(notes);
            }
            
            notes.println();
        }
        
        for (String s : byDepartment.keySet())
        {
            Documents docs = byDepartment.get(s);
            File outputFile = new File(nextOutputDir, s + ".xml");
            FileWriter fw = new FileWriter(outputFile);
            PrintWriter pw = new PrintWriter(fw);
            marshal(docs, pw);
            pw.flush();
            fw.flush();
            fw.close();    
        }
        
        Desktop.getDesktop().open(nextOutputDir);
    }
    
    public static void marshal(Documents docs, PrintWriter out) 
    {
        try {
            JAXBContext jc = JAXBContext.newInstance(Documents.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "http://www.bepress.com/document-import.xsd");
            m.marshal( docs, out );
        } catch( JAXBException jbe ){
            // ...
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(Class<T> docClass, InputStream inputStream)
        throws JAXBException
    {
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        T doc = (T)u.unmarshal(inputStream);
        return doc;
    }
    private JTextArea myOutput; 
    
    public UI()
    {
        super("DublinCoreToBPress");
        JPanel dropTarget = new JPanel();
        JLabel label      = new JLabel("Drop files on this label to translate them", JLabel.RIGHT);
        myOutput = new JTextArea(40, 80);
        BoxLayout layout = new BoxLayout(dropTarget, BoxLayout.Y_AXIS);
        
        dropTarget.setLayout(layout);
        label.setMinimumSize(new Dimension(300, 300));
        label.setPreferredSize(new Dimension(300, 300));
        dropTarget.add(label);
        dropTarget.add(new JScrollPane(myOutput));
        
        getContentPane().add(dropTarget, BorderLayout.CENTER);
        
        setDropTarget(new DropTarget(dropTarget, DnDConstants.ACTION_COPY_OR_MOVE, this));
    }

    public void dragEnter(DropTargetDragEvent dtde)
    {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }
    }

    @Override
    public void dragExit(DropTargetEvent dte)
    {
        
        
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde)
    {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void drop(DropTargetDropEvent dtde)
    {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            dtde.acceptDrop(dtde.getDropAction());
            List<File> theFiles;
            try
            {
                theFiles = (List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                Writer w = new Writer() {
                    public void close() throws IOException
                    {
                        
                    }

                    public void flush() throws IOException
                    {
                        
                    }

                    public void write(char[] arg0, int arg1, int arg2)
                        throws IOException
                    {
                        myOutput.append(new String(arg0, arg1, arg2));
                    }
                };
                myOutput.setText("");
                PrintWriter pw = new PrintWriter(w);
                processFiles(theFiles, pw);
                pw.flush();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde)
    {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }
    }
    
    public static List<String> copyAssociatedPDF
    (
        List<File> pdfFiles, 
        File pdfOutputDir, 
        Document toUpdate, 
        ObjectFactory of
    )
    {
        List<String> warns = new ArrayList<String>();
        if (pdfFiles.size() == 0)
        {
            toUpdate.setDepartment(DocumentUpdater.makeEST("doc", of));
            return Collections.<String>emptyList();
        }
            
        
        StringBuilder sb = new StringBuilder();
        
        for (File pdfFile : pdfFiles)
        {
            String baseName = pdfFile.getName();
            File dest = new File(pdfOutputDir, baseName);
            int suffix = 0;
            while (dest.exists())
            {
                suffix ++;
                dest = new File(pdfOutputDir, suffix + "-" + baseName);
            }
            if (suffix > 0)
            {
                warns.add("The file " + pdfFile.getName() + " was renamed because there was already a file by that name in the pdfs directory");
            }
            if (sb.length() > 0)
            {
               sb.append(", ");   
            }
            sb.append(dest.getName());
            try
            {
                copyFile(pdfFile, dest);
            }
            catch (IOException e)
            {
                warns.add("Could not copy pdf file across: " + e.getLocalizedMessage());
            }
        }
        
        toUpdate.setDepartment(DocumentUpdater.makeEST(sb.toString(), of));
        
        return warns;
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            
            long amtTransferred = 0;
            while (amtTransferred < source.size())
            {
                amtTransferred += destination.transferFrom(source, amtTransferred, source.size() - amtTransferred);
            }
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}
