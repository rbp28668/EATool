/*
 * ScriptExplorer.java
 * Project: EATool
 * Created on 05-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.Explorer;
import alvahouse.eatool.gui.ExplorerTree;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.SettingsManager;

/**
 * ScriptExplorer is a tree based explorer for managing scripts.
 * 
 * @author rbp28668
 */
/**
 * ScriptExplorer
 * 
 * @author rbp28668
 */
public class ScriptExplorer extends JInternalFrame implements Explorer{

    private static final long serialVersionUID = 1L;
    private ActionSet actions;
    private javax.swing.JScrollPane scrollPane;
    private ExplorerTree tree;
    private ScriptTreeModel treeModel;
    private JInternalFrame thisFrame;
    private Application app;
    private static final String WINDOW_SETTINGS = "/Windows/ScriptExplorer";

    /**
     * 
     */
    public ScriptExplorer(Application app, Repository repository) throws Exception{
        super();
        setTitle("Scripts");
        init(app,repository);
    }

    /**
     * @param title
     */
    public ScriptExplorer(String title,Application app, Repository repository) throws Exception {
        super(title);
        init(app,repository);
    }

    /**
     * 
     */
    private void init(Application app, Repository repository) throws Exception{
		thisFrame = this;
		this.app = app;
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        
        // Magic up ImportMappings from settings.
        Scripts scripts = repository.getScripts();

        treeModel = new ScriptTreeModel("Scripts",scripts);
        
        tree = new ExplorerTree();
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement("/ScriptExplorer/popups");
        actions = new ScriptActionSet(this,scripts,app);
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
								if(thingy instanceof Script){
									Action action = actions.getAction("ScriptRun");
									action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "run"));
								}
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

    /* (non-Javadoc)
     * @see javax.swing.JInternalFrame#dispose()
     */
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
