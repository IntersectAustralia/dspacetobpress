package au.org.intersect.bpress;

import static au.org.intersect.bpress.DocumentUpdater.addField;
import static au.org.intersect.bpress.DocumentUpdater.makeEST;
import static au.org.intersect.bpress.DocumentUpdater.makeEntitySetter;
import static au.org.intersect.bpress.DocumentUpdater.makeNoOp;
import static au.org.intersect.bpress.DocumentUpdater.makeStringFieldSetter;
import static au.org.intersect.dspace.Elements.contributor;
import static au.org.intersect.dspace.Elements.date;
import static au.org.intersect.dspace.Elements.description;
import static au.org.intersect.dspace.Elements.identifier;
import static au.org.intersect.dspace.Elements.publisher;
import static au.org.intersect.dspace.Elements.title;
import static au.org.intersect.dspace.Qualifiers.author;
import static au.org.intersect.dspace.Qualifiers.chrScopusID;
import static au.org.intersect.dspace.Qualifiers.book;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bpress.ObjectFactory;
import org.bpress.Documents.Document;

import au.org.intersect.bpress.DocumentUpdater.Entities;
import au.org.intersect.dspace.Elements;
import au.org.intersect.dspace.Entry;
import au.org.intersect.dspace.Qualifiers;

/**
 * An UpdateDispatcher takes Entries from a list, and dispatches them to
 * DocumentUpdater instances based on the Entrys' 'element' and 'qualifier'.
 */
public class UpdateDispatcher
{
    //Dispatch table
    private Map<Elements, Map<Qualifiers, DocumentUpdater>> table;
    
    public UpdateDispatcher()
    {
        table = new HashMap<Elements, Map<Qualifiers, DocumentUpdater>>();
        for (Elements e : Elements.values())
        {
            Map<Qualifiers, DocumentUpdater> map = new HashMap<Qualifiers, DocumentUpdater>();
            table.put(e, map);
            for (Qualifiers q : Qualifiers.values())
            {
                map.put(q, DocumentUpdater.makeIllegal(e, q));
            }
        }
    }

    /**
     * Some 'global' rules based not on Entries themselves, but on PublicationInfo
     * based on those Entries.
     * 
     * Return a list of errors, problems with the update of the document.
     */
    public List<String> updateDocument(Document doc, PublicationInfo info, ObjectFactory factory)
    {
        ArrayList<String> issues = new ArrayList<String>();
        if (info.peerReviewed)
        {
            addField(doc, "boolean", "peer_reviewed", "true", factory);
            addField(doc, "string", "reviewed", "Peer-Reviewed", factory);
        }
        
        addField(doc, "boolean", "create_openurl", "false", factory);
        
        if (info.department != null)
        {
            doc.setDepartment(makeEST(info.department, factory));
        }
        
        doc.setDocumentType(makeEST(info.publicationType.documentType(), factory));
        
        return issues;
    }
    
    /**
     * Set up the dispatch rules. This is where it's all glued together.
     */
    public void init(PublicationInfo info)
    {
        register(identifier,  isbn,             makeStringFieldSetter("isbn"));
        register(identifier,  issn,             makeStringFieldSetter("isbn"));
        register(identifier,  uriarticle,       makeStringFieldSetter("doi"));
        register(identifier,  uriconference,    makeStringFieldSetter("doi"));
        register(identifier,  chrScopusID,      makeStringFieldSetter("indexed_by_scopus"));
        register(description, pubcategory,      makeNoOp(null)); //yep, already handled
        register(description, internalauthors,  makeNoOp(null)); //meta-information
        register(description, externalauthors,  makeNoOp(null)); //meta-information
        register(description, volume,           makeStringFieldSetter("volume"));
        register(description, issue,            makeStringFieldSetter("issue"));
        register(description, startpage,        makeEntitySetter(Entities.fpage));
        register(description, endpage,          makeEntitySetter(Entities.lpage));
        register(description, comments,         makeNoOp(null));
        register(date,        issued,           makeEntitySetter(Entities.pubdate));
        
        register(title,       none,             makeNoOp(null));

        DocumentUpdater titleUpdater = DocumentUpdater.makeTitleSetter();
        register(title,       publication,      titleUpdater);
        register(title,       book,             titleUpdater);
        register(title,       conferencename,   titleUpdater);
        register(title,       proceedings,      titleUpdater);
        register(title,       journalname,      titleUpdater);
        
        
        register(publisher,   none,             makeStringFieldSetter("publisher"));
        
        //The authors updater one is way special! It has to be shared by
        // both the contributor qualifiers.
        DocumentUpdater contributorUpdater = DocumentUpdater.makeContributorUpdater(info);
        register(contributor, author, contributorUpdater);
        register(contributor, department, contributorUpdater);
    }

    void register(Elements el, Qualifiers ql, DocumentUpdater ud)
    {
        table.get(el).put(ql, ud);
    }
    
    /**
     * Invoke a dispatch based on the dispatch table set up in 'init'.
     */
    public String dispatch(Document d, Entry e, ObjectFactory of, PublicationInfo info)
    {
        if (e.isValid)
        {
            return table.get(e.element).get(e.qualifier).updateDocument(d, e, of, info);
        }
        else
        {
            return e.stringValue;
        }
    }
    
    public List<String> cleanUp(Document d, ObjectFactory f)
    {
        List<String> warnings = new ArrayList<String>();
        for (Map<Qualifiers, DocumentUpdater> top : table.values())
        {
            for (DocumentUpdater updater : top.values())
            {
                String warn = updater.cleanUp(d, f);
                if (warn != null)
                {
                    warnings.add(warn);
                }
            }
        }
        
        return warnings;
    }
}
