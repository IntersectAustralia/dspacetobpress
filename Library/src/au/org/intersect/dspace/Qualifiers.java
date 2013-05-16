package au.org.intersect.dspace;
/**
 * Each line in a DSpace output file has an 'element' and a 'qualifier' 
 * attribute. This enum enumerates the valid values for 'qualifier'. 
 *
 * @author joe
 */
public enum Qualifiers 
{
    systemid,
    isbn,
    issn,
    pubcategory,
    application,
    spatial,
    commercialdist,
    issued,
    publication,
    internalauthors,
    externalauthors,
    book,
    none,
    idbn,
    volume,
    startpage,
    endpage,
    pagenumbers,
    speccalc,
    author,
    department,
    conferencename,
    proceedings,
    uriarticle,
    comments,
    paperref,
    journalname,
    issue,
    chrScopusID,
    peerreviewed,
    mimetype,
    uriconference;
}
