package au.org.intersect.bpress;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import au.org.intersect.dspace.EntryKey;
import au.org.intersect.dspace.EntryKeySet;

public enum PublicationTypes
{
    A1("book", BPress.BOOK_WITH_PAGES),
    A2("book", BPress.BOOK_WITHOUT_PAGES),
    A3("book", BPress.BOOK_WITHOUT_PAGES),
    A4("book", BPress.BOOK_WITHOUT_PAGES),
    B1("book_contribution", BPress.BOOK_WITH_PAGES),
    B2("book_contribution", BPress.BOOK_WITH_PAGES),
    C1("article", BPress.ARTICLE),
    C2("article", BPress.ARTICLE),
    C3("article", BPress.ARTICLE),
    C4("article", BPress.ARTICLE),
    E1("presentation", BPress.PUBLISHED_CONFERENCE),
    E2("presentation", BPress.PUBLISHED_CONFERENCE),
    E3("presentation", BPress.CONFERENCE_WITH_UNPULISHED_PROCEEDINGS),
    E4("presentation", BPress.PUBLISHED_CONFERENCE),
    E5("presentation", BPress.PUBLISHED_CONFERENCE),
    E6("presentation", BPress.BASIC_CONFERENCE),
    CreativeWorks("other", BPress.OTHER);
        
    PublicationTypes(String documentType, EntryKeySet ... expectedEntries)
    {
        myDocumentType    = documentType;
        myExpectedEntries = new LinkedHashSet<EntryKey>();
        for (EntryKeySet addMe : expectedEntries)
        {
            myExpectedEntries.addAll(addMe);
        }; 
    }
    
    private String myDocumentType;
    
    private HashSet<EntryKey> myExpectedEntries;
    
    public String documentType()
    {
        return myDocumentType;
    }
    
    public Set<EntryKey> expectedEntries()
    {
        return myExpectedEntries;
    }
    
    
    
}
