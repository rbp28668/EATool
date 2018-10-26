/*
 * ModelExplorer.java
 *
 * Created on 20 February 2002, 20:08
 */

package alvahouse.eatool.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.util.SettingsManager;

/**
 * Provide a tree-based editor to explore and edit the Model.
 * @author  rbp28668
 */
public class ModelExplorer extends javax.swing.JInternalFrame implements Explorer {

    private static final long serialVersionUID = 1L;
    private javax.swing.JScrollPane scrollPane;
    private ExplorerTree tree;
    private ModelExplorerTreeModel treeModel;
    private Application app;
    
    private final static String WINDOW_SETTINGS = "/Windows/ModelExplorer";
    /** Creates new ModelExplorer */
    public ModelExplorer(Application app, Repository repository) {
        this.app = app;
        
        setTitle("Model Explorer");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS, app);

        tree = new ExplorerTree();
        treeModel = new ModelExplorerTreeModel("Model");
        treeModel.setModel(repository.getModel());
        treeModel.setMetaModel(repository.getMetaModel());
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement("/ModelExplorer/popups");
        ActionSet actions = new ModelActionSet(this, app, repository);
        tree.setPopups(cfg,actions);
        
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setViewportView(tree);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS, app);
        app.getWindowCoordinator().removeFrame(this);
        treeModel.dispose();
        super.dispose();
    }
    
    /** Call when the meta model changes to get the explorer to re-build
     * it's internal representation
     */
    public void refresh() {
        treeModel.refresh();
    }

    /** gets the selected node (if any) from the tree
     * @return the selected node or null if none selected
     */
    public DefaultMutableTreeNode getSelectedNode() {
        return tree.getSelectedNode();
    }
    
    /**
     * Gets the Model this explorer is exploring.
     * @return the model.
     */
    public Model getModel(){
        return treeModel.getModel();
    }
}
