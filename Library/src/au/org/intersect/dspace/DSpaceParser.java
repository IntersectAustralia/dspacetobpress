package au.org.intersect.dspace;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * The output from DSpace in dublin core XML is not quite XML and not quite 
 * complient with Dublin Core. 
 * <p>
 * This parser works with DSpace output but a craftily created file that 
 * looks like DSpace at the start and end of each line would be able to make it 
 * throw. 
 * 
 * @author joe
 */
public class DSpaceParser
{
    public static String LINE_STARTS_WITH = "<dcvalue element=";
    public static String LINE_END_WITH = "</dcvalue>";
    
    public List<Entry> parse(Reader readMe) throws IOException
    {
        List<Entry> entries = new ArrayList<Entry>();
        BufferedReader reader = new BufferedReader(readMe);
        
        String line;
        String entry = null;
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();
            //If the line doesn't start with <dcvalue then ignore it.
            if (entry == null && !line.startsWith(LINE_STARTS_WITH))
            {
                continue;
            }
            
            if (entry == null)
            {
                entry = line;
            }
            else
            {
                entry += " " + line;   
            }
            
            entry = entry.trim();
            
            if (!line.endsWith(LINE_END_WITH))
            {
                continue;
            }

            line = entry;
            //Strip the start and end
            line = line.substring(LINE_STARTS_WITH.length());
            line = line.substring(0, line.length() - LINE_END_WITH.length());
            
            StringTokenizer st = new StringTokenizer(line, ">");
            String first = st.nextToken();
            String last  = st.nextToken();
            
            st = new StringTokenizer(first, "\"");
            String element = st.nextToken();
            st.nextToken();
            String qualifier = st.nextToken();
            
            last = last.replace("&apos;", "'");
            last = StringEscapeUtils.unescapeHtml3(last);
            
            try
            {
                Entry e = new Entry
                (
                    Elements.valueOf(element), 
                    Qualifiers.valueOf(qualifier), 
                    last
                );
                entries.add(e);
            } catch (Exception e)
            {
                entries.add(new Entry(element + "/" + qualifier));
            }
            
            entry = null;
        }
        
        return entries;
    }
}
