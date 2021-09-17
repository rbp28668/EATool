/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import alvahouse.eatool.Main;

/**
 * Class that allows multiple selection of items from a list of allowable ones. 
 * For example select a number of users from a list of known users or select a
 * number of properties from a list of properties.
 * @author bruce_porteous
 *
 * @param <T>
 */
public class SelectItemsPanel<T> extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultListModel<T> excludedListModel = new DefaultListModel<>();
	private DefaultListModel<T> includedListModel = new DefaultListModel<>();
	private JList<T> excludedList = new JList<>(excludedListModel);
	private JList<T> includedList = new JList<>(includedListModel);
	private JButton include = new JButton(">>>");
	private JButton exclude = new JButton("<<<");
	private JButton removeAll = new JButton("Remove All");
	
	public SelectItemsPanel(List<T> allowedItems, List<T> selectedItems) {

		setLayout(new BorderLayout());
		setBorder(EditCouchSecurityDialog.componentBorder);
		
		excludedList.setBorder(EditCouchSecurityDialog.componentBorder);
		includedList.setBorder(EditCouchSecurityDialog.componentBorder);
		excludedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		includedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		
		TwinnedScrollPane tsExcluded = new TwinnedScrollPane(excludedList);
		TwinnedScrollPane tsIncluded = new TwinnedScrollPane(includedList);
		tsExcluded.setTwin(tsIncluded);
		tsIncluded.setTwin(tsExcluded);
		
		add(tsExcluded, BorderLayout.WEST);
		add(tsIncluded, BorderLayout.EAST);
		
		ButtonBox box = new ButtonBox();
		
		box.add(include);
		box.add(exclude);
		box.add(removeAll);
		box.add(Box.createVerticalGlue()); // center buttons
		add(box, BorderLayout.CENTER);
		box.setBorder(EditCouchSecurityDialog.componentBorder);
		
		for(T item : allowedItems) {
			if(selectedItems.contains(item)) {
				includedListModel.addElement(item);
			} else {
				excludedListModel.addElement(item);
			}
		}

		include.setEnabled(false);
		exclude.setEnabled(false);
		removeAll.setEnabled(includedListModel.getSize()>0);
					
        include.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	int idx = excludedList.getSelectedIndex();
            	if(idx != -1) {
            		T item = excludedListModel.remove(idx);
            		includedListModel.addElement(item);
            		include.setEnabled(false); // as no selection now
            		removeAll.setEnabled(true); // as must be something to remove
            	}
            }
        });

        exclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	int idx = includedList.getSelectedIndex();
            	if(idx != -1) {
            		T item = includedListModel.remove(idx);
            		excludedListModel.addElement(item);
            		exclude.setEnabled(false);
            		removeAll.setEnabled(includedListModel.getSize()>0);
            	}
            }
        });

        removeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	includedListModel.clear();
            	excludedListModel.clear();
            	
            	try {
					for(T item : allowedItems) {
						excludedListModel.addElement(item);
		            }
            	} catch (Exception e) {
            		new ExceptionDisplay(Main.getAppFrame(), e);
            	}
            }
        });

		includedList.addListSelectionListener(new javax.swing.event.ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				int idx = includedList.getSelectedIndex();
				exclude.setEnabled(idx != -1);
				removeAll.setEnabled(includedListModel.getSize()>0);
			}
		});
		excludedList.addListSelectionListener(new javax.swing.event.ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				int idx = excludedList.getSelectedIndex();
				include.setEnabled(idx != -1);
			}
		});
	}
	
	Collection<T> getSelectedItems() {
		List<T> items = new LinkedList<>();
		for(int i=0; i<includedListModel.size(); ++i) {
			items.add(includedListModel.get(i));
		}
		return items;
	}
	
	/**
	 * TwinnedScrollPane extends JScrollPane so that 2 scroll panes
	 * can be twinned such that their preferred sizes will always
	 * be equal.
	 * @author bruce.porteous
	 *
	 */	
	protected class TwinnedScrollPane extends JScrollPane {
		
		private static final long serialVersionUID = 1L;

		/** Twin of this scroll pane */
		private TwinnedScrollPane twin = null;
		
		/**
		 * Constructor for TwinnedScrollPane sets up the
		 * contents of the pane.
		 * @param view is the component to be scrolled.
		 */
		public TwinnedScrollPane(Component view) {
			super(view);
		}
		
		/**
		 * setTwin sets this TwinnedScrollPane's counterpart
		 * pane such as their sizes will be synchronised.
		 * @param twin is the counterpart TwinnedScrollPane.
		 */
		public void setTwin(TwinnedScrollPane twin) {
			this.twin = twin;
		}
		
		/** getPreferredSize gets the preferred size of this 
		 * component taking into account the basic preferred
		 * size if its twin.
		 * @see java.awt.Component#getPreferredSize()
		 */
		public Dimension getPreferredSize() {
			Dimension d = getSuperPreferredSize();
			if(twin != null){
				Dimension dTwin = twin.getSuperPreferredSize();
				if(dTwin.width > d.width) d.width = dTwin.width;
				if(dTwin.height > d.height) d.height = dTwin.height;
			}
			return d;
		}
		
		/**
		 * getSuperPreferredSize gets the preferred size of the
		 * underlying JScrollPane without taking into account
		 * any twinning.
		 * @return Dimension - the preferred size.
		 */
		private Dimension getSuperPreferredSize() {
			return super.getPreferredSize();
		}
	}

	
}