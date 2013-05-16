package au.org.intersect.dspace;

/**
 * This class carries the contents from one line of a DSpace file. It's just
 * the element and qualifier name, along with whatever was in between the 
 * DCValue tags for that line.
 * 
 * @author joe
 */
public class Entry
{
    public final EntryKey   key;
    public final Elements   element;
    public final Qualifiers qualifier;
    public final String     stringValue;
    public final boolean    isValid;
    
    public Entry(String invalidTags)
    {
        element = null;
        qualifier = null;
        stringValue = invalidTags;
        isValid = false;
        key = null;
    }
    
    public Entry(Elements element, Qualifiers qualifier, String stringValue)
    {
        super();
        this.element = element;
        this.qualifier = qualifier;
        this.stringValue = stringValue;
        this.isValid = true;
        this.key = new EntryKey(element, qualifier);
    }
    
    public String toString()
    {
        return element + "/" + qualifier + ":\t" + stringValue;
    }
}
