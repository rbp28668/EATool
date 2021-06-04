/*
 * MetaModelViewer.java
 *
 * Created on 12 February 2002, 08:04
 */

package alvahouse.eatool.gui.graphical.standard.metamodel;

import javax.swing.JInternalFrame;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.util.SettingsManager.Element;


/**
 * MetaModelViewer is the basic graphical display for
 * a meta-model.
 * @author  rbp28668
 */
public class MetaModelViewer extends StandardDiagramViewer {

    private Application app;
    private static final long serialVersionUID = 1L;
    private static final String WINDOW_SETTINGS = "Windows/MetaModelViewer";

    /** Creates new form MetaModelViewer */
    public MetaModelViewer(StandardDiagram diagram, Application app, Repository repository) throws Exception {
        super(diagram, 
                new MetaModelViewerItemHandler(MetaModelDiagramType.getInstance(), 
                		app, repository),
                app,
                repository
        );
        this.app = app;
        
        setTitle("Meta-Model - " + diagram.getName());
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);
        
		Element cfg = app.getConfig().getElement("/MetaModelViewer/menus");
		ActionSet actions = new MetaModelViewerActionSet(this, app, repository);
		setMenuBar(cfg, actions);

        //getViewPane().setLayoutStrategy(new GridLayoutStrategy() );
        //getViewPane().deferLayout();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JInternalFrame#dispose()
     */
    public void dispose(){
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS, app);
        super.dispose();
    }

    /**
	 * DiagramViewerWindowFactory provides a WindowFactory for creating
	 * DiagramViewers in conjunction with the WindowCoordinator.
	 * @author Bruce.Porteous
	 *
	 */
	public static class WindowFactory implements WindowCoordinator.WindowFactory {

		private Diagram diagram;
		private Repository repository;
		private Application app;
		/**
		 * Creates a window factory for creating a window for the given
		 * diagram.
		 * @param diagram to create a viewer for.
		 */
		public WindowFactory(Diagram diagram, Application app,Repository repository) {
			this.diagram = diagram;
			this.repository = repository;
			this.app = app;
		}

		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.gui.WindowCoordinator.WindowFactory#createFrame()
		 */
		public JInternalFrame createFrame() throws Exception {
			return new MetaModelViewer((StandardDiagram)diagram, app, repository);
		}
   	
   }


}
