/*
 * Explorer.java
 * Project: EATool
 * Created on 24 Nov 2007
 *
 */
package alvahouse.eatool.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Explorer is an interface any explorer tree should implement.
 * 
 * @author rbp28668
 */
public interface Explorer {

    /** gets the selected node (if any) from the tree
     * @return the selected node or null if none selected
     */
    public DefaultMutableTreeNode getSelectedNode();

}
