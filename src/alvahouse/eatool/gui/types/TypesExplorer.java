/*
 * TypesExplorer.java
 * Project: EATool
 * Created on 11-Jul-2006
 *
 */
package alvahouse.eatool.gui.types;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypes;
import alvahouse.eatool.util.SettingsManager;

/**
 * TypesExplorer is an explorer frame for the repositories extensible types.
 * 
 * @author rbp28668
 */
public class TypesExplorer extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private JScrollPane scrollPane;
    private TypesTree tree;
    private TypesTreeModel treeModel;
    private Application app;
    
    private final static String WINDOW_SETTINGS = "/Windows/TypesExplorer";
    
    /**
     * 
     */
    public TypesExplorer(ExtensibleTypes types, Application app) throws Exception{
        super();
        this.app = app;
        
        setTitle("Types Explorer");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        tree = new TypesTree();
        treeModel = new TypesTreeModel(types, "Types");
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement("/TypesExplorer/popups");
        ActionSet actions = new TypesActionSet(this);
        tree.setPopups(cfg,actions);
        
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setViewportView(tree);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    /** gets the selected node (if any) from the tree
     * @return the selected node or null if none selected
     */
    public DefaultMutableTreeNode getSelectedNode() {
        return tree.getSelectedNode();
    }
    
    /**
     * Gets the ExtensibleTypes this explorer is exploring.
     * @return the types.
     */
    public ExtensibleTypes getTypes(){
        return treeModel.getTypes();
    }
    

    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        treeModel.dispose();
        super.dispose();
    }
    
    /** 
     * Make sure all the view is refreshed.
     */
    public void refresh() {
        treeModel.refresh();
    }

}
