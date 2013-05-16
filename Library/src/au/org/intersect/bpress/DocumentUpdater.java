package au.org.intersect.bpress;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.bpress.Documents;
import org.bpress.ExcelDateType;
import org.bpress.ExcelHtmlMarkupType;
import org.bpress.ExcelStringType;
import org.bpress.ExcelURIType;
import org.bpress.Individual;
import org.bpress.ObjectFactory;
import org.bpress.Documents.Document;
import org.bpress.Documents.Document.Authors;
import org.bpress.Documents.Document.Fields;
import org.bpress.Documents.Document.Fields.Field;

import au.org.intersect.dspace.Elements;
import au.org.intersect.dspace.Entry;
import au.org.intersect.dspace.Qualifiers;

/**
 * A DocumentUpdater encapulates a function which takes as input a Document
 * and a DSpace Entry, and modifies the Document to reflect the contents of the 
 * Entry.  
 * 
 * @author joe
 */
public abstract class DocumentUpdater
{
    /**
     * The public interface of this class. This used to do some sanity checking,
     * but no longer does. When I get SCU library to tell me what constitutes 
     * 'sane', the checking will go back in here, and this method will no
     * longer be redundant.
     * <p>
     * Returns a string that gives a message to a user if something requiring
     * a warning happens, or null if everything was as expected. 
     */
    public String updateDocument
    (
        Documents.Document  doc, 
        Entry               ent, 
        ObjectFactory       of,
        PublicationInfo     info
        
    )
    {
        return _updateDocument(doc, ent, of, info);
    }
    
    /**
     * Some DocumentUpdaters (e.g. author/department)
     * can require to be told that they are done (e.g. an author with no 
     * department).
     * 
     * Return a warning, if any
     */
    public String cleanUp(Document doc, ObjectFactory of)
    {
        //by default, does nothing.
        return null;
    }
    
    /**
     * For implementation by subclasses
     */
    public abstract String _updateDocument
    (
        Documents.Document doc, 
        Entry              entry, 
        ObjectFactory      of,
        PublicationInfo    info
    );
    
    
    // ******************************************
    //      Utility functions to hide XML hassle
    // ******************************************
    
    /**
     * Take a Document, and if there aren't already Fields in it, create them.
     * Then, return the fields from that Document. 
     */
    public static Fields getDocFields(Document doc, ObjectFactory of)
    {
        Fields f = doc.getFields();
        if (f == null)
        {
            doc.setFields(of.createDocumentsDocumentFields());
            f = doc.getFields();
        }
        return f;
    }
    
    /**
     * Create an ExcelStringType with the contents 'string'. If the string
     * is 'N/A', then leave the EST blank.
     */
    public static ExcelStringType makeEST(String string, ObjectFactory of)
    {
        ExcelStringType est = of.createExcelStringType();
        if (!string.equals("N/A"))
        {
            est.setValue(string);
        }
        return est;
    }
    
    /**
     * Create an ExcelHTMLMarkupType with the contents 'string'. If the string
     * is 'N/A', then leave the EHMT blank.
     */
    public static ExcelHtmlMarkupType makeEHMT(String string, ObjectFactory of)
    {
        ExcelHtmlMarkupType ehmt = of.createExcelHtmlMarkupType();
        if (!string.equals("N/A"))
        {
            ehmt.getContent().add(string);
        }
        return ehmt;
    }
    
