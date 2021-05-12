/*
 * RepositoryItemBase.java
 *
 * Created on 10 January 2002, 20:00
 */

package alvahouse.eatool.repository.base;

/**
 * A NamedRepositoryItem contains a name and description of the item.
 * @author  rbp28668
 */
import java.io.IOException;

import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

public class NamedRepositoryItem extends RepositoryItem implements  Comparable<NamedItem>, NamedItem  {

    private String m_name = "";
    private String m_description = "";

    /** Creates new RepositoryItemBase */
    public NamedRepositoryItem(UUID uuid) {
        super(uuid);
    }
    
    protected void writeAttributesXML(XMLWriter out) throws IOException {
        if(getName().length() > 0) 
            out.addAttribute("name",getName());
        out.addAttribute("uuid",getKey().toString());
        if(getDescription().length() > 0) {
            out.addAttribute("description",getDescription());
        }
    }

    protected void cloneTo(NamedRepositoryItem copy) {
        super.cloneTo(copy);
        copy.m_name = new String(m_name);
        copy.m_description = new String(m_description);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.base.NamedItem#getName()
     */
    public String getName(){
        return m_name;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.base.NamedItem#setName(java.lang.String)
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.base.NamedItem#getDescription()
     */
    public String getDescription() {
        return m_description;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.base.NamedItem#setDescription(java.lang.String)
     */
    public void setDescription(String desc) {
        m_description = desc;
    }
    
    public String toString() {
        return getName();
    }
    
    public int compareTo(NamedItem other) {
        if(getKey().equals(other.getKey()))
            return 0;
        int cmp = getName().compareTo(other.getName());
        if(cmp != 0)
            return cmp;
        return getKey().compareTo(other.getKey());
    }
    
}
