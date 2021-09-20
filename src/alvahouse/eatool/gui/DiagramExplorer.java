package alvahouse.eatool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.tree.DefaultMutableTreeNode;

import alvahouse.eatool.Application;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.Diagrams;
import alvahouse.eatool.util.SettingsManager;

/**
 * Class to allow the use of a tree view to track diagram types and diagrams.
 * @author rbp28668
 *
 */
public class DiagramExplorer extends JInternalFrame implements Explorer{

    private static final long serialVersionUID = 1L;
    private ActionSet actions;
    private javax.swing.JScrollPane scrollPane;
    private ExplorerTree tree;
    private DiagramExplorerTreeModel treeModel;
    private JInternalFrame thisFrame;
    private String windowSettings;
    private Application app;
    
    /*
     * Title = "DiagramExplorer"
     * window settings = "/Windows/DiagramExplorer";
     * rootTitle = "Diagrams";
     * popups = "/DiagramExplorer/popups";
     */
	/**
	 * Constructor for DiagramExplorer.
 	 * @param title is the title for the window (e.g. "DiagramExplorer").
	 * @param windowSettings is the key to load/save the window settings (e.g. "/Windows/DiagramExplorer").
	 * @param rootTitle is the name of root item in the explorer (e.g. "Diagrams").
	 * @param popups is the path in the configuration to the popups for the explorer (e.g. "/DiagramExplorer/popups").
	 */
	public DiagramExplorer(String title, String windowSettings, String rootTitle, String popups, Application app, Repository repository) {
		super();
		this.app = app;
		thisFrame = this;
		
        setTitle(title);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);

        
        this.windowSettings = windowSettings;
        GUIBuilder.loadBounds(this,windowSettings,app);

        tree = new ExplorerTree();

        treeModel = new DiagramExplorerTreeModel(rootTitle);
        tree.setModel(treeModel);

        SettingsManager.Element cfg = app.getConfig().getElement(popups);
        actions = new DiagramExplorerActionSet(this,repository,app);
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
								if(thingy instanceof Diagram){
									Action action = actions.getAction("DiagramEdit");
									action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "edit"));
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
         GUIBuilder.saveBounds(this,windowSettings,app);

        app.getWindowCoordinator().removeFrame(this);
        treeModel.dispose();
        super.dispose();
    }
    
    /** Call when the meta model changes to get the explorer to re-build
     * it's internal representation
     */
    public void refresh() throws Exception{
        treeModel.refresh();
    }

    /** gets the selected node (if any) from the tree
     * @return the selected node or null if none selected
     */
    public DefaultMutableTreeNode getSelectedNode() {
        return tree.getSelectedNode();
    }
    
    public void setRepository(DiagramTypes types, Diagrams diagrams){
    	treeModel.setDiagrams(types, diagrams);
    }


}
