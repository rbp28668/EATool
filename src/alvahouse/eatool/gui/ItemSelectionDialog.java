/*
 * ItemSelectionDialog.java
 * Project: EATool
 * Created on 16-Feb-2006
 *
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


/**
 * ItemSelectionDialog is a general purpose dialog for selecting Objects in a
 * list box.  This allows multiple selections, for other simple selection tasks
 * see the static methods of the Dialogs class.
 * 
 * @author rbp28668
 */
public class ItemSelectionDialog extends BasicDialog {

    private JList list = new JList();
    
    /**
     * Creates an ItemSelectionDialog
     * @param parent is the parent JDialog.
     * @param title is the dialog title.
     * @param items is the collection of things to select from.
     */
    public ItemSelectionDialog(JDialog parent, String title, Collection items) {
        super(parent, title);
        init(items);
    }

    /**
     * Creates an ItemSelectionDialog
     * @param parent is the parent Component.
     * @param title is the dialog title.
     * @param items is the collection of things to select from.
     */
    public ItemSelectionDialog(Component parent, String title, Collection items) {
        super(parent, title);
        init(items);
    }

    private void init(Collection items){
        
        list.setListData(items.toArray());
        list.setEnabled(true); 
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scroll = new JScrollPane(list);
        getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
		pack();

    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return !list.isSelectionEmpty();
    }

    public Collection getSelectedItems(){
        LinkedList values =  new LinkedList();;
        Collections.addAll(values, list.getSelectedValues());
        return values;
    }
    
    public Set getSelectedSet(){
        HashSet values = new HashSet();
        Collections.addAll(values, list.getSelectedValues());
        return values;
    }
}
