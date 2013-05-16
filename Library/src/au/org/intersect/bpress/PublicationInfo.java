package au.org.intersect.bpress;

import static au.org.intersect.bpress.PublicationTypes.A1;
import static au.org.intersect.bpress.PublicationTypes.B1;
import static au.org.intersect.bpress.PublicationTypes.C1;
import static au.org.intersect.bpress.PublicationTypes.C2;
import static au.org.intersect.bpress.PublicationTypes.C3;
import static au.org.intersect.bpress.PublicationTypes.C4;
import static au.org.intersect.bpress.PublicationTypes.E1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.intersect.dspace.Elements;
import au.org.intersect.dspace.Entry;
import au.org.intersect.dspace.EntryKey;
import au.org.intersect.dspace.Qualifiers;

/**
 * Records some information about a publication based on a list of Entries
 * and some rules from the SCU Research Office.
 * 
 * This object is also used to keep track of the entries that are dispatched 
 * against each file, to do some reporting at the end. 
 * 
 * @author joe
 */
public class PublicationInfo
{
    public static Set<PublicationTypes> PEER_REVIEWED = new HashSet<PublicationTypes>(Arrays.asList(
        A1, 
        B1, 
        C1, 
        C3,
        E1
    ));
    
    public static Set<PublicationTypes> PUBLICATION_STATUS = new HashSet<PublicationTypes>(Arrays.asList(
        C1, C2, C3, C4
    ));
    
    public PublicationTypes publicationType;
    public boolean          peerReviewed;
    public boolean          publicationStatus;
    public String           department;
    public List<String>     issues;
    public int              internalAuthors;
    public int              externalAuthors;
    
    /**
     * Sniff the list (and remove the publication entry stuff, if we find it).
     */
    public PublicationInfo(List<Entry> sniffMe)
    {
        for (Entry entry : sniffMe)
        {
            if (entry.element == Elements.description && entry.qualifier == Qualifiers.pubcategory)
            {
                for (PublicationTypes pt : PublicationTypes.values())
                {
                    if (entry.stringValue.startsWith(pt.name()))
                    {
                        publicationType = pt;
                        break;
                    }
                }
                
                if (publicationType == null)
                {
                    throw new RuntimeException
                    (
                        "Had " + entry.stringValue + " as publication type, but no such type is known to this program."
                    );
                }
                
                peerReviewed = PEER_REVIEWED.contains(publicationType);
                publicationStatus = PUBLICATION_STATUS.contains(publicationType);
            }
            
            if (entry.element == Elements.contributor && entry.qualifier == Qualifiers.department && department == null)
            {
                department = entry.stringValue;
            }
            
            if (entry.element == Elements.description && entry.qualifier == Qualifiers.internalauthors)
            {
                internalAuthors = Integer.parseInt(entry.stringValue);
            }
            
            if (entry.element == Elements.description && entry.qualifier == Qualifiers.externalauthors)
            {
                externalAuthors = Integer.parseInt(entry.stringValue);
            }
        }
        
        workOutIssues(sniffMe);
    }
    
    private void workOutIssues(List<Entry> sniffMe)
    {
        issues = new ArrayList<String>();
        
        //First, work out if there are any things missing that should be there
        Set<EntryKey> expected = publicationType.expectedEntries();
        Set<EntryKey> unexpected = new HashSet<EntryKey>();
        Set<EntryKey> missing    = new HashSet<EntryKey>(expected);
        
        for (Entry e : sniffMe)
        {
            if (e.isValid)
            {
                EntryKey next = new EntryKey(e.element, e.qualifier);
                if (expected.contains(next))
                {
                    missing.remove(next);
                }
                else
                {
                    unexpected.add(next);
                }
            }
            else
            {
                if (issues.size() == 0)
                {
                    issues.add("The following entries had elements or qualifiers that this program was not expecting. They will be ignored.");
                }
                issues.add("\t" + e.stringValue);
            }
        }
        
        if (missing.size() > 0)
        {
            issues.add("The following entries were expected, but not in the dublin core file.");
            for (EntryKey k : missing)
            {
                issues.add(String.format("\t%s/%s", k.getMyElement().name(), k.getMyQualifier().name()));
            }
        }
        
        if (unexpected.size() > 0)
        {
            issues.add("The following entries were in the dublin core file, but not expected, and cannot go into bpress, so will be ignored.");
            for (EntryKey k : unexpected)
            {
                issues.add
                (
                    String.format
                    (
                        "\t%s/%s", 
                        k.getMyElement().name(), 
                        k.getMyQualifier().name()
                    )
                );
            }
        }
    }
}