    /**
     * Create a ExcelDateType, with the date parsed from 'string'. The string
     * should just be a year (i.e. yyyy format according to a SimpleDateFormat).
     */
    public static ExcelDateType makeDate(String string, ObjectFactory of)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date d;
        try
        {
            d = sdf.parse(string);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(d);
        XMLGregorianCalendar date2;
        try
        {
            date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        catch (DatatypeConfigurationException e)
        {
            throw new RuntimeException(e);
        }
        
        date2.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        ExcelDateType date = of.createExcelDateType();
        date.setValue(date2);
        return date;
    }
    
    /**
     * Add a BPress field to the document, with the given 'name' and 'type', 
     * plus a string of 'value' between the field tags. 
     */
    public static void addField
    (
        Documents.Document  doc, 
        String              type, 
        String              name, 
        String              value, 
        ObjectFactory       of
    )
    {
        Fields fs = getDocFields(doc, of);
        Field field = of.createDocumentsDocumentFieldsField();
        field.setName(name);
        field.setType(type);
        
        field.getContent().add(new JAXBElement<ExcelStringType>(new QName("value"), ExcelStringType.class, makeEST(value, of)));
        
        fs.getField().add(field);
    }
    
    //**********************************************
    // Some helper methods to make DocumentUpdaters for 
    // some common tasks.
    //**********************************************
    
    /**
     * Make a document updater that just throws when run
     */
    public static DocumentUpdater makeIllegal(final Elements e, final Qualifiers q)
    {
        return new DocumentUpdater() 
        {
            public String _updateDocument
            (
                Document        doc, 
                Entry           entry,
                ObjectFactory   of,
                PublicationInfo info
            )
            {
                throw new RuntimeException("Illegal combination.");
            }
            
        };
    }
    
    /**
     * Make a document updater that does nothing, and returns that as a warning
     */
    public static DocumentUpdater makeNoOp(final String msg)
    {
        return new DocumentUpdater() 
        {
            public String _updateDocument
            (
                Document        doc, 
                Entry           entry,
                ObjectFactory   of,
                PublicationInfo info
            )
            {
                return msg;
            }
            
        };
    }
    
    public static DocumentUpdater makeTitleSetter()
    {
        return new DocumentUpdater()
        {
            public String _updateDocument
            (
                Document        doc, 
                Entry           entry,
                ObjectFactory   of,
                PublicationInfo info
            )
            {
                if (Qualifiers.publication == entry.qualifier)
                {
                    doc.setTitle(makeEST(entry.stringValue, of));
                }
                else
                {
                    switch (info.publicationType)
                    {
                    case A1:
                    case A2:
                    case A3:
                    case A4: break;
                    default:
                         addField(doc, "string", "source_publication", entry.stringValue, of);
                    }
                }
                return null;
            }
        };
    }
    /**
     * Set a field in the document, called 'name' with the contents of the 
     * entity used to provide a value
     */
    public static DocumentUpdater makeStringFieldSetter(final String name)
    {
        return new DocumentUpdater() 
        {
            public String _updateDocument
            (
                Document        doc, 
                Entry           entry,
                ObjectFactory   of,
                PublicationInfo info
            )
            {
                addField(doc, "string", name, entry.stringValue, of);
                return null;
            }
            
        };
    }

    /**
     * A setter that sets the full text field of a BPress document
     * with the value of the Entry.
     */
    public static DocumentUpdater makeFullTextSetter()
    {
        return new DocumentUpdater() 
        {
            public String _updateDocument
            (
                Document        doc, 
                Entry           entry,
                ObjectFactory   of,
                PublicationInfo info
            )
            {
                
                ExcelURIType eut = new ExcelURIType();
                eut.setValue(entry.stringValue);
                doc.setFulltextUrl(eut);
                return null;
            }
            
        };
    }

    /**
     * A setter that sets the full text field of a BPress document
     * with the value of the Entry.
     */
    public static DocumentUpdater makeSuggestedCitationSetter()
    {
        return new DocumentUpdater() 
        {
            public String _updateDocument
            (
                Document        doc, 
                Entry           entry,
                ObjectFactory   of,
                PublicationInfo info
            )
            {
                ExcelURIType eut = new ExcelURIType();
                eut.setValue(entry.stringValue);
                
                doc.setFulltextUrl(eut);
                return null;
            }
            
        };
    }

    /**
     * This one takes a bunch pair of author/department fields and on getting
     * the second sets up a 'contributor' set of tags in the document. The 
     * DovumentUpdater created by this method is stateful (i.e. it remembers 
     * Entries that it has seen before, and uses them eventually). 
     */
    public static DocumentUpdater makeContributorUpdater(final PublicationInfo theInfo)
    {
        return new DocumentUpdater()
        {
        private String name;
        private String department;
        private PublicationInfo info = theInfo;
        private List<String> authorsWithoutDepartments = new ArrayList<String>();
        private List<String> authorsWithDepartments = new ArrayList<String>();
        
        public String _updateDocument(Document doc, Entry ent, ObjectFactory of, PublicationInfo info)
        {
            String warning = null;
            if (ent.qualifier == Qualifiers.department && department != null)
            {
                warning = "The department " + department + " was found, but no author was attached. Skipping";
            }
            
            if (ent.qualifier == Qualifiers.author && name != null)
            {
                authorsWithoutDepartments.add(name);
                makeIndividual(null, name, of, doc);
                name = null;
            }
            
            if (ent.qualifier == Qualifiers.author)
            {
                name = ent.stringValue;
            }
            
            if (ent.qualifier == Qualifiers.department)
            {
                department = ent.stringValue;
            }
            
            if (department != null && name != null)
            {
                authorsWithDepartments.add(name);
                makeIndividual(department, name, of, doc);
                name = null;
                department = null;
            }
            
            return warning;
        }
        
        public String cleanUp(Document doc, ObjectFactory of)
        {
            String warning = "";
            
            if (department != null)
            {
                warning += "Cleaning up, had a department without an author: " + department + ". Skipping. ";
            }

            if (name != null)
            {
                authorsWithoutDepartments.add(name);
                makeIndividual(department, name, of, doc);
                name = null;
            }

            if (authorsWithDepartments.size() != info.internalAuthors)
            {
                warning += "Expected " + info.internalAuthors + " internal authors but had " + authorsWithDepartments.size() + " authors with departments. ";
            }
                
            if (authorsWithoutDepartments.size() != info.externalAuthors)
            {
                warning += "Expected " + info.externalAuthors + " external authors but had " + authorsWithoutDepartments.size() + " authors without departments. ";
            }

            if (warning.length() == 0)
            {
                warning = null;
            }
            return warning;
        }
        
        
        };
        
    }
    
    private static void makeIndividual(String department, String name, ObjectFactory of, Document doc)
    {
        Individual i = of.createIndividual();
        if (department != null)
        {
            i.setInstitution(makeEST(department, of));
        }
        
        //Try to get a first and last name !!
        StringTokenizer st = new StringTokenizer(name, ",");
        String lastName = st.nextToken().trim();
        lastName = processLastName(lastName);
        String firstName = st.nextToken().trim();
        i.setLname(makeEST(lastName, of));
        i.setFname(makeEST(firstName, of));
        
        Authors a = doc.getAuthors();
        if (a == null)
        {
            doc.setAuthors(of.createDocumentsDocumentAuthors());
            a = doc.getAuthors();
        }
        a.getAuthor().add(i);

    }

    /**
     * Do some capitalisation of names according to the SCU format. The rule is
     * basically "If the name comprises only upper case letters and punctuation, then
     * every letter that follows another letter is converted to lower case. Everythhing else is
     * left as is."  
     */
    private static String processLastName(String str)
    {
        if (!str.matches("[A-Z\\-\\' ]*"))
        {
            return str;
        }

        StringBuffer lastName = new StringBuffer();
        boolean lastWasSeparator = true;
        for (int i = 0; i < str.length(); i++)
        {
            char next = str.charAt(i);
            if (Character.isLetter(next))
            {
                if (!lastWasSeparator)
                {
                    lastName.append(Character.toLowerCase(next));
                }
                else
                {
                    lastName.append(next);
                }
                lastWasSeparator = false;
            }
            else
            {
                lastName.append(next);
                lastWasSeparator = true;
            }
        }
        
        return lastName.toString();
    }

    public enum Entities { identity, fpage, lpage, pubdate, title, comments};
    
    /**
     * A DocumentUpdater that sets a tag in the BPress document based on the
     * Entity given. 
     */
    public static DocumentUpdater makeEntitySetter(final Entities toSet)
    {
        return new DocumentUpdater()
        {
            public String _updateDocument
            (
                Document doc, 
                Entry ent,
                ObjectFactory of,
                PublicationInfo info
            )
            {
                try
                {
                    switch (toSet)
                    {
                    case fpage: doc.setFpage(new BigInteger(ent.stringValue));
                         break;
                    case lpage: doc.setLpage(new BigInteger(ent.stringValue));
                         break;
                    case identity: doc.setIdentifier(makeEST(ent.stringValue, of));
                         break;
                    case pubdate: 
                            doc.setPublicationDate(makeDate(ent.stringValue, of));
                            doc.setPublicationDateDateFormat(makeEST("YYYY", of));
                         break;
                    case title: doc.setTitle(makeEST(ent.stringValue, of));
                         break;
                    case comments: doc.setComments(makeEHMT(ent.stringValue, of));
                        break;
                    }
                    return null;
                } catch (Exception e)
                {
                }
                return "Could not create a valid value for '" + ent.stringValue + "'";    
            }
            
        };
    }
   
}
