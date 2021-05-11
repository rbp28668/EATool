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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import alvahouse.eatool.Main;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaProperty;

/**
 * MetaEntityDisplayHintEditor is an editor dialog for editing the
 * meta entity display hints.  The display hints determine which
 * fields should be used when a textual representation of an entity
 * is needed.  The editor selects those fields.
 * @author bruce.porteous
 */
public class MetaEntityDisplayHintEditor extends BasicDialog {

	private static final long serialVersionUID = 1L;
	private MetaEntityDisplayHint originalHint;
	private MetaEntity metaEntity;
	private HintsPanel hintsPanel;
	/**
	 * Constructor for MetaEntityDisplayHintEditor.
	 * @param parent
	 * @param title
	 */
	public MetaEntityDisplayHintEditor(Component parent, MetaEntityDisplayHint dh, MetaEntity me) throws Exception{
        super(parent ,"Edit Display Hint");
        
        if(dh == null) {
            throw new NullPointerException("Can't edit a null MetaEntityDisplayHint");
        }
        if(me == null) {
            throw new NullPointerException("Can't edit display hint for null MetaEntity");
        }

		metaEntity = me;
        originalHint = dh;
        
        JLabel label = new JLabel("Hint for " + metaEntity.getName());
        label.setBorder(componentBorder);
        getContentPane().add(label, BorderLayout.NORTH);
        
        hintsPanel = new HintsPanel(dh);
        getContentPane().add(hintsPanel, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
	 */
	protected void onOK() {
		originalHint.clearPropertyKeys();
		Collection<MetaProperty> properties = hintsPanel.getIncludedProperties();
		for (MetaProperty element : properties) {
			originalHint.addPropertyKey(element.getKey());
		}
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
	 * In this case, any combination of fields is valid so
	 * always return true.
	 */
	protected boolean validateInput() {
		return true;
	}

	private class HintsPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private MetaEntityDisplayHint displayHint;
		private DefaultListModel<MetaProperty> excludedListModel = new DefaultListModel<MetaProperty>();
		private DefaultListModel<MetaProperty> includedListModel = new DefaultListModel<MetaProperty>();
		private JList<MetaProperty> excludedList = new JList<MetaProperty>(excludedListModel);
		private JList<MetaProperty> includedList = new JList<MetaProperty>(includedListModel);
		private JButton include = new JButton(">>>");
		private JButton exclude = new JButton("<<<");
		private JButton removeAll = new JButton("Remove All");
		
		public HintsPanel(MetaEntityDisplayHint dh) throws Exception {
			displayHint = dh;

			setLayout(new BorderLayout());
			setBorder(componentBorder);
			
			excludedList.setBorder(componentBorder);
			includedList.setBorder(componentBorder);
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
			box.setBorder(componentBorder);
			
			for(MetaProperty mp : metaEntity.getMetaProperties()) {
				if(displayHint.referencesProperty(mp)) {
					includedListModel.addElement(mp);
				} else {
					excludedListModel.addElement(mp);
				}
			}

			include.setEnabled(false);
			exclude.setEnabled(false);
			removeAll.setEnabled(includedListModel.getSize()>0);
						
	        include.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	int idx = excludedList.getSelectedIndex();
	            	if(idx != -1) {
	            		MetaProperty omp = excludedListModel.remove(idx);
	            		includedListModel.addElement(omp);
	            		include.setEnabled(false); // as no selection now
	            		removeAll.setEnabled(true); // as must be something to remove
	            	}
	            }
	        });

	        exclude.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	int idx = includedList.getSelectedIndex();
	            	if(idx != -1) {
	            		MetaProperty omp = includedListModel.remove(idx);
	            		excludedListModel.addElement(omp);
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
						for(MetaProperty mp: metaEntity.getMetaProperties()) {
							excludedListModel.addElement(mp);
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
		
		/**
		 * getIncludedProperties gets the list of included MetaProperties from the included
		 * list box.
		 * @return a collection of included properties, never null, may be empty.
		 */
		Collection<MetaProperty> getIncludedProperties() {
			List<MetaProperty> properties = new LinkedList<MetaProperty>();
			for(int i=0; i<includedListModel.size(); ++i) {
				properties.add(includedListModel.get(i));
			}
			return properties;
		}
		
	}
	
	/**
	 * TwinnedScrollPane extends JScrollPane so that 2 scroll panes
	 * can be twinned such that their preferred sizes will always
	 * be equal.
	 * @author bruce.porteous
	 *
	 */	
	private class TwinnedScrollPane extends JScrollPane {
		
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
