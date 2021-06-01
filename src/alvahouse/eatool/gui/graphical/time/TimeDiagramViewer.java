/*
 * TimeDiagramViewer.java
 * Project: EATool
 * Created on 26-Oct-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;

import org.apache.bsf.BSFException;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.DiagramViewerWindowFactory;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.graphical.DiagramViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.SettingsManager.Element;

/**
 * TimeDiagramViewer
 * 
 * @author rbp28668
 */
public class TimeDiagramViewer extends DiagramViewer {
    
    private static final long serialVersionUID = 1L;
    private JMenuBar menuBar;
	private LabelPane labelPane;
    private ViewerPane viewPane;
    private JSplitPane splitPane;
	private JScrollPane scrollLeft;
	private JScrollPane scrollRight;
	
    private TimeDiagram diagram;
    private float zoom = 1.0f;
    private Application app;
    private Repository repository;
    private final ActionSet actions;
	private static final String WINDOW_SETTINGS = "Windows/TimeDiagramViewer";


    public TimeDiagramViewer(TimeDiagram diagram, Application app, Repository repository) throws Exception {
		super(getViewerTitle(diagram), diagram);

		this.diagram = (TimeDiagram)diagram;
		this.app = app;
		this.repository = repository;
 
		try {
            diagram.getEventMap().fireEvent(Diagram.ON_DISPLAY_EVENT, repository.getScripts());
        } catch (BSFException e) {
            new ExceptionDisplay(app.getCommandFrame(),e);
        }
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        // default with magic numbers
        setSize(300,200);   // magic
        setLocation(20,20); // magic

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        labelPane = new LabelPane();
        scrollLeft = new JScrollPane(labelPane);
        splitPane.setLeftComponent(scrollLeft);

        viewPane = new ViewerPane();
        scrollRight = new JScrollPane(viewPane);
        splitPane.setRightComponent(scrollRight);
        
        // Make the 2 scroll panes use a common model for vertical scrolling so panes stay in sync
        scrollLeft.getVerticalScrollBar().setModel(scrollRight.getVerticalScrollBar().getModel());

        scrollRight.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentListener() {

            public void adjustmentValueChanged(AdjustmentEvent arg0) {
                viewPane.repaint();
            }
        
        });
        this.setContentPane(splitPane);
        
        viewPane.setToolTipText("*");
        
