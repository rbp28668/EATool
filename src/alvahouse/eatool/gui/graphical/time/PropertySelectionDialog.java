package alvahouse.eatool.gui.graphical.time;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;

/**
 * PropertySelectionDialog allows the user to select a given set of properties
 * that can be displayed on a time diagram.
 * @author Bruce.Porteous
 *
 */
public class PropertySelectionDialog extends BasicDialog {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private boolean selected = false;
	private JTree tree = null;

	public PropertySelectionDialog(
		Component parent,
		TimeDiagramType diagramType,
		//MetaModel metaModel,
		Model model) {
		super(parent, "Select Property");

		// Build a tree model to display a tree
		tree = new JTree(createTree(diagramType, model));
		tree.setRootVisible(false);

		JScrollPane scroll = new JScrollPane(tree);
		
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						try {
							fireOK();
						} catch (Exception x) {
							new ExceptionDisplay(PropertySelectionDialog.this,x);
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);

		getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);

		pack();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#onOK()
	 */
	protected void onOK() {
		selected = isSelectedProperty();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		return isSelectedProperty();
	}

	/**
	 * Determines whether any of the  selected items is an Entity.  Note-
	 * entities are actually stored in the leaf nodes but they are translated
	 * into properties when read.
	 * @return true iff there is a selected item and it's an Entity.
	 */
	private boolean isSelectedProperty() {
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return false;
		}

		for(int i=0; i<paths.length; ++i){
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			Object obj = selectedNode.getUserObject();
			if(obj instanceof Entity) {
			    return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether a property was selected while the dialog was active.
	 * @return true if there is a valid property selection to use.
	 */
	public boolean isPropertySelected() {
		return selected;
	}

	/**
	 * Gets the Property the user selected when the dialog was active.  Call isPropertySelected
	 * first to ensure that there is a selected property.
	 * @return the selected entity.
	 */
	public Property getSelectedProperty() {
		TreePath treePath = tree.getSelectionPath();
		if (treePath == null) {
			return null;
		}
		
		Object[] path =	treePath.getPath();
        Property p = getPropertyFromPath(path);
        return p;
	}
	
	/**
	 * Gets all selected Properties.
	 * @return an array with all the selected Entities or null if none selected.
	 */
	public Property[] getAllSelected(){
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return null;
		}

		List<Property> properties = new LinkedList<Property>();
		for(int i=0; i<paths.length; ++i){
		    Object[] path = paths[i].getPath();
            Property p = getPropertyFromPath(path);
			if(p != null){
			    properties.add(p);
			}
		}
		return (Property[])properties.toArray(new Property[properties.size()]);
	}
	
	/**
	 * Gets a property from a selection path in the tree.
     * @param path is a selection path from the tree.
     * @return the selected Property or null if the selection cannot be 
     * translated into a property.
     */
    private Property getPropertyFromPath(Object[] path) {
        int len = path.length;
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path[len-1];
        Object obj = selectedNode.getUserObject();
        Property p = null;
        if(obj instanceof Entity) {
            // Go up the tree one level to identify which of this
            // entity's properties we want.
            Entity e = (Entity)obj;
        	DefaultMutableTreeNode entryNode = (DefaultMutableTreeNode) path[len-2];
        	TimeDiagramType.TypeEntry entry = (TimeDiagramType.TypeEntry)entryNode.getUserObject();
            p = e.getPropertyByMeta(entry.getTargetProperty().getKey());
        }
        return p;
    }

    /**
	 * @param metaModel
	 * @param model
	 * @return
	 */
	private TreeModel createTree(TimeDiagramType diagramType, Model model) {

		DefaultTreeModel treeModel =
			new DefaultTreeModel(new DefaultMutableTreeNode("Properties"));
		int idx = 0;
    	for(TimeDiagramType.TypeEntry entry : diagramType.getTargets()){
			idx = addEntryNode(treeModel, entry, model, idx);
		}
		return treeModel;

	}
	/** Adds a new tree node for a meta-entity and adds it to the parent node
	 * @param treeModel is the TreeModel to insert into.
	 * @param me me is the meta-entity to add
	 * @param model is the model to get the meta-entities children from
	 * @param idxEntity is where to insert the entity-node into its parent
	 * @return the index for the next entity to be added to
	 */
	private int addEntryNode(
		DefaultTreeModel treeModel,
		TimeDiagramType.TypeEntry entry,
		Model model,
		int idxEntity) {
	    MetaEntity me = entry.getTargetType();
		List<Entity> listEntities = model.getEntitiesOfType(me);
		if (!listEntities.isEmpty()) {
			DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode(entry);
			treeModel.insertNodeInto(
				tnEntity,
				(MutableTreeNode) treeModel.getRoot(),
				idxEntity);
			setMetaEntityNodeChildren(treeModel, tnEntity, listEntities);
			++idxEntity;
		}
		return idxEntity;
	}

	/** Sets the child nodes for a meta-entity
	 * @param treeModel
	 * @param tnMetaEntity
	 * @param listEntities
	 */
	private void setMetaEntityNodeChildren(
		DefaultTreeModel treeModel,
		MutableTreeNode tnMetaEntity,
		List<Entity> listEntities) {
		Iterator<Entity> iter = listEntities.iterator();
		int idx = 0;
		while (iter.hasNext()) {
			Entity e = iter.next();
			DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode(e);
			treeModel.insertNodeInto(tnEntity, tnMetaEntity, idx++);
		}
	}

}
