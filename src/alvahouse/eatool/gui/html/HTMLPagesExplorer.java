/*
 * HTMLPagesExplorer.java
 * Project: EATool
 * Created on 04-May-2007
 *
 */
package alvahouse.eatool.gui.html;

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
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.html.HTMLPages;
import alvahouse.eatool.repository.html.PageChangeEvent;
import alvahouse.eatool.util.SettingsManager;

/**
 * HTMLPagesExplorer
 * 
 * @author rbp28668
 */
public class HTMLPagesExplorer extends JInternalFrame implements Explorer{

 
    private static final long serialVersionUID = 1L;
    private ActionSet actions;
    private javax.swing.JScrollPane scrollPane;
    private ExplorerTree tree;
    private HTMLPagesTreeModel treeModel;
    private JInternalFrame thisFrame;
    private Application app;
    private static final String WINDOW_SETTINGS = "/Windows/PageExplorer";

    /**
     * 
     */
    public HTMLPagesExplorer(HTMLPages pages, Application app, Repository repository) throws Exception {
        super();
        setTitle("Framework Pages");
        init(pages,app, repository);
    }

    /**
     * 
     */
    private void init(HTMLPages pages, Application app, Repository repository) throws Exception{
		thisFrame = this;
		this.app = app;
        
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        
        // Magic up ImportMappings from settings.
        //Scripts scripts = repository.getScripts();

        treeModel = new HTMLPagesTreeModel("Pages",pages);
        
        tree = new ExplorerTree();
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement("/PageExplorer/popups");
        actions = new HTMLPagesActionSet(this,pages, app, repository);
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
								if(thingy instanceof HTMLPage){
									Action action = actions.getAction("PageShow");
									action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "show"));
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
   
   public void pageAdded(PageChangeEvent event) {
       treeModel.pageAdded(event);
   }

   public void pageEdited(PageChangeEvent event) {
       treeModel.pageEdited(event);
   }

   public void pageRemoved(PageChangeEvent event) {
	   treeModel.pageRemoved(event);
   }



}
