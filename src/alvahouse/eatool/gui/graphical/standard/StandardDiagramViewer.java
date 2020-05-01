package alvahouse.eatool.gui.graphical.standard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.bsf.BSFException;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.graphical.DiagramViewer;
import alvahouse.eatool.gui.graphical.EventErrorHandler;
import alvahouse.eatool.gui.graphical.layout.IGraphicalLayoutStrategy;
import alvahouse.eatool.gui.graphical.layout.LayoutManager;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.TooltipProvider;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.scripting.proxy.ScriptWrapper;
import alvahouse.eatool.util.SettingsManager;

/**
 * This is the frame window for all diagram display and editing.  This should
 * be subclassed and the correct ItemHandler supplied for the type of diagram.
 * @author bruce.porteous
 */
public abstract class StandardDiagramViewer extends DiagramViewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollView;
	private ViewerPane viewPane;
	private PalettePane palettePane;
	private StandardDiagramViewerActionSet actions;
	private JMenuBar menuBar;
	private ItemHandler itemHandler;
	private Application app;
	private Repository repository;
	private static final String WINDOW_SETTINGS = "Windows/StandardDiagramViewer";
	
	/**
	 * Constructor for StandardDiagramViewer.
	 * @throws BSFException
	 */
	public StandardDiagramViewer(StandardDiagram diagram, ItemHandler itemHandler, Application app, Repository repository)  {
		super(getViewerTitle(diagram),diagram);
		this.app = app;
		this.repository = repository;

		actions = new StandardDiagramViewerActionSet(this, app, repository);
		try {
            fireEvent(diagram, repository, StandardDiagram.ON_DISPLAY_EVENT);
        } catch (BSFException e) {
            new ExceptionDisplay(app.getCommandFrame(),e);
        }
		
		this.itemHandler = itemHandler;
		
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        viewPane = new ViewerPane(this, diagram, itemHandler);
        palettePane = new PalettePane(this,diagram);
        
		scrollView = new JScrollPane(viewPane);

        getContentPane().add(scrollView,BorderLayout.CENTER);
        //getContentPane().add(palettePane,BorderLayout.WEST);
        
        viewPane.setToolTipText("*");
        setVisible(true);
    }

	/**
	 * @param diagram
	 * @param repository
	 * @param context
	 * @throws BSFException
	 */
	private void fireEvent(StandardDiagram diagram, Repository repository, String event) throws BSFException {
		ScriptContext context = diagram.getEventMap().getContextFor(event);
		if(context != null) {
			Object proxy = ScriptWrapper.wrap(diagram);
		    context.addObject("diagram", proxy, proxy.getClass());
		    
		    proxy = ScriptWrapper.wrap(this, diagram, app, repository);
		    context.addObject("viewer", proxy, proxy.getClass());
		    
		    EventErrorHandler errHandler = new EventErrorHandler(this);
		    context.setErrorHandler(errHandler);
		    ScriptManager.getInstance().runScript(context);
		}
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

    /**
	 * Repaints the diagram view.
	 */
	public void refresh() {
        viewPane.refresh();
    }

    /* (non-Javadoc)
	 * @see javax.swing.JInternalFrame#dispose()
	 */
	public void dispose() {
	    
		try {
              StandardDiagram diagram = getViewPane().diagram;
              fireEvent(diagram, repository, StandardDiagram.ON_CLOSE_EVENT);
        } catch (BSFException e) {
            new ExceptionDisplay(app.getCommandFrame(),e);
        }

	    GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        viewPane.dispose();
        super.dispose();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramViewer#fitDiagramToWindow()
     */
    public void fitDiagramToWindow(){
		int winX = scrollView.getWidth();
		int winY = scrollView.getHeight();

    	viewPane.fitDiagramToWindow(winX, winY);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramViewer#getZoom()
     */
    public float getZoom(){
    	return viewPane.getZoom();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramViewer#setZoom(float)
     */
    public void setZoom(float zoom){
    	viewPane.setZoom(zoom);
		scrollView.invalidate();
	   	scrollView.validate();
    	viewPane.repaint();
     }
     
	/**
	 * Gets the ViewerPane displaying the diagram.
	 * @return the ViewerPane.
	 */
	public ViewerPane getViewPane(){
		return viewPane;
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
	 * Lays out the diagram with the current layout strategy.
	 */
	public void layoutDiagram(){
		viewPane.layoutDiagram();
		viewPane.repaint();
	}
        
	/**
	 * Sets the current layout strategy to use with the auto layout.
	 * @param strategy is the layout strategy to use.
	 */
	public void setLayoutStrategy(IGraphicalLayoutStrategy strategy){
		viewPane.setLayoutStrategy(strategy);
	}

	/**
	 * Gets the ItemHandler for this viewer.  Allows type specific code
	 * to be used by generic handlers.
	 * @return the registered ItemHandler.
	 */
	public ItemHandler getItemHandler(){
	    return itemHandler;
	}

    /**
     * Gets any GraphicalObject selected on the diagram. Note that this
     * may be a symbol but will not be a Connector.
     * @return any selected GraphicalObject or <code>null</code> if nothing selected.
     */
    public GraphicalObject getSelectedObject() {
        return viewPane.getSelectedObject();
    }


	/**
	 * Gets any Symbol selected on the diagram.
     * @return any selected Symbol or <code>null</code> if no symbol is selected.
	 */
	public Symbol getSelectedSymbol(){
	    return viewPane.getSelectedSymbol();
	}
	
	/**
	 * Gets any connector selected on the diagram.
     * @return any selected Conenctor or <code>null</code> if no connector is selected.
	 */
	public Connector getSelectedConnector() {
	    return viewPane.getSelectedConnector();
	}

	/**
	 * Gets the action set for this viewer.
	 * @return the viewer's action set.
	 */
	public ActionSet getActions(){
	    return actions;
	}
	
    /**
	 * @return the palettePane
	 */
	public PalettePane getPalettePane() {
		return palettePane;
	}

	/**
	 * PalettePane is intended for diagram tools.
	 * @author Bruce.Porteous
	 *
	 */
	private class PalettePane extends JPanel {
		private static final long serialVersionUID = 1L;
		private StandardDiagramViewer viewer;
		private StandardDiagram diagram;
		
		PalettePane(StandardDiagramViewer dv, StandardDiagram d) {
			viewer = dv;
			diagram = d;
			
			Box box = Box.createVerticalBox();
			JButton btnName = new JButton("Name");
			btnName.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String name = diagram.getName();
					String inputValue = JOptionPane.showInputDialog(viewer, "StandardDiagram Name",name);
					if(inputValue != null) {
						diagram.setName(inputValue); 
						viewer.updateTitleFrom(diagram);
					}
				}
			});
			box.add(btnName);
			
			String[] zoomOptions = {"10%","25%","50%","100%","200%","400%","fit"};
			JComboBox<String> zoom = new JComboBox<String>(zoomOptions);
			zoom.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					@SuppressWarnings("unchecked")
					JComboBox<String> cbx = (JComboBox<String>)e.getSource();
					String cmd = (String)cbx.getSelectedItem();
					if(cmd.equals("fit")){
						viewer.fitDiagramToWindow();
					} else {
						cmd = cmd.substring(0,cmd.lastIndexOf('%'));
						float zoomPct = Integer.parseInt(cmd);
						viewer.setZoom(zoomPct / 100.0f);
					}
				}
				
			});
			box.add(zoom);
			
			JButton btnBackgnd = new JButton("Background");
			btnBackgnd.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Color colour = JColorChooser.showDialog(viewer,"Background Colour", diagram.getBackgroundColour());
					if(colour != null){
						diagram.setBackgroundColour(colour);
					}
				}
			});
			box.add(btnBackgnd);

			add(box);
		}
    }
    
    /** internal pane to hold the view of a model.  This does the real
     * display and editing work.
     */
    public class ViewerPane extends JPanel {
        
		private static final long serialVersionUID = 1L;
		private float zoom = 1.0f;
		private StandardDiagramViewer viewer;
		private StandardDiagram diagram;
		private LayoutManager layoutManager;
        //private boolean mustDoLayout = false;
        private StandardDiagramViewerMouseHandler mouse;
        
		/**
		 * @param dv
		 * @param d
		 */
		ViewerPane(StandardDiagramViewer dv, StandardDiagram d, ItemHandler itemHandler) {
            viewer = dv;
            diagram = d;
            
			mouse = new StandardDiagramViewerMouseHandler(dv, this, itemHandler);
			addMouseListener(mouse);
			addMouseMotionListener(mouse);
			setAutoscrolls(true);
			layoutManager = new LayoutManager(diagram);
			diagram.addChangeListener(layoutManager);

        }
        

        public void refresh() {
            repaint();
        }
        
        public StandardDiagram getDiagram(){
        	return diagram;
        }
        
        /* (non-Javadoc)
		 * @see java.awt.Component#getPreferredSize()
		 */
		public Dimension getPreferredSize() {
	       	Rectangle2D.Float bounds = diagram.getExtendedBounds(zoom);
        	float width = (bounds.x + bounds.width);
        	float height = (bounds.y + bounds.height);
        	System.out.println(width + "," + height);
			return new Dimension((int)width + 1, (int)height + 1); 
        }
        
        /* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			this.setBackground(diagram.getBackgroundColour());
            super.paintComponent(g);

			if(diagram.isLayoutDeferred()){
				diagram.sizeWith((Graphics2D)g);
				layoutDiagram();
				diagram.sizeWith((Graphics2D)g);
			}

            g.setColor(Color.black);
            diagram.draw((Graphics2D)g, zoom);
        }

        /* (non-Javadoc)
		 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
		 */
		public String getToolTipText(MouseEvent event) {
        	String text = null;
        	int mx = event.getX();
        	int my = event.getY();
        	
			Symbol symbol = diagram.getSymbolAt(mx,my,zoom);
			if(symbol != null) {
				text = symbol.getItem().toString();	
			}
 
 			if(text == null) {
				Connector connector = diagram.getConnectorAt(mx,my,zoom);
				if(connector != null){
					Object userObject = connector.getItem();
					if(userObject instanceof TooltipProvider){
						text = ((TooltipProvider)userObject).getTooltip();
					} else {
						text = userObject.toString();
					}
				}
 			}
        	
			return text;
        }
        
        /**
		 * releases any references to external objects.
		 */
		public void dispose() {
			diagram.removeChangeListener(layoutManager);
			layoutManager = null;
        	viewer = null;
        	diagram = null;
        }
        
		/**
		 * This sets the zoom size so that the diagram is fitted to the window.
		 */
		public void fitDiagramToWindow( int winX, int winY){
			Rectangle2D.Float bounds = diagram.getExtendedBounds(1.0f);
			
			float zoomX = winX / bounds.width;
			float zoomY = winY / bounds.height;
			
			float zoom = Math.min(zoomX,zoomY);
			viewer.setZoom(zoom);
		}
    
        public void layoutDiagram(){
        	layoutManager.layoutGraphs(getContentPane().getSize());
        	diagram.deferLayout(false);
        }
        
        public void setLayoutStrategy(IGraphicalLayoutStrategy strategy){
        	layoutManager.setLayout(strategy);
        }
        
        /**
		 * Allows automatic layout to be deferred until the next draw.
		 */
		public void deferLayout(){
        	diagram.deferLayout(true);
        }

	     /**
		 * Allows automatic layout to be deferred until the next draw.
		 */
		public void deferLayout(boolean defer){
        	diagram.deferLayout(defer);
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
		}

	    /**
	     * Gets any GraphicalObject selected on the diagram. Note that this
	     * may be a symbol but will not be a Connector.
	     * @return any selected GraphicalObject or <code>null</code> if nothing selected.
	     */
        public GraphicalObject getSelectedObject() {
            return mouse.getSelectedObject();
        }
		
    	/**
    	 * Gets any Symbol selected on the diagram.
         * @return any selected Symbol or <code>null</code> if no symbol is selected.
    	 */
		public Symbol getSelectedSymbol(){
		    return mouse.getSelectedSymbol();
		}
		
		/**
		 * Gets any connector selected on the diagram.
	     * @return any selected Conenctor or <code>null</code> if no connector is selected.
		 */
		public Connector getSelectedConnector() {
		    return mouse.getSelectedConnector();
		}

    }

    

}
