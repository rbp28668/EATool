package alvahouse.eatool.gui.graphical;

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

import alvahouse.eatool.Main;
import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;

/**
 * EntitySelectionDialog allows the user to select a given entity by
 * first selecting its meta type, then selecting from the entities of that
 * type.
 * @author Bruce.Porteous
 *
 */
public class EntitySelectionDialog extends BasicDialog {
	private static final long serialVersionUID = 1L;
	private boolean selected = false;
	private JTree tree = null;

	public EntitySelectionDialog(
		Component parent,
		List<MetaEntity> allowedMetaEntities,
		Model model) {
		super(parent, "Select Entity");

		// Build a tree model to display a tree
		tree = new JTree(createTree(allowedMetaEntities, model));
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
							new ExceptionDisplay(Main.getAppFrame(),x);
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
		selected = isSelectedEntity();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		return isSelectedEntity();
	}

	/**
	 * Determines whether any of the  selected items is an Entity.
	 * @return true iff there is a selected item and it's an Entity.
	 */
	private boolean isSelectedEntity() {
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
	 * Determines whether an entity was selected while the dialog was active.
	 * @return true if there is a valid entity selection to use.
	 */
	public boolean isEntitySelected() {
		return selected;
	}

	/**
	 * Gets the Entity the user selected when the dialog was active.  Call isEntitySelected
	 * first to ensure that there is a selected entity.
	 * @return the selected entity.
	 */
	public Entity getSelectedEntity() {
		TreePath treePath = tree.getSelectionPath();
		if (treePath == null) {
			return null;
		}

		DefaultMutableTreeNode selectedNode =
			(DefaultMutableTreeNode) treePath.getLastPathComponent();
		return (Entity) selectedNode.getUserObject();
	}
	
	/**
	 * Gets all selected Entities.
	 * @return an array with all the selected Entities or null if none selected.
	 */
	public Entity[] getAllSelected(){
		TreePath[] paths = tree.getSelectionPaths();
		if (paths == null) {
			return null;
		}

		List<Entity> entities = new LinkedList<>();
		for(int i=0; i<paths.length; ++i){
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			Object obj = selectedNode.getUserObject();
			if(obj instanceof Entity) {
			    entities.add((Entity)obj);
			}
		}
		return (Entity[])entities.toArray(new Entity[entities.size()]);
	}
	/**
	 * @param metaModel
	 * @param model
	 * @return
	 */
	private TreeModel createTree(List<MetaEntity> allowedMetaEntities, Model model) {

		DefaultTreeModel treeModel =
			new DefaultTreeModel(new DefaultMutableTreeNode("Entities"));
		int idx = 0;
		for (MetaEntity me : allowedMetaEntities) {
			idx = addMetaEntityNode(treeModel, me, model, idx);
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
	private int addMetaEntityNode(
		DefaultTreeModel treeModel,
		MetaEntity me,
		Model model,
		int idxEntity) {
		List<Entity> listEntities = model.getEntitiesOfType(me);
		if (!listEntities.isEmpty()) {
			DefaultMutableTreeNode tnEntity = new DefaultMutableTreeNode(me);
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
