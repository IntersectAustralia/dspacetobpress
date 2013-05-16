package au.org.intersect.dspace;

/**
 * Each line in a DSpace output file has an 'element' and a 'qualifier' 
 * attribute. This enum enumerates the valid values for 'element'. 
 *
 * @author joe
 */
public enum Elements
{
    identifier, 
    coverage,
    description,
    date,
    title,
    publisher,
    format,
    contributor;
    
    public EntryKeySet with(Qualifiers ... qs)
    {
        EntryKeySet set = new EntryKeySet();
        for (Qualifiers q : qs)
        {
            set.add(new EntryKey(this, q));
        }
        
        return set;
    }
};