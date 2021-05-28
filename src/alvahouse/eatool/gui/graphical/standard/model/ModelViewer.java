/*
 * ModelViewer.java
 *
 * Created on 06 March 2002, 21:22
 */

package alvahouse.eatool.gui.graphical.standard.model;

import javax.swing.JInternalFrame;

import org.apache.bsf.BSFException;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.DiagramViewerWindowFactory;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.util.SettingsManager.Element;


/**
 * ModelViewer is the basic window for the graphical
 * display of a model.
 * @author  rbp28668
 */
public class ModelViewer extends StandardDiagramViewer{

    private static final long serialVersionUID = 1L;
    private static final String WINDOW_SETTINGS = "Windows/ModelViewer";
    private Application app;

    /** Creates new ModelViewer 
     * @throws BSFException*/
    public ModelViewer(StandardDiagram diagram, Application app, Repository repository) throws Exception {
        super(  diagram, 
                new ModelViewerItemHandler((StandardDiagramType)diagram.getType(), app, repository),
                app, repository);
        this.app = app;
        
        setTitle("Model Viewer - " + diagram.getName());
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);
		//getViewPane().setLayoutStrategy(new GridLayoutStrategy() );
		//getViewPane().deferLayout();
 
		Element cfg = app.getConfig().getElement("/ModelViewer/menus");
		ActionSet actions = new ModelViewerActionSet(this, app, repository);
		setMenuBar(cfg, actions);
        
    }
    /* (non-Javadoc)
     * @see javax.swing.JInternalFrame#dispose()
     */
    public void dispose(){
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        super.dispose();
    }
    
    /**
	 * DiagramViewerWindowFactory provides a WindowFactory for creating
	 * DiagramViewers in conjunction with the WindowCoordinator.
	 * @author Bruce.Porteous
	 *
	 */
	public static class WindowFactory implements DiagramViewerWindowFactory {

		private StandardDiagram diagram;
		private Repository repository;
		private Application app;

		/**
		 * Default constructor for reflective instantiation.
		 */
		public WindowFactory(){
		}

		/**
		 * Creates a window factory for creating a window for the given
		 * diagram.
		 * @param diagram to create a viewer for.
		 */
		public WindowFactory(StandardDiagram diagram, Application app, Repository repository) {
			init(diagram,app,repository);
		}
		
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.gui.DiagramViewerWindowFactory#init(alvahouse.eatool.gui.graphical.Diagram, alvahouse.eatool.repository.model.Model)
		 */
		public void init(Diagram diagram, Application app, Repository repository){
		    this.diagram = (StandardDiagram)diagram;
		    this.repository = repository;
		    this.app = app;
		}
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.gui.WindowCoordinator.WindowFactory#createFrame()
		 */
		public JInternalFrame createFrame() throws Exception {
		    if(diagram == null || repository == null){
		        throw new IllegalStateException("Model viewer not intitialised");  
		    }
			return new ModelViewer(diagram, app, repository);
		}
   	
   }

}
