package au.org.intersect.dspace;

public class EntryKey
{
    private final Elements myElement;
    private final Qualifiers myQualifier;
    public EntryKey(Elements myElement, Qualifiers myQualifier)
    {
        super();
        if (myElement == null || myQualifier == null)
        {
            throw new IllegalArgumentException("Element: " + myElement + ". Qualifier: " + myQualifier);
        }
        this.myElement = myElement;
        this.myQualifier = myQualifier;
    }
    
    public Elements getMyElement()
    {
        return myElement;
    }

    public Qualifiers getMyQualifier()
    {
        return myQualifier;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((myElement == null) ? 0 : myElement.hashCode());
        result = prime * result
            + ((myQualifier == null) ? 0 : myQualifier.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntryKey other = (EntryKey) obj;
        if (myElement == null)
        {
            if (other.myElement != null)
                return false;
        }
        else if (!myElement.equals(other.myElement))
            return false;
        if (myQualifier == null)
        {
            if (other.myQualifier != null)
                return false;
        }
        else if (!myQualifier.equals(other.myQualifier))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "EntryKey [myElement=" + myElement + ", myQualifier="
            + myQualifier + "]";
    }
    
    
}
