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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.DefaultListModel;
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
public class ItemSelectionDialog<T> extends BasicDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList<T> list = new JList<>();
    
    /**
     * Creates an ItemSelectionDialog
     * @param parent is the parent JDialog.
     * @param title is the dialog title.
     * @param items is the collection of things to select from.
     */
    public ItemSelectionDialog(JDialog parent, String title, Collection<T> items) {
        super(parent, title);
        init(items);
    }

    /**
     * Creates an ItemSelectionDialog
     * @param parent is the parent Component.
     * @param title is the dialog title.
     * @param items is the collection of things to select from.
     */
    public ItemSelectionDialog(Component parent, String title, Collection<T> items) {
        super(parent, title);
        init(items);
    }

	private void init(Collection<T> items){
		DefaultListModel<T> model = new DefaultListModel<>();
		for(T item : items) {
			model.addElement(item);
		}
		list.setModel(model);
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

    public Collection<T> getSelectedItems(){
        LinkedList<T> values =  new LinkedList<>(list.getSelectedValuesList());
        return values;
    }
    
    public Set<T> getSelectedSet(){
        HashSet<T> values = new HashSet<>(list.getSelectedValuesList());
        return values;
    }
}