        setTitle("Time  Viewer - " + diagram.getName());
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);
 
        SettingsManager settings = app.getConfig();
		Element cfg = settings.getElement("/TimeDiagramViewer/menus");
		actions = new TimeDiagramViewerActionSet(this, app, repository);
		setMenuBar(cfg, actions);
            
        
    }

    
    /**
     * Gets the actions for the time diagram viewer - this allows the actions
     * to be scripted.
     * @return ActionSet for this viewer.
     */
    public ActionSet getActions() {
    	return actions;
    }
    
    /**
     * Creates a window name from a diagram name and its type.
	 * @param diagram is the diagram to create the name.
	 * @return String with the title for the viewer window.
	 */
	private static final String getViewerTitle(Diagram diagram){
		assert(diagram != null);
		return diagram.getType().getName() + ":" + diagram.getName();    	
    }
    
    /**
     * updates the window title from the given diagram.
	 * @param diagram to use to update the window.
	 */
	public void updateTitleFrom(Diagram diagram) {
    	setTitle(getViewerTitle(diagram));
    }

    /**
     * Sets the menu bar from the configuration.
     * @param cfg is the configuration of the menus.
     * @param actions is the ActionSet containing the actions for the menu.
     */
    protected void setMenuBar(SettingsManager.Element cfg, ActionSet actions) {
        menuBar = new JMenuBar();
		GUIBuilder.buildMenuBar(menuBar, actions, cfg);
		setJMenuBar(menuBar);
    }

    public void refresh() {
        labelPane.refresh();
        viewPane.refresh();
    }

    public TimeDiagram getDiagram(){
    	return diagram;
    }

    /**
	 * Gets the current zoom factor. 1 is normal size, 0.5
	 * half size, 2.0 double etc.
	 * @return the current zoom factor.
	 */
	public float getZoom() {
		return zoom;
	}

	/**
	 * Sets the zoom factor.
	 * @param f is the value to use.
	 */
	public void setZoom(float f) {
		zoom = f;
	    diagram.forceLayout();
	    invalidate();
	    validate();
		refresh();
	}

    public void fitDiagramToWindow(){
		int winX = scrollRight.getWidth();
		int winY = scrollRight.getHeight();

    	viewPane.fitDiagramToWindow(winX, winY);
    }

    public void dispose() {
 		try {
            diagram.getEventMap().fireEvent(Diagram.ON_CLOSE_EVENT, repository.getScripts());
        } catch (Exception e) {
            new ExceptionDisplay(app.getCommandFrame(),e);
        }

	    GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        viewPane.dispose();
        labelPane.dispose();
    }
    
    /** internal pane to hold the view of a model
     */
    class ViewerPane extends JPanel {
        private static final long serialVersionUID = 1L;
        
        ViewerPane() {
            setAutoscrolls(true);
            ToolTipManager.sharedInstance().registerComponent(this);
            TimeDiagramViewerMouseHandler mouse = new TimeDiagramViewerMouseHandler(TimeDiagramViewer.this, app, repository);
			addMouseListener(mouse);
			addMouseMotionListener(mouse);
        }
        
        
        public void refresh() {
            repaint();
        }
        
        
        /* (non-Javadoc)
		 * @see java.awt.Component#getPreferredSize()
		 */
		public Dimension getPreferredSize() {
		    return diagram.getDiagramSize();
        }
        
        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        public void paintComponent(Graphics g) {
 			this.setBackground(diagram.getBackgroundColour());
            super.paintComponent(g);

            Rectangle windowBounds;
            Container parent = getParent();
            if(parent instanceof JViewport){
                windowBounds = ((JViewport)parent).getViewRect();
            } else {
                windowBounds = getBounds();
            }
            //g.setClip(windowBounds.x,windowBounds.y, windowBounds.width, windowBounds.height); 
            g.setColor(Color.black);
            diagram.drawTimeScale((Graphics2D)g,windowBounds,zoom);
            diagram.draw((Graphics2D)g, zoom);
       }

		/**
		 * This sets the zoom size so that the diagram is fitted to the window.
		 */
		public void fitDiagramToWindow( int winX, int winY){
			Rectangle2D.Float bounds = diagram.getExtendedBounds(1.0f);
			
			float zoomX = winX / bounds.width;
			float zoomY = winY / bounds.height;
			
			float zoom = Math.min(zoomX,zoomY);
			setZoom(zoom);
		}

        public String getToolTipText(MouseEvent event)        {
            return null;
        }
        
        public void dispose() {
        }
        
       
    }
    
    /**
     * LabelPane
     * 
     * @author rbp28668
     */
    private class LabelPane extends JPanel {
        
        private static final long serialVersionUID = 1L;

        public void refresh() {
            repaint();
        }
        
        public void paintComponent(Graphics g) {
 			this.setBackground(diagram.getBackgroundColour());
            super.paintComponent(g);
            diagram.drawCaptions((Graphics2D)g, zoom);
       }

        /* (non-Javadoc)
		 * @see java.awt.Component#getPreferredSize()
		 */
		public Dimension getPreferredSize() {
		    return diagram.getCaptionsSize();
        }

        
        public String getToolTipText(MouseEvent event)        {
            return null;
        }
        
        public void dispose() {
        }

    }
    
    /**
	 * DiagramViewerWindowFactory provides a WindowFactory for creating
	 * DiagramViewers in conjunction with the WindowCoordinator.
	 * @author Bruce.Porteous
	 *
	 */
	public static class WindowFactory implements DiagramViewerWindowFactory {

		private TimeDiagram diagram;
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
		public WindowFactory(TimeDiagram diagram, Application app, Repository repository) {
			init(diagram, app, repository);
		}
		
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.gui.DiagramViewerWindowFactory#init(alvahouse.eatool.gui.graphical.Diagram, alvahouse.eatool.repository.model.Model)
		 */
		public void init(Diagram diagram, Application app, Repository repository){
		    this.diagram = (TimeDiagram)diagram;
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
			return new TimeDiagramViewer(diagram, app, repository);
		}
   	
   }
}

 
