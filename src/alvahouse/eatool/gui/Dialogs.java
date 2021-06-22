/*
 * Dialogs.java
 * Project: EATool
 * Created on 28-Nov-2005
 *
 */
package alvahouse.eatool.gui;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.NamedItem;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;

/**
 * Dialogs provides helper methods for common dialogs.
 * 
 * @author rbp28668
 */
public abstract class Dialogs {

    /**
     * Select a MetaRelationship from all MetaRelationships defined in the metamodel.
     * @param frame is the parent frame for the dialog.
     * @return the selected MetaRelationship or null if none selected.
     */
    public static MetaRelationship selectMetaRelationship(JInternalFrame frame,Repository repository) throws Exception{
        MetaRelationship[] options = repository.getMetaModel().getMetaRelationshipsAsArray();
        MetaRelationship meta = null;
        if(options.length == 1){
            meta = options[0];
        } else if(options.length > 1) {
	        Arrays.sort(options, new NameComparator());
	        meta = (MetaRelationship)JOptionPane.showInputDialog(
	        	frame,  "Select Relationship Type", "EATool",
	        	JOptionPane.QUESTION_MESSAGE, null,
	        	options, null
	        );
        }
        return meta;
    }

    /**
     * Select a MetaRelationship from MetaRelationships that connect to a given MetaEntity.
     * Used when we have an Entity and want to create a new Relationship to it.
     * @param me is the MetaEntity we wish to select a MetaRelationship for.
     * @param frame is the parent frame for the dialog.
     * @return the selected MetaRelationship or null if none selected.
     */
    public static MetaRelationship selectMetaRelationshipFor(MetaEntity me, Component parent,Repository repository) throws Exception{
    	Set<MetaRelationship> related = repository.getMetaModel().getMetaRelationshipsFor(me);
        MetaRelationship[] options = related.toArray(new MetaRelationship[related.size()]);
        MetaRelationship meta = null;
        if(options.length == 1) {
            meta = (MetaRelationship)options[0];
        } else if (options.length > 1){
	        Arrays.sort(options, new NameComparator());
	        meta = (MetaRelationship)JOptionPane.showInputDialog(
	        	parent,  "Select Relationship Type", "EATool",
	        	JOptionPane.QUESTION_MESSAGE, null,
	        	options, null
	        );
        }
        return meta;
    }
    
    /**
     * Select an Entity of a given entity type.
     * @param me is the type of entity we want.
     * @param frame is the parent frame for the dialog.
     * @return the selected Entity or null if none selected.
     */
    public static Entity selectEntityOf(MetaEntity me, Component parent,Repository repository) throws Exception{
    	Collection<Entity>entities = repository.getModel().getEntitiesOfType(me);
        Entity[] options = (Entity[])entities.toArray(new Entity[entities.size()]);
        Entity e = null;
        if(options.length == 1){
            e = (Entity)options[0];
        } else if (options.length > 1) {
	        Arrays.sort(options, new EntityComparator());
	        
	        e = (Entity)JOptionPane.showInputDialog(
	        	parent,  "Select Entity", "EATool",
	        	JOptionPane.QUESTION_MESSAGE, null,
	        	options, null
	        );
        }
        return e;
    }

    /**
     * Select an Entity from a collection
     * @param entities is the collection of Entity to select from.
     * @param frame is the parent frame for the dialog.
     * @return the selected Entity or null if none selected.
     */
    public static Entity selectEntityFrom(Collection<Entity> entities, Component parent){
        Entity[] options = (Entity[])entities.toArray(new Entity[entities.size()]);
        Entity e = null;
        if(options.length == 1){
            e = (Entity)options[0];
        } else if (options.length > 1){
	        Arrays.sort(options, new EntityComparator());
	        
	        
	        e = (Entity)JOptionPane.showInputDialog(
	        	parent,  "Select Entity", "EATool",
	        	JOptionPane.QUESTION_MESSAGE, null,
	        	options, null
	        );
        }
        
        return e;
    }

    /**
     * Select an Entity from a collection
     * @param entities is the collection of Entity to select from.
     * @param frame is the parent frame for the dialog.
     * @return the selected Entity or null if none selected.
     */
    public static Collection<Entity> selectMultipleEntitiesFrom(Collection<Entity> entities, Component parent){
        
        Collection<Entity> results = new LinkedList<Entity>();
        
        if(entities.size() == 1){
            results.add(entities.iterator().next()); // just copy to output - don't prompt the user.
        } else if (entities.size() > 1){
            
            Entity[] tempArr = (Entity[])entities.toArray(new Entity[entities.size()]);
            Arrays.sort(tempArr, new EntityComparator());
            
        	Object[] options = new Object[tempArr.length + 1];
            int idx = 0;
            String selectAll = "-- Select All --";
            options[idx++] = selectAll;
            for(Entity e : tempArr){
            	options[idx++] = e;
            }
	        
	        Object selected = JOptionPane.showInputDialog(
	        	parent,  "Select Entity", "EATool",
	        	JOptionPane.QUESTION_MESSAGE, null,
	        	options, null
	        );
	        
	        if(selected != null) {
		        if(selected == selectAll){
		            for(Entity e : tempArr){
		            	results.add(e);
		            }
		        } else {
		        	results.add((Entity) selected);
		        }
	        }
        }
        
        return results;
    }

    /**
     * Select a NamedRepositoryItem from a list.
     * @param nres is the list of NamedRepositoryItem to select from
     * @param title is the title for the dialog.
     * @param frame is the parent frame for the dialog.
     * @return the selected NamedRepositoryItem or null if none selected.
     */
    public static NamedItem selectNRIFrom(Collection<? extends NamedItem> nres, String title, Component parent){
        NamedItem[] options = (NamedItem[])nres.toArray(new NamedItem[nres.size()]);
        NamedItem nri = null;
        if(options.length == 1){
            nri = (NamedItem)options[0];
        } else if (options.length > 1) {
	        Arrays.sort(options, new NameComparator());
	        nri = (NamedItem)JOptionPane.showInputDialog(
	        	parent,  title, "EATool",
	        	JOptionPane.QUESTION_MESSAGE, null,
	        	options, null
	        );
        }
        return nri;
    }

    public static void message(Component parent, String text){
        JOptionPane.showMessageDialog(parent,text,"EATool", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warning(Component parent, String text){
        JOptionPane.showMessageDialog(parent,text,"EATool", JOptionPane.WARNING_MESSAGE);
    }
    /**
     * @param string
     * @return
     */
    public static boolean question(Component parent, String text) {
        int sel = JOptionPane.showConfirmDialog(parent,text,"EATool",JOptionPane.YES_NO_OPTION);
        return sel == JOptionPane.YES_OPTION;
    }

    /**
     * @param string
     * @return
     */
    public static String input(Component parent, String text) {
        String value = JOptionPane.showInputDialog(parent,text,"EATool",JOptionPane.QUESTION_MESSAGE);
        return value;
    }

    private static class NameComparator implements Comparator<NamedItem>{
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(NamedItem mr0, NamedItem mr1) {
            return mr0.getName().compareTo(mr1.getName());
        }
    }
    
    private static class EntityComparator implements Comparator<Entity>{
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Entity e0, Entity e1) {
            return e0.toString().compareTo(e1.toString());
        }
        
    }

}
