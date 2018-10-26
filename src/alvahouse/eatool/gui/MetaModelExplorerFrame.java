/*
 * MetaModelExplorerFrame.java
 *
 * Created on 20 January 2002, 14:42
 */

package alvahouse.eatool.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.impl.MetaModelImpl;
import alvahouse.eatool.util.SettingsManager;

/**
 * Main frame for displaying the meta-model in the form of a tree-view.
 * @author  rbp28668
 */
public class MetaModelExplorerFrame extends javax.swing.JInternalFrame implements Explorer{

    private static final long serialVersionUID = 1L;
    private Application app;
    private final static String WINDOW_SETTINGS = "/Windows/MetaModelExplorer";
    
    /** Creates new form MetaModelExplorerFrame 
     * @param mm is the meta-model to display
     */
    public MetaModelExplorerFrame( Application app, Repository repository) {
        this.app = app;
        
        setTitle("Meta-Model Explorer");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        tree = new ExplorerTree();
        treeModel = new MetaModelExplorerTreeModel("Meta-Model");
        treeModel.setMetaModel(repository.getMetaModel());
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement("/MetaModelExplorer/popups");
        ActionSet actions = new MetaModelActionSet(this, app, repository);
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
    
    private javax.swing.JScrollPane scrollPane;
    private ExplorerTree tree;
    private MetaModelExplorerTreeModel treeModel;
    
}
