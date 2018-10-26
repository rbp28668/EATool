/*
 * ImportMappingExplorer.java
 * Project: EATool
 * Created on 03-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JInternalFrame;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.ExplorerTree;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.mapping.ImportMappings;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.SettingsManager;

/**
 * ImportMappingExplorer is the main explorer for setting up and maintaining import mappings.
 * 
 * @author rbp28668
 */
public class ImportMappingExplorer extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private ActionSet actions;
    private javax.swing.JScrollPane scrollPane;
    private ExplorerTree tree;
    private ImportMappingTreeModel treeModel;
    private JInternalFrame thisFrame;
    private static final String WINDOW_SETTINGS = "/Windows/ImportMappingExplorer";
    private Application app;
    
	/**
	 * Constructor for ImportMappingExplorer.
	 */
	public ImportMappingExplorer(Application app, Repository repository) {
		super();
		thisFrame = this;
		this.app = app;
		
        setTitle("Import Mappings");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        
        // Magic up ImportMappings from settings.
        MetaModel metaModel = repository.getMetaModel();
        ImportMappings mappings = repository.getImportMappings();

        treeModel = new ImportMappingTreeModel("Mappings",mappings);
        
        tree = new ExplorerTree();
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement("/ImportMappingExplorer/popups");
        actions = new ImportMappingActionSet(this,mappings,app,repository);
        tree.setPopups(cfg,actions);
        
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {

						DefaultMutableTreeNode node = tree.getSelectedNode();
						if(node != null){
							try{
								Object thingy = node.getUserObject();
//								if(thingy instanceof Diagram){
//									Action action = actions.getAction("DiagramEdit");
//									action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "edit"));
//								}
							} catch (Exception ex){
								new ExceptionDisplay(thisFrame,ex);
							}
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);

        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setViewportView(tree);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
 	}
 	
    public void dispose() {
         GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);

        app.getWindowCoordinator().removeFrame(this);
        treeModel.dispose();
        super.dispose();
    }

    /**
     * Gets the node currently selected (if any).
     * @return DefaultMutableTreeNode - the current node or null if none selected.
     */
    public DefaultMutableTreeNode getSelectedNode() {
        return tree.getSelectedNode();
    }

}
