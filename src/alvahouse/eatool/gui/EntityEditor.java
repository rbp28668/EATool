 /*
 * EntityEditor.java
 *
 * Created on 14 March 2002, 22:09
 */

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;

/**
 * EntityEditor provides editing for a single Entity.  A cloned copy is edited
 * and only copied back to the original entity if the user clicks on OK.  The
 * actual editing is done within the inner classes, {@link RelationshipPanel} and
 * {@link PropertiesPanel}.  In turn the panels use <code>JTable</code> which handles the
 * actual editing via further inner classes, subclasses of <code>AbstractTableModel</code>:
 * <code>PropertiesTableModel</code> and <code>RelationshipsTableModel</code>
 * @author  rbp28668
 */
public class EntityEditor  extends BasicDialog {

   private static final long serialVersionUID = 1L;

    /** The original entity to be edited */
    private Entity originalEntity;
    
    /** A cloned copy that is edited - it is copied back over the
     * original if the user clicks on OK */
    //private Entity editCopy;
    
    /** Panel for handling property edit - presented in the properties tab */
    private PropertiesPanel propertiesPanel;

    /** Panel for handling relationship edit - presented in the relationships tab */
    private RelationshipsPanel relationshipsPanel;
    
    /** Repository to contain associated relationships */
    private Repository repository;

