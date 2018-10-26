/*
 * CopyKeyAction.java
 * Project: EATool
 * Created on 24 Nov 2007
 *
 */
package alvahouse.eatool.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.repository.base.KeyedItem;

/**
 * CopyKeyAction is for use in explorer action sets where popups are used and
 * is a generic action to copy a string representation of the selected item's
 * key to the clipboard.  This only works if the selected item implements
 * KeyedItem.  This allows script writers to get the UUIDs for scripts without
 * having to clutter up the explorer trees (which was messy).
 * 
 * @author rbp28668
 */
public class CopyKeyAction extends AbstractAction {

    private Explorer explorer;
    private JInternalFrame parent;
   
    public static final String NAME="CopyKey";
    
    private static final long serialVersionUID = 1L;

    public CopyKeyAction(JInternalFrame parent, Explorer explorer){
        if(explorer == null){
            throw new NullPointerException("Null explorer in CopyKeyAction");
        }
        this.explorer = explorer;
        this.parent = parent;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            DefaultMutableTreeNode node = explorer.getSelectedNode();
            if(node != null){
                Object tied = node.getUserObject();
               if (tied instanceof KeyedItem) {
                   KeyedItem keyed = (KeyedItem) tied;
                   Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                   StringSelection sel = new StringSelection(keyed.getKey().toString());
                   clip.setContents(sel,sel);
                }
                
            }
        } catch (Throwable t){
            new ExceptionDisplay(parent,t);
        }
    }

}
