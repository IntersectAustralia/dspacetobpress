package au.org.intersect.bpress;

import static au.org.intersect.dspace.Elements.contributor;
import static au.org.intersect.dspace.Elements.date;
import static au.org.intersect.dspace.Elements.description;
import static au.org.intersect.dspace.Elements.identifier;
import static au.org.intersect.dspace.Elements.publisher;
import static au.org.intersect.dspace.Elements.title;
import static au.org.intersect.dspace.Qualifiers.author;
import static au.org.intersect.dspace.Qualifiers.book;
import static au.org.intersect.dspace.Qualifiers.chrScopusID;
import static au.org.intersect.dspace.Qualifiers.comments;
import static au.org.intersect.dspace.Qualifiers.conferencename;
import static au.org.intersect.dspace.Qualifiers.department;
import static au.org.intersect.dspace.Qualifiers.endpage;
import static au.org.intersect.dspace.Qualifiers.externalauthors;
import static au.org.intersect.dspace.Qualifiers.internalauthors;
import static au.org.intersect.dspace.Qualifiers.isbn;
import static au.org.intersect.dspace.Qualifiers.issn;
import static au.org.intersect.dspace.Qualifiers.issue;
import static au.org.intersect.dspace.Qualifiers.issued;
import static au.org.intersect.dspace.Qualifiers.journalname;
import static au.org.intersect.dspace.Qualifiers.none;
import static au.org.intersect.dspace.Qualifiers.proceedings;
import static au.org.intersect.dspace.Qualifiers.pubcategory;
import static au.org.intersect.dspace.Qualifiers.publication;
import static au.org.intersect.dspace.Qualifiers.startpage;
import static au.org.intersect.dspace.Qualifiers.uriarticle;
import static au.org.intersect.dspace.Qualifiers.uriconference;
import static au.org.intersect.dspace.Qualifiers.volume;
import au.org.intersect.dspace.EntryKeySet;

/**
 * This class holds the set of expected Element/Qualifier pairs for each 
 * kind of BPress article.
 * 
 * @author joe
 */
public class BPress
{
    
    private static EntryKeySet union(EntryKeySet ... subsets)
    {
        EntryKeySet folded = new EntryKeySet();
        for (EntryKeySet set : subsets)
        {
            folded.addAll(set);
        }
        return folded;
    }
    
    public static EntryKeySet UNIVERSAL_TAGS = union
    (
        contributor.with(author, department),
        description.with(comments),
        identifier.with(chrScopusID, uriarticle, uriconference)
    );
    
    public static EntryKeySet BOOK_WITHOUT_PAGES = union
    (
        UNIVERSAL_TAGS,
        identifier.with(isbn),
        description.with(pubcategory, externalauthors, internalauthors, volume, issue),
        date.with(issued),
        title.with(publication, book),
        publisher.with(none)
    );

    public static EntryKeySet BOOK_WITH_PAGES = union
    (
        BOOK_WITHOUT_PAGES,
        description.with(startpage, endpage)
    );
    
    public static EntryKeySet ARTICLE = union
    (
        UNIVERSAL_TAGS,
        identifier.with(issn, uriarticle),
        description.with(pubcategory, internalauthors, externalauthors, volume, issue, startpage, endpage),
        date.with(issued),
        title.with(publication, journalname)
    );
        
    public static EntryKeySet BASIC_CONFERENCE = union
    (
        UNIVERSAL_TAGS,
        identifier.with(uriconference),
        description.with(pubcategory, internalauthors, externalauthors),
        date.with(issued),
        title.with(publication),
        title.with(conferencename)
    );
    
    public static EntryKeySet CONFERENCE_WITH_UNPULISHED_PROCEEDINGS = union
    (
        BASIC_CONFERENCE,
        title.with(proceedings)
    );
    
    public static EntryKeySet PUBLISHED_CONFERENCE = union
    (
        BASIC_CONFERENCE,
        identifier.with(isbn),
        description.with(startpage, endpage),
        publisher.with(none)
    );
    
    public static EntryKeySet OTHER = union
    (
        UNIVERSAL_TAGS,
        description.with(pubcategory),
        description.with(internalauthors, externalauthors),
        date.with(issued),
        title.with(publication)
    );
}