    /** Creates new form EntityEditor 
     * @param parent is the parent Component that created this dialog
     * @param e, Entity is the entity to be edited
     */
    public EntityEditor(Component parent, Entity e, Repository repository) {
        super(parent ,"Edit Entity");

        originalEntity = e;
        this.repository = repository;
        //editCopy = (Entity)e.clone();
        
        JLabel label = new JLabel("uuid: " + e.getKey().toString());
        label.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        getContentPane().add(label, BorderLayout.NORTH);
        
        propertiesPanel = new PropertiesPanel(e);
        relationshipsPanel = new RelationshipsPanel(e);
        
        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP);
        tabs.addTab("Properties",propertiesPanel);
        tabs.addTab("Relationships",relationshipsPanel);
        getContentPane().add(tabs, BorderLayout.CENTER);
        getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
        pack();
    }

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
	 */
    protected void onOK() {
    	propertiesPanel.onOK(); // updates original entity.
    	relationshipsPanel.onOK();
    }
    
	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
	 */
    protected boolean validateInput() {
        return propertiesPanel.validateInput() && relationshipsPanel.validateInput();    
    }
   
    /*=================================================================*/
    // PropertiesPanel is the edit panel for the entity's properties
    /*=================================================================*/
    private class PropertiesPanel extends JPanel{
        
        private static final long serialVersionUID = 1L;
        private Map<Property,Component> components = new HashMap<Property,Component>();

        /**
		 * Creates a new PropertiesPanel and initialises the contained
		 * JTable & PropertiesTableModel that provides the display and 
		 * editing of the entity.
		 * @param e is the Entity to be edited.
		 */
        PropertiesPanel(Entity e) {
            
            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            GridBagConstraints c = new GridBagConstraints();
            c.gridheight = 1;
            c.gridwidth = 1;
            c.weightx = 1.0f;
            c.weighty = 0.0f;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.LINE_START;
            c.ipadx = 10;
            c.insets = new Insets(5,2,5,2);

            
            setBorder(new StandardBorder());
            
            MetaEntity metaEntity = e.getMeta();
            Component firstEdit = null;
            for(MetaProperty mp : metaEntity.getMetaProperties()){
                
                c.gridwidth = 1; // start of row.
                c.fill = GridBagConstraints.HORIZONTAL;
                
                
                JLabel name = new JLabel(mp.getName());
                name.setToolTipText(mp.getDescription());
                layout.setConstraints(name,c);
                add(name);

                JLabel type = new JLabel("(" + mp.getMetaPropertyType().getTypeName() + ")");
                type.setToolTipText(mp.getDescription());
                layout.setConstraints(type,c);
                add(type);
                
                Property p = e.getPropertyByMeta(mp.getKey());
                Component component;
                if(mp.isReadOnly()) {
                    component = mp.getMetaPropertyType().getRenderer(p.getValue());
                } else {
                    component = mp.getMetaPropertyType().getEditor(p.getValue());
                    if(firstEdit == null){
                        firstEdit = component;
                    }
                }
                c.fill = GridBagConstraints.NONE;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                layout.setConstraints(component,c);
                add(component);
                
                components.put(p,component);
            }
            c.gridwidth = 1;
            c.weighty = 1.0f;
            JLabel padding = new JLabel();
            layout.setConstraints(padding,c);
            add(padding);
            
            if(firstEdit != null){
                firstEdit.requestFocus();
            }
        }
        
		/**
		 * Method onOK - called when the OK button is clicked
		 *  to tidy up any current edit.
		 */
        void onOK() {
            for(Map.Entry<Property, Component> entry: components.entrySet()){
                Property p = entry.getKey();
                Component c = entry.getValue();
                MetaProperty meta = p.getMeta();
                if(!meta.isReadOnly()){
                    String value = meta.getMetaPropertyType().getEditValue(c);
                    p.setValue(value);
                }
            }
        }
        
        /**
         * Validates the properties in the model.  If invalid a message is displayed
         * and the offending row highlighted.
         * @return true if valid, false if not.
         */
        public boolean validateInput(){
            boolean valid = true;
            for(Map.Entry<Property, Component> entry: components.entrySet()){
                Property p = entry.getKey();
                Component c = entry.getValue();
                MetaProperty meta = p.getMeta();
                
                String value = meta.getMetaPropertyType().getEditValue(c);
                if(value.trim().length() == 0){
                    if(meta.isMandatory()){
	                    Dialogs.warning(this,"Must supply a value for " + meta.getName());
	                    c.requestFocusInWindow();
	                    valid = false;
	                    break;
	                }
                } else {
	                MetaPropertyType type = meta.getMetaPropertyType();
	                try {
	                    type.validate(value);
	                } catch (Exception e){
	                    Dialogs.warning(this,"Value " + value + " is not a valid " + type.getTypeName());
	                    c.requestFocusInWindow();
	                    valid = false;
	                    break;
	                }
                }
            }
            return valid;
        }

    }

    /*=================================================================*/
    // RelationshipsPanel is the editing panel for the Entity's 
    // relationships.
    /*=================================================================*/
    private class RelationshipsPanel extends JPanel{
 
        private static final long serialVersionUID = 1L;
        private JTable table;
        private List<Relationship> relationshipsToAdd = new LinkedList<Relationship>();
        private List<Relationship> relationshipsToDelete = new LinkedList<Relationship>();
        private RelationshipsTableModel tableModel;
		/**
		 * Method RelationshipsPanel.
		 * @param e
		 */
        RelationshipsPanel(Entity e) {
            setLayout(new BorderLayout());

            tableModel = new RelationshipsTableModel(e);
            table = new JTable(tableModel);
            table.setRowSelectionAllowed(true);
            table.setColumnSelectionAllowed(false);
            
            JScrollPane scroll = new JScrollPane(table);
            add(scroll,BorderLayout.CENTER);
            
            JButton addNewButton = new JButton("Add New");
            JButton deleteSelectedButton = new JButton("Delete Selected");
            
            Box box = Box.createHorizontalBox();
            box.add(Box.createHorizontalGlue());
            box.add(addNewButton);
            box.add(Box.createHorizontalStrut(20));
            box.add(deleteSelectedButton);
            box.add(Box.createHorizontalGlue());
            
            add(box,BorderLayout.SOUTH);
            
            addNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    addNewRelationship();
                }
            });
            deleteSelectedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    deleteSelectedRelationships();
                }
            });
         }

        /**
         * Adds a new relationship to this entity.
         */
        private void addNewRelationship(){
            // For this type of entity, need to get list of MetaRelationships that
            // describe the relationships that can connect to this.
            MetaEntity me = originalEntity.getMeta();
            MetaRelationship mr = Dialogs.selectMetaRelationshipFor(me,this, repository);
            if(mr != null) {
                // Now we need to pick up the allowable Entities at the "other end" of
                // the relationship.
                MetaEntity otherMetaEntity = null;
                MetaRole thisMetaRole = null;
                MetaRole otherMetaRole = null;
                boolean existingIsStart = true;

                // Order of these 2 if statements - if either end is valid then default to start end.
                if(mr.finish().connectsTo().equals(me)){
                    otherMetaEntity = mr.start().connectsTo();
                    thisMetaRole = mr.finish();
                    otherMetaRole = mr.start();
                    existingIsStart = false;
                }
                if(mr.start().connectsTo().equals(me)){
                    otherMetaEntity = mr.finish().connectsTo();
                    thisMetaRole = mr.start();
                    otherMetaRole = mr.finish();
                    existingIsStart = true;
                }
                
                if(otherMetaEntity == null){
                    throw new IllegalStateException("Can't find other entity type when adding new relationship");
                }
                
                Entity other = Dialogs.selectEntityOf(otherMetaEntity,this, repository);
                if(other != null) {
                    Relationship r = new Relationship(mr);
                    
                    Role thisEnd = new Role(thisMetaRole);
                    thisEnd.setConnection(originalEntity);
                    thisEnd.setRelationship(r);
                    
                    Role otherEnd = new Role(otherMetaRole);
                    otherEnd.setConnection(other);
                    otherEnd.setRelationship(r);
                    
                    if(existingIsStart){
                        r.setStart(thisEnd);
                        r.setFinish(otherEnd);
                    } else {
                        r.setStart(otherEnd);
                        r.setFinish(thisEnd);
                    }
                    
                    //System.out.println("New Relationship for " + editCopy + " is " + r);
                    
                    relationshipsToAdd.add(r);
                    tableModel.add(r);
                }
            }
        }
        
        /**
         * Removes any selected relationships from the grid and puts them in
         * a delete list.
         */
        private void deleteSelectedRelationships(){
            if(table.getSelectedRowCount() > 0){
                int[] selected = table.getSelectedRows();
                Relationship[] toRemove = new Relationship[selected.length];
                for(int i=0; i<selected.length; ++i){
                    Relationship r = tableModel.getRow(selected[i]);
                    relationshipsToDelete.add(r);
                    toRemove[i] = r;
                }
                for(int i=0; i<toRemove.length; ++i){
                    tableModel.remove(toRemove[i]);
                }
            }
        }
        
        /**
         * Check that the relationships in the model will still be valid when
         * the changes are applied.
         * @return true if valid, false if not.
         */
        protected boolean validateInput(){
            boolean valid = true;
            normaliseRelationships();
            
            Map<RelationshipKey,Count> track = new HashMap<RelationshipKey,Count>();
            // Check each relationship we're going to add 
            for(Relationship r : relationshipsToAdd){
                //Entity startEntity = r.start().connectsTo();
                Count count = getCount(track, r, true); 
                count.inc(); // another relationship of this type for the entity.
                
                //Entity finishEntity = r.start().connectsTo();
                count = getCount(track, r, false); 
                count.inc(); // another relationship of this type for the entity.
            }

            // Check each relationship we're going to delete 
            for(Relationship r : relationshipsToDelete){
                //Entity startEntity = r.start().connectsTo();
                Count count = getCount(track, r, true); 
                count.dec(); // one less relationship of this type for the entity.
                
                //Entity finishEntity = r.start().connectsTo();
                count = getCount(track, r, false); 
                count.dec(); // one less relationship of this type for the entity.
            }
            
            List<String> faults = new LinkedList<String>();
            for(Map.Entry<RelationshipKey,Count>entry : track.entrySet()){
                RelationshipKey key = (RelationshipKey)entry.getKey();
                Count count = (Count)entry.getValue();
                
                if(!key.getMultiplicity().isValid(count.getCount())){
                    String fault = "Entity " + key.getEntity() + " has " + count.getCount() + " of " 
                    	+ key.getMetaRelationship().getName() + " but should be " + key.getMultiplicity().toString(); 
                    faults.add(fault);
                    valid = false;
                }
            }
            if(faults.size()>0){
         	    JList<String> list = new JList<String>();
                list.setListData(faults.toArray(new String[faults.size()]));
                list.setEnabled(false); // no point selecting items.
                JScrollPane scroll = new JScrollPane(list);
        	    JOptionPane.showMessageDialog(this,scroll,"Invalid Relationship(s)", JOptionPane.WARNING_MESSAGE);
            }
            return valid;
        }

        /**
         * Helper method to set up a count for this given entity, relationship
         * type and end (identified by MetaRole).
         * @param track is the Map to track all the counts.
         * @param r is the relationship to check.
         * @param startEntity is true if the entity is at the start end of the relationship,
         * otherwise it should be false.
         * @return A Count for this relationship type, entity and direction.
         */
        private Count getCount(Map<RelationshipKey,Count> track, Relationship r, boolean isStart) {
            Entity e = (isStart) ? r.start().connectsTo() : r.finish().connectsTo();
            MetaRole end = (isStart) ? r.finish().getMeta() : r.start().getMeta();
            
            RelationshipKey key = new RelationshipKey(e,r.getMeta(), end);
            Count count = (Count)track.get(key);
            if(count == null){
                if(e.getModel() == null){
                    count = new Count();
                } else {
	                Collection<Relationship> startRelationships = e.getConnectedRelationshipsOf(r.getMeta());
	                count = new Count(startRelationships.size());
                }
                //System.out.println("Created new tracking record " + key + " with count " + count.getCount());
                track.put(key,count);
            }
            return count;
        }

        /**
		 * Method onOK. Updates the model from the add/delete relationship lists.
		 * Note that this assumes that the optionality/cardinality constraints won't 
		 * be invalidated.  This should have been checked with validateInput().
		 */
        void onOK() {
            
            // First make sure any relationships in both add and delete lists
            // are removed from both (i.e. user changed his mind).
            normaliseRelationships();
            
            Model model = originalEntity.getModel();
            for(Relationship toAdd : relationshipsToAdd){
                model.addRelationship(toAdd);
            }
            for(Relationship toDelete : relationshipsToDelete){
                model.deleteRelationship(toDelete.getKey());
            }
        }

        /**
         * This removes any relationships that are in both the "to add" and in the
         * "to delete" lists.  No point adding and immediately deleting a relationship!
         */
        private void normaliseRelationships() {
            List<Relationship> tempDelete = new LinkedList<Relationship>();
            for(Relationship toDelete : relationshipsToDelete){
                if(relationshipsToAdd.contains(toDelete)){
                    relationshipsToAdd.remove(toDelete);
                } else {
                    tempDelete.add(toDelete);
                }
            }
            relationshipsToDelete = tempDelete;
        }
    }
    
        
     
    /*=================================================================*/
    // RelationshipsTableModel provides a display of the relationships
    // between this entity and other entities.
    /*=================================================================*/
    private static class RelationshipsTableModel extends AbstractTableModel {
        
        private static final long serialVersionUID = 1L;
        private Entity entity;
        private Vector<Relationship> relationships;
        private static String[] headers = {"This Role", "Relationship", "Other Role", "To"};
		
		/**
		 * Method RelationshipsTableModel.
		 * @param e
		 */
        RelationshipsTableModel(Entity e) {
            entity = e;
            if(e.getModel() == null) {
                relationships = new Vector<Relationship>(); // empty
            } else {
	            Set<Relationship> rs = e.getConnectedRelationships();
	            relationships = new Vector<Relationship>(rs);
            }
        }
        
		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
        public int getRowCount() { 
            return relationships.size();
        }
        
		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
        public int getColumnCount() { 
           return headers.length; 
        } 
        
		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
        public Object getValueAt( int row, int col ) {
            if(row < 0 || row >= relationships.size()) {
            	throw new IllegalArgumentException("Row out of range in relationship table model");
            }

            String val = "";
            
            Relationship r = (Relationship)relationships.get(row);
            Role near;
            Role far;
            if(r.start().connectsTo().equals(entity)) {
            	near = r.start();
            	far = r.finish();
            } else {
            	near = r.finish();
            	far = r.start();
            }

            switch(col) {
            	case 0:
					val = near.getMeta().getName();            		
            		break;
            	case 1:
            		val = r.getMeta().getName();
            		break;
            	case 2:
            		val = far.getMeta().getName();
            		break;
            	case 3:
            		val = far.connectsTo().toString();
            		break;
            }
            return val;
        }
        
		/**
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
        public String getColumnName(int c) {
            return headers[c];
        }
        
        /**
         * Adds a new relationship to the table
         * @param r is the relationship to add.
         */
        void add(Relationship r){
            int idx = relationships.size();
            relationships.add(r);
            fireTableRowsInserted(idx,idx);
        }
        
        /**
         * Removes a relationship from the table.
         * @param r is the relationship to remove.
         */
        void remove(Relationship r){
            int idx = relationships.indexOf(r);
            if(idx >= 0){
                relationships.remove(idx);
                fireTableRowsDeleted(idx,idx);
            }
        }
        
        /**
         * Get the Relationship for a given row.
         * @param idx is the row to get the relationship for.
         * @return the Relationship for the idx-th row.
         */
        Relationship getRow(int idx){
            return (Relationship)relationships.get(idx);
        }
    }
    
    /**
     * RelationshipKey is a class used to form a composite key for a map based
     * on an Entity, a MetaRelationship and a MetaRole. Note that the MetaRole 
     * should be at the far end of the relationship from the entity so that it
     * can be used to check the multiplicity.
     * 
     * @author rbp28668
     */
    private static class RelationshipKey {
        private Entity e;
        private MetaRelationship mr;
        private MetaRole end;
        
        RelationshipKey(Entity e, MetaRelationship mr, MetaRole end){
            this.e  = e;
            this.mr = mr;
            this.end = end;
        }
        
        public Multiplicity getMultiplicity() {
            return end.getMultiplicity();
        }
        
        public MetaRelationship getMetaRelationship(){
            return mr;
        }
        
        public Entity getEntity(){
            return e;
        }
        
        public int hashCode(){
            return 53 * e.hashCode() + 27 * mr.hashCode() + end.hashCode();
        }
        
        public boolean equals(Object o){
            if(o instanceof RelationshipKey) {
                RelationshipKey other = (RelationshipKey) o;
                return e.equals(other.e) && mr.equals(other.mr) && end.equals(other.end);
            }
            return false;
        }
        
        public String toString(){
            return e.toString() + "-" + mr.toString() + "-" + end.toString();
        }
    }

    /**
     * Count is a simple integer counter, similar to Integer, but mutable.
     * 
     * @author rbp28668
     */
    private static class Count {
        private int count;
        
        public Count() {
            this(0);
        }
        
        public Count(int initialValue){
            count = initialValue;
            //System.out.println("Initialising count to " + count);
        }
 
        public void inc() {
            ++count;
        }
        
        public void dec() {
            --count;
        }
        
        /**
         * @return Returns the count.
         */
        public int getCount() {
            return count;
        }
//        /**
//         * @param count The count to set.
//         */
//        public void setCount(int count) {
//            this.count = count;
//        }
    }
}
