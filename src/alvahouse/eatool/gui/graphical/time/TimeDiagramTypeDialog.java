/*
 * TimeDiagramTypeDialog.java
 * Project: EATool
 * Created on 29-Jul-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.ButtonBox;
import alvahouse.eatool.gui.DiagramTypeEditor;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.NamedRepositoryItemPanel;
import alvahouse.eatool.gui.graphical.time.TimeDiagramType.TypeEntry;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.types.TimeSeriesType;

/**
 * TimeDiagramTypeDialog
 * 
 * @author rbp28668
 */
public class TimeDiagramTypeDialog extends BasicDialog implements DiagramTypeEditor {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** TimeDiagramType that's currently being edited */
    private TimeDiagramType type;
    
    /** Panel for common NamedRepositoryItem properties */
    private NamedRepositoryItemPanel nriPanel;
    
    /** Panel for allowable metaEntities/metaProperties */
    private TypePanel typePanel;
    
    /** List of possible metaProperties (those with a property of TimeSeriesType).*/
    private List<MetaProperty> allowableMeta = new LinkedList<MetaProperty>();
    

    /**
     * @param parent
     * @param title
     */
    public TimeDiagramTypeDialog(Component parent) {
        super(parent, "Time Diagram Type");
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.DiagramTypeEditor#init(alvahouse.eatool.gui.graphical.DiagramType, alvahouse.eatool.repository.metamodel.MetaModel, alvahouse.eatool.gui.graphical.DiagramTypes)
     */
    public void init(DiagramType diagramType, MetaModel metaModel, DiagramTypes diagramTypes) throws Exception {
        init((TimeDiagramType)diagramType, metaModel);
    }

    /**
     * @param type
     */
    private void init(TimeDiagramType type,MetaModel metaModel) {
        this.type = type;
        
        // Can only have meta-entities on diagram with time series type properties.
        for(MetaEntity metaEntity : metaModel.getMetaEntities()){
            for(MetaProperty metaProperty : metaEntity.getMetaProperties()){
                if(metaProperty.getMetaPropertyType() instanceof TimeSeriesType){
                    allowableMeta.add(metaProperty);
                    break;
                }
            }
        }
        
        setLayout(new BorderLayout());
        
        nriPanel = new NamedRepositoryItemPanel(type);
        getContentPane().add(nriPanel,BorderLayout.NORTH);
        
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);

        typePanel = new TypePanel(type);
        getContentPane().add(typePanel, BorderLayout.CENTER);
        
        pack();
        
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#onOK()
     */
    protected void onOK() {
        type.setName(nriPanel.getName());
        type.setDescription(nriPanel.getDescription());

        type.clearTargets();
        Collection types = typePanel.getTypes();
        for(Iterator iter = types.iterator(); iter.hasNext();){
            TimeDiagramType.TypeEntry entry = (TimeDiagramType.TypeEntry)iter.next();
            type.addTarget(entry);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.BasicDialog#validateInput()
     */
    protected boolean validateInput() {
        return nriPanel.validateInput() && typePanel.validateInput();
    }

//    /**
//     * This works out which meta-entities are selectable once a given type has been selected.  The only 
//     * selectable meta-entities are those ones which have a meta-property of the given type.  The selectable
//     * meta-entities must be a subset of those which have a property of type TimeSeriesType i.e. those in
//     * the allowableMeta collection.
//     * @param type
//     */
//    private void getSelectableFromType(TimeSeriesType type){
//        
//        selectable = new LinkedList();
//        
//        // Can only have meta-entities on diagram with time series type properties.
//        for(Iterator iter = allowableMeta.iterator(); iter.hasNext();){
//            MetaEntity metaEntity = (MetaEntity)iter.next();
//            for(Iterator iterProps = metaEntity.getMetaProperties().iterator(); iterProps.hasNext();){
//                MetaProperty metaProperty = (MetaProperty)iterProps.next();
//                if(metaProperty.getMetaPropertyType().equals(type)){
//                    selectable.add(metaEntity);
//                    break;
//                }
//            }
//        }
//
//    }
    

    /**
     * @param entry
     */
    void addEntry(TimeDiagramType.TypeEntry entry){
        typePanel.addEntry(entry);
        pack();
    }
    
    /**
     * @param entry
     */
    void removeEntry(TimeDiagramType.TypeEntry entry){
        typePanel.removeEntry(entry);
        pack();
    }

    /**
     * @param entry
     */
    void selectEntry(TypeEntry entry) {
        typePanel.selectEntry(entry);
    }

    /**
     * TypePanel is the full panel displaying colours and the selection list.
     * 
     * @author rbp28668
     */
    private class TypePanel extends JPanel {

        /** List of selected entries */
        private TypesPanel types;
        
        /** Tabbed Pane for all the properties/colours settings */
        private JTabbedPane tabs;

        /** Keep track of the entries by index for tabs */
        private Vector entries = new Vector();

        
        TypePanel(TimeDiagramType type){
            setLayout(new BorderLayout());
            types = new TypesPanel();
            tabs = new JTabbedPane();
            add(tabs, BorderLayout.NORTH);
            add(types, BorderLayout.SOUTH);
            
            for(Iterator iter = type.getTargets().iterator(); iter.hasNext();){
                TimeDiagramType.TypeEntry entry = (TimeDiagramType.TypeEntry)iter.next();
                addEntry(entry);
            }
            
            tabs.addChangeListener( new ChangeListener() {

                public void stateChanged(ChangeEvent arg0) {
                    int idx = tabs.getSelectedIndex();
                    if(idx != -1){
                        TimeDiagramType.TypeEntry entry = (TimeDiagramType.TypeEntry) entries.get(idx);
                        types.selectEntry(entry);
                    }
                }
            });
        }

        /**
         * Selects the tab for the given TypeEntry.
         * @param entry is the TypeEntry to select.
         */
        public void selectEntry(TypeEntry entry) {
            int index = entries.indexOf(entry);
            tabs.setSelectedIndex(index);
        }

        /**
         * Adds a new TypeEntry - a tab is created for this entry.
         * @param entry is the entry to add.
         */
        void addEntry(TimeDiagramType.TypeEntry entry){
            ColourPanel colours = new ColourPanel(entry);
            String title = entry.toString();
            entries.add(entry);
            tabs.addTab(title,colours);
         }
        
        /**
         * Removes a TypeEntry - the tab is deleted.
         * @param entry is the TypeEntry to remove.
         */
        void removeEntry(TimeDiagramType.TypeEntry entry){
            int index = entries.indexOf(entry);
            tabs.removeTabAt(index);
            entries.removeElementAt(index);
        }

        
        /**
         * Gets the collection of TypeEntry as edited on the diagram.
         * @return
         */
        Collection getTypes(){
            for(int i=0; i<entries.size(); ++i){
                TypeEntry entry = (TypeEntry)entries.get(i);
                ColourPanel colourPanel = (ColourPanel)tabs.getComponentAt(i);
                Collection colours = colourPanel.getColours();
                entry.setColours(colours);
            }
            return entries;
        }
        
        /**
         * @return
         */
        protected boolean validateInput() {
            return types.validateInput();
        }

    }
    
    /**
     * ColourPanel displays and allows editing of colours for a time-line.
     * 
     * @author rbp28668
     */
    private class ColourPanel extends JPanel {
 
        private GridBagLayout gridBag = new GridBagLayout();
        private Vector buttons = new Vector();
        private Vector displays = new Vector();
        
        /**
         * 
         */
        ColourPanel(){
            this(null);
        }
        
        /**
         * @param type
         */
        ColourPanel(TimeDiagramType.TypeEntry type){
            super();
            setLayout(gridBag);
        
            if(type != null){
	            TimeSeriesType timeLine = type.getTimeLine();
	            if(timeLine != null){
	                setType(timeLine, type.getColours());
	            }
            }
        }
        
       
        /**
         * @param type
         * @param colours
         */
        void setType(TimeSeriesType type, Vector colours){
            assert(type != null);
            assert(colours != null);
            assert(colours.size() > 1);
            assert(type.getIntervals().size() == colours.size());
            
            removeAll();
        
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = 1;
            c.gridheight = 1;
            c.anchor = GridBagConstraints.LINE_START;
            
            
            int idx=0;
            for(Iterator iter = type.getIntervals().iterator(); iter.hasNext();){
                String interval = (String)iter.next();
                Color colour = (Color)colours.get(idx);
                
                JLabel label = new JLabel(interval);
                c.gridwidth = 1;
                gridBag.setConstraints(label,c);
                add(label);
                
                ColourDisplay display = new ColourDisplay();
                display.setColour(colour);
                gridBag.setConstraints(display,c);
                add(display);
                displays.add(idx, display);
                
                JButton change = new JButton("Change");
        		change.addActionListener( new ActionListener() {

        			public void actionPerformed(ActionEvent e) {
        			    Object source = e.getSource();
        			    int idx = buttons.indexOf(source);
        			    assert(idx >=0);
        			    
    				    ColourDisplay display = (ColourDisplay)displays.get(idx);
        				Color colour = JColorChooser.showDialog(ColourPanel.this,"Interval Colour", display.getColour());
        				if(colour != null){
        					display.setColour(colour);
        				}
        				
        			}
        		});
        		
        		c.gridwidth = GridBagConstraints.REMAINDER;
                gridBag.setConstraints(change,c);
                add(change);
                buttons.add(idx,change);
        		
        		++idx;
            }
        }
        
        Collection getColours(){
            List colours = new LinkedList();
            for(int i=0; i<displays.size(); ++i){
                ColourDisplay display = (ColourDisplay)displays.get(i);
                colours.add(display.getColour());
            }
            return colours;
        }
    }
    
	/**
	 * ColourDisplay provides a single colour box.
	 * @author Bruce.Porteous
	 *
	 */
	private static class ColourDisplay extends JLabel{
		
		/**
		 * 
		 */
		ColourDisplay(){
			setOpaque(true);
			setPreferredSize(new Dimension(20,20));
		}
		
		/**
		 * @param colour
		 */
		void setColour(Color colour){
			setBackground(colour);
		}
		
		/**
		 * @return
		 */
		Color getColour(){
			return getBackground();
		}

	}

    /**
     * TypesPanel maintains a list of the allowed types allowing you to add, edit
     * and delete the different entries.
     * 
     * @author rbp28668
     */
    private class TypesPanel extends JPanel {
        
        private JList list = new JList();
        private JButton addButton = new JButton("Add");
        private JButton delButton = new JButton("Delete");
        
        private DefaultListModel listModel = new DefaultListModel();

        
        TypesPanel(){

            Collection types = type.getTargets();
            for(Iterator iter = types.iterator(); iter.hasNext();){
                TimeDiagramType.TypeEntry entry = (TimeDiagramType.TypeEntry)iter.next();
                listModel.addElement(entry);
            }
       

	        GridBagLayout layout = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        c.fill = GridBagConstraints.BOTH;
	        c.gridheight = 1;
	        c.gridwidth = 1;
	        c.weightx = 1.0f;
	        c.insets = new Insets(20,10,20,10);
	        c.anchor = GridBagConstraints.LINE_START;
	        
            setLayout(layout);
            
 	        list.setModel(listModel);
	        list.setVisibleRowCount(6);
	        
	        c.gridwidth = 1;
	        c.weightx = 0.1;
	        list.setBorder(BorderFactory.createLineBorder(Color.black));
	        layout.setConstraints(list,c);
	        add(list);

	        list.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent event) {
                    enableButtons();
                    int idx = list.getSelectedIndex();
                    if(idx != -1){
                        TimeDiagramType.TypeEntry entry = (TimeDiagramType.TypeEntry)listModel.get(idx);
                        TimeDiagramTypeDialog.this.selectEntry(entry);
                    }
                }
	        });
	        
	        ButtonBox box = new ButtonBox();
	        addButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                     MetaEntity selected = (MetaEntity)Dialogs.selectNRIFrom(getSelectableEntities(),"Select Entity", TimeDiagramTypeDialog.this);
                    if(selected != null){
                        
                        List possibleProperties = getPossibleProperties(selected);
                        
                        assert(possibleProperties.size() > 0);
                        
                        MetaProperty selectedProperty = (MetaProperty)possibleProperties.get(0);
                        if(possibleProperties.size() > 1){
                            selectedProperty = (MetaProperty)Dialogs.selectNRIFrom(possibleProperties,"Select Property", TimeDiagramTypeDialog.this);
                        }
                        if(selectedProperty != null){
                            TimeDiagramType.TypeEntry entry = new TimeDiagramType.TypeEntry(selected,selectedProperty);
                            entry.setColours(defaultColoursFor((TimeSeriesType)selectedProperty.getMetaPropertyType()));
                            listModel.addElement(entry);
                            addEntry(entry);
                            enableButtons();
                        }
                    }
                }
	        });
	        box.add(addButton);

	        delButton.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent arg0) {
                    int index = list.getSelectedIndex();
                    if(index != -1){
                        TimeDiagramType.TypeEntry value = (TimeDiagramType.TypeEntry)listModel.elementAt(index);
                        listModel.remove(index);
                        removeEntry(value);
                        enableButtons();
                    }
                }
	        });
	        box.add(delButton);
	        
	        
	        c.fill = GridBagConstraints.VERTICAL;
	        c.gridheight = 1;
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        c.anchor = GridBagConstraints.LINE_END;
	        c.weightx = 0.0;
	        
	        layout.setConstraints(box,c);
	        add(box);
	        
	        enableButtons();
        }

        /**
         * Get a list of MetaEntities that can be selected from. This uses the base
         * allowableMeta list of MetaProperties but removes any that are already selected.
         * The resulting list provides a set of parent MetaEntities.
         * @return Selectable meta-entities.
         */
        Collection getSelectableEntities(){
            List selectable = new LinkedList();
            
            // Create a set of used MetaProperty to exclude them from the list.
            Set used = new HashSet();
            for(int i=0; i< listModel.size(); ++i){
                TypeEntry entry = (TypeEntry)listModel.elementAt(i);
                used.add(entry.getTargetProperty());
            }
            
            for(Iterator iter = allowableMeta.iterator(); iter.hasNext();){
                MetaProperty meta = (MetaProperty)iter.next();
                if(!used.contains(meta)){
	                MetaEntity metaEntity = (MetaEntity)meta.getContainer();
	                if(!selectable.contains(metaEntity)){
	                    selectable.add(metaEntity);
	                }
                }
            }
            return selectable;
        }
        
        /**
         * For a given metaEntity get the time series meta properties excluding any
         * that have already been selected for use. 
         * @param selected  is the selected MetaEntity.
         * @return allowable meta-properties for the selected MetaEntity.
         */
        private List<MetaProperty> getPossibleProperties(MetaEntity selected) {
            
            // Create a set of used MetaProperty to exclude them from the list.
            Set used = new HashSet();
            for(int i=0; i< listModel.size(); ++i){
                TypeEntry entry = (TypeEntry)listModel.elementAt(i);
                used.add(entry.getTargetProperty());
            }
            
            List possibleProperties = new LinkedList();
            
            for(Iterator iterProps = selected.getMetaProperties().iterator(); iterProps.hasNext();){
                MetaProperty metaProperty = (MetaProperty)iterProps.next();
                if(metaProperty.getMetaPropertyType() instanceof TimeSeriesType){
                    if(!used.contains(metaProperty)){
                        possibleProperties.add(metaProperty);
                    }
                }
            }
            return possibleProperties;
        }
        
        
        /**
         * @param entry
         */
        public void selectEntry(TypeEntry entry) {
            list.setSelectedValue(entry,true); // true -> scroll if necessary
        }

        /**
         * @param type
         */
        Collection defaultColoursFor(TimeSeriesType type){
            float h = 0.0f;
            float s = 1.0f; // Saturated colours
            float b = 1.0f;
            
            Collection intervals = type.getIntervals();
            Vector colours = new Vector(intervals.size());
            
            float dh = 1.0f / (1.0f + intervals.size());
            
            for(int i=0; i<intervals.size(); ++i){
                colours.add(i,Color.getHSBColor(h, s, b));
                h += dh;
            }
            
            return colours;
        }

        /**
         * 
         */
        private void enableButtons(){
            int idx = list.getSelectedIndex();
            delButton.setEnabled(idx != -1);
        }

        
        /**
         * Determines whether a valid selection has been made.
         * @return true if valid.
         */
        boolean validateInput(){
            return listModel.size() > 0;
        }
    }

}