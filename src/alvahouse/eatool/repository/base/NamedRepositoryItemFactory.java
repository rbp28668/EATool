/*
 * NamedRepositoryItemFactory.java
 *
 * Created on 27 January 2002, 12:58
 */

package alvahouse.eatool.repository.base;

import org.xml.sax.Attributes;

/**
 * Helper base class for factories to read in NamedRepositoryItems.
 * @author  rbp28668
 */
public class NamedRepositoryItemFactory extends FactoryBase {

    /** Creates new NamedRepositoryItemFactory */
    public NamedRepositoryItemFactory() {
    }
    
    /**
     * Gets the common fields for a NamedRepository item.
     * @param item is the NamedRepositoryItem to update.
     * @param attrs are the attributes containing the common parameters to update the NRI with.
     */
    public void getCommonFields(NamedItem item, Attributes attrs) {
        String attr = attrs.getValue("name");
        if(attr != null) item.setName(attr);
        attr = attrs.getValue("description");
        if(attr != null) item.setDescription(attr);
    }

}
