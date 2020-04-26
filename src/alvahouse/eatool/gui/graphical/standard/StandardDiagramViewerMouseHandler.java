/*
 * StandardDiagramViewerMouseHandler.java
 * Created on 13-Jun-2004
 * By Bruce.Porteous
 * 
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.MouseInputAdapter;

import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.PositionalPopup;
import alvahouse.eatool.gui.graphical.EventErrorHandler;
import alvahouse.eatool.gui.graphical.RubberBand;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.graphical.GraphicalObject;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.graphical.standard.TextBox;
import alvahouse.eatool.repository.graphical.standard.TextObjectSettings;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.scripting.proxy.ScriptWrapper;


/**
 * StandardDiagramViewerMouseHandler is a mouse handler for the StandardDiagramViewerProxy.  It is
 * generic as the diagram type specific stuff is abstracted into an ItemHandler.
 * In effect, this acts as a controller for the StandardDiagramViewerProxy:
 * <pre>
 * Model: StandardDiagram
 * View: StandardDiagramViewerProxy
 * Controller: StandardDiagramViewerMouseHandler
 * </pre>
 * @author Bruce.Porteous
 *
 */
final class StandardDiagramViewerMouseHandler extends MouseInputAdapter {
	private final StandardDiagramViewer.ViewerPane viewPane;
	private final StandardDiagramViewer viewer;
	private final ItemHandler itemHandler;
	//private Symbol selected = null;
	private GraphicalObject selected = null;
	private Connector selectedConnector = null;
	private RubberBand rubberBand = new RubberBand();
	private Set selectedObjects = new HashSet();
	
	/**
	 * @param StandardDiagramViewerProxy.ViewerPane
	 */
	StandardDiagramViewerMouseHandler(StandardDiagramViewer viewer, StandardDiagramViewer.ViewerPane viewPane, ItemHandler itemHandler ) {
		if(viewer == null){
			throw new NullPointerException("Null viewer passed to mouse handler");
		}
		if(viewPane == null){
			throw new NullPointerException("Null view pane passed to mouse handler");
		}
		if(itemHandler == null){
			throw new NullPointerException("Null item handler passed to mouse handler");
		}
		this.viewer = viewer;
		this.viewPane = viewPane;
		this.itemHandler = itemHandler;
	}
	

	/**
	 * @param e
	 * @throws LogicException
	 */
	private void addSymbol(MouseEvent e) throws LogicException {
		Symbol[] symbols = itemHandler.addSymbolsAt(viewPane, e.getX(), e.getY());
		if(symbols != null){
		    for(int i=0; i<symbols.length; ++i){
		        viewPane.getDiagram().addSymbol(symbols[i]);
		    }
		    viewPane.repaint();
		}
	}
	
	
	/**
	 * Tries to select the connector using the given mouse event.
	 * @param e is the mouse down event to select with
	 * @return true if a connector is selected.
	 */
	private boolean selectConnector(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		StandardDiagram diagram = viewPane.getDiagram();

		float zoom = viewPane.getZoom();
		selectedConnector = diagram.getConnectorAt(mx, my, zoom);
		
		if(selectedConnector != null){
			for(Iterator iter = diagram.getConnectors().iterator(); iter.hasNext(); ){
				Connector c = (Connector) iter.next();
				if(c == selectedConnector){
					selectedConnector.onSelect(e.getX(), e.getY(), zoom);
				} else {
					if(c.isSelected()){
						c.clearSelect();
					}
				}
			}
		} else {
			for(Iterator iter = diagram.getConnectors().iterator(); iter.hasNext(); ){
				Connector c = (Connector) iter.next();
				if(c.isSelected()){
					c.clearSelect();
				}
			}
		}

		viewPane.repaint();
		return selectedConnector != null;
	}

	/**
	 * Gets the connector at the current mouse position.
	 * 
	 * @param e is the MouseEvent to use for hit-testing
	 * @return if there is a connector at the given mouse position then it
	 * is returned, otherwise null is returned.
	 */
	private Connector getSelectedConnector(MouseEvent e) {
		Connector selected = null;
		float zoom = viewPane.getZoom();
		StandardDiagram diagram = viewPane.getDiagram();
		for(Iterator iter = diagram.getConnectors().iterator(); iter.hasNext(); ){
			Connector c = (Connector)iter.next();
			if(c.hitTest(e.getX(),e.getY(),zoom)){
				selected = c;
				break;
			}
		}
		return selected;
	}

	/**
	 * Tries to select the symbol using the given mouse event.
	 * @param e is the mouse down event to select with
	 * @return true if a symbol is selected.
	 */
	private boolean selectGraphicalObject(MouseEvent e, Collection objects) {
		
		selected = hitTestGraphicalObject(e,objects);
		
		float zoom = viewPane.getZoom();
		
		if(selected != null) {
			if(e.isControlDown()) {
				if(selected.isSelected()) {
					selected.clearSelect();
					selectedObjects.remove(selected);
				} else {
					selected.onSelect(e.getX(), e.getY(), zoom);
					selectedObjects.add(selected);
				}
			}else {
				selected.onSelect(e.getX(), e.getY(), zoom);
				selectedObjects.add(selected);
			}
		}
		return selected != null;
	}

	/**
	 * Gets the GraphicalObject at the current mouse position.
	 * 
	 * @param e is the MouseEvent to use for hit-testing
	 * @return if there is an object at the given mouse position then it
	 * is returned, otherwise null is returned.
	 */
	private GraphicalObject hitTestGraphicalObject(MouseEvent e, Collection objects) {
		GraphicalObject selected = null;
		float zoom = viewPane.getZoom();
		StandardDiagram diagram = viewPane.getDiagram();
		for(Iterator iter = objects.iterator(); iter.hasNext(); ){
			GraphicalObject s = (GraphicalObject)iter.next();
			if(s.hitTest(e.getX(),e.getY(),zoom)){
				selected = s;
				break;
			}
		}
		return selected;
	}

	/**
     * @param e
     * @return
     */
    private GraphicalObject getSelectedGraphicalObject(MouseEvent e) {
        StandardDiagram diagram = viewPane.getDiagram();
        GraphicalObject object = hitTestGraphicalObject(e,diagram.getSymbols());
        if(object == null){
            object = hitTestGraphicalObject(e,diagram.getTextBoxes());
        }
        if(object == null){
            object = hitTestGraphicalObject(e,diagram.getImages());
        }
        
 //       clearUnselectedObjects();

        viewPane.repaint();

        return object;
    }

    /**
     * Clears the selection status of all the graphical objects (bar connectors)
     * which are not in the selectedObjects set.
     */
    private void clearUnselectedObjects(){
        StandardDiagram diagram = viewPane.getDiagram();
        clearUnselected(diagram.getSymbols());
        clearUnselected(diagram.getTextBoxes());
        clearUnselected(diagram.getImages());
    }
	
    /**
     * Clears all the unselected objects in the given collection. Note
     * selection is indicated by being in the selectedObjects set.
     * @param c is the collection of objects to (conditionally) clear.
     */
    private void clearUnselected(Collection c){
        for(Iterator iter = c.iterator(); iter.hasNext();){
            GraphicalObject object = (GraphicalObject)iter.next();
            if(!selectedObjects.contains(object)){
                object.clearSelect();
            }
        }
    }
    
    /**
     * Clears the selection status of all the selected objects.
     */
    private void clearSelectedExcept( GraphicalObject sel){
        for(Iterator iter = selectedObjects.iterator(); iter.hasNext();){
            GraphicalObject object = (GraphicalObject)iter.next();
            assert(object.isSelected());
            if(sel != object){
                object.clearSelect();
            }
        }
        selectedObjects.clear();
        if(sel != null){
            selectedObjects.add(sel);
        }
    }
	/**
	 * Attempts to join 2 symbols on the diagram.
	 * @param first is the first Symbol to be connected.
	 * @param second is the second Symbol to be connected.
	 * @return LogicException - if the connector can't be created.
	 */
	private void joinSymbols(Symbol first, Symbol second) 
	throws LogicException{

		Connector con = itemHandler.addConnector(viewPane,first,second);
		if(con != null) {
			con.setEnds(first,second);
			first.addArc(con);
			second.addArc(con);
			viewPane.getDiagram().addConnector(con);
			viewPane.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		try{
			if(e.getClickCount() == 1 ) {	
			    if(e.getButton() == MouseEvent.BUTTON1) {
					if(e.isControlDown()){
					    // Ctrl-click
					    GraphicalObject object = getSelectedGraphicalObject(e);
						if(object != null) {
						    if(object instanceof TextObjectSettings){
								TextObjectSettingsDialog dlg = new TextObjectSettingsDialog((TextObjectSettings)object,viewPane,"Edit Symbol Display");
								dlg.setVisible(true);	
								if(dlg.wasEdited()){
									viewPane.repaint();
								}
						    }
						} else { // null selected
						    Symbol s = itemHandler.addSymbolNewItem(viewer, e.getX(), e.getY());
						    if(s != null){
						        viewPane.getDiagram().addSymbol(s);
						        viewPane.repaint();
						    }
						}
						
					} else if (e.isShiftDown()) {
					    // Shift-click
						GraphicalObject object  = getSelectedGraphicalObject(e);
						if(object != null && object instanceof GraphicalProxy) {
						    GraphicalProxy proxy = (GraphicalProxy)object;
						    Object item = proxy.getItem();
						    String event = item.getClass().getName();
						    int idx = event.lastIndexOf('.');
						    if(idx >= 0){
						        event = event.substring(idx + 1);
						    }
						    idx = event.lastIndexOf('$');
						    if(idx >= 0){
						        event = event.substring(idx + 1);
						    }
						    Object target = ScriptWrapper.wrap(item);
						    Object diagram = ScriptWrapper.wrap(viewPane.getDiagram());
						    
						    ScriptContext context = viewPane.getDiagram().getEventMap().getContextFor(event);
						    if(context != null){
							    context.addObject("target",target,target.getClass());
							    context.addObject("diagram", diagram, diagram.getClass());

							    EventErrorHandler errHandler = new EventErrorHandler(viewer);
							    context.setErrorHandler(errHandler);
							    
							    ScriptManager.getInstance().runScript(context);
							    
						    }
						}
					} else if (e.isAltDown()) {
					    // Alt-click
					} else {
					    // Usual click
					}
					
			        
			    } else if(e.getButton() == MouseEvent.BUTTON3) {
					if(e.isControlDown()){
					    // Right-Ctrl-click
					} else if (e.isShiftDown()) {
					    // Right-Shift-click
					} else if (e.isAltDown()) {
					    // Right-Alt-click
					} else {
					    // Right-Usual click
				    
					    KeyedItem selected = null;
						GraphicalObject object = getSelectedGraphicalObject(e);
					    
						// If it's a graphical proxy then we're interested in the
						// thing the graphical object is proxying, otherwise we're
						// interested in the graphical object itself.
						if(object != null){
						    if(object instanceof KeyedItem){
						        selected = (KeyedItem)object;
						    }
						    
						    if(object instanceof GraphicalProxy) {
						        GraphicalProxy proxy = (GraphicalProxy)object;
						        selected = proxy.getItem();
						    }
						}
					    
					    if(selected == null){
					        Connector c = getSelectedConnector(e);
					        if(c != null){
					            selected = c.getItem();
					        }
					    }
					    
					    PositionalPopup popup = null;
		                if(selected != null) {
                            popup = itemHandler.getPopupFor(viewer, selected.getClass());
		                } else {
		                    popup = itemHandler.getBackgroundPopup(viewer);
		                }
		                
                        if(popup != null) {
                        	//System.out.println("Popup for " + strClass);
                            popup.setTargetPosition(e.getX(), e.getY());
                            popup.show(viewer, e.getX(), e.getY());
                        }
					}
			    }
			            
			}
			else if(e.getClickCount() == 2 ) {
				if(e.getButton() == MouseEvent.BUTTON1) {
				    // Left double click.  Do the default action which will
				    // be to edit the item in an appropriate fashion.  This
				    // drops through the different types on a diagram. If nothing
				    // selected then do the default action which is to add a new 
				    // symbol.
					StandardDiagram diagram = viewPane.getDiagram();
					boolean wasEdited = false;

					GraphicalObject object = null;
					
					object = hitTestGraphicalObject(e, diagram.getSymbols());
					if(object != null) {
					    Symbol symbol = (Symbol)object;
					    wasEdited = itemHandler.editSymbolItem(viewPane, symbol.getItem());
					}
					
					if(object == null){
					    object = hitTestGraphicalObject(e,diagram.getTextBoxes());
					    if(object != null){
						    TextBox box = (TextBox)object;
					        TextBoxDialog dialog = new TextBoxDialog(box,viewPane,"Edit Text Box");
					        dialog.setVisible(true);
					        wasEdited = dialog.wasEdited();
					    }
					}

					if(object == null){
					    object = hitTestGraphicalObject(e,diagram.getImages());
					    if(object != null){
						    // Not sure what to do with an image!
					    }
					}
					
					if(object == null){
						// Double click on background
						addSymbol(e);
					}
					
					if(wasEdited){
						viewPane.repaint();
					}
					
				}
			}
		}catch(Throwable t) {
			new ExceptionDisplay(viewer,t);
		}
	}
	


    /* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		try{
		    selected = getSelectedGraphicalObject(e);
		    if(selected != null){
				float zoom = viewPane.getZoom();
		        selected.onSelect(e.getX(),e.getY(),zoom);
		        selectedObjects.add(selected);
		        clearSelectedExcept(selected);
		        
		        if(selectedConnector != null){
		            selectedConnector.clearSelect();
		            selectedConnector = null;
		        }
		    } else {
		        clearSelectedExcept(null);
		        selectConnector(e);
		    }
		    
	    
		}catch(Throwable t) {
			new ExceptionDisplay(viewer,t);
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		try{
			if(e.isShiftDown()) {
				if(selected != null && selected instanceof Symbol) {
					StandardDiagram diagram = viewPane.getDiagram();

					Symbol target = (Symbol)hitTestGraphicalObject(e,diagram.getSymbols());
					if(target != null) {
						joinSymbols((Symbol)selected,target);
						viewPane.refresh();
					}
				}
			} else {
				// Normal drag - if dragged out of the window may want to adjust scroll bars.
				if(!viewPane.contains(e.getX(), e.getY())){
					viewPane.revalidate();
				}
				
			}
			
			rubberBand.clearBand(viewPane.getGraphics());
			//selected = null;
		} catch(Exception ex){
			new ExceptionDisplay(viewer,ex);
		} 
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e){ 
		float zoom = viewPane.getZoom();
		try{
			if(e.isShiftDown()) {
			    /* Then connecting 2 objects so, providing one is selected,
			     * start a rubber-band from that object.
			     */
				if(selected != null) {
					int startx = Math.round(selected.getX() * zoom);
					int starty = Math.round(selected.getY() * zoom);
					int x = e.getX(); // Math.round(e.getX() / zoom);
					int y = e.getY(); // Math.round(e.getY() / zoom);
					Graphics g = viewPane.getGraphics();
					rubberBand.drawBand(g,startx,starty,x,y);
				}
			} else {
			    /* Just dragging the selected object */
				if(selected != null) {
					selected.onDrag(e.getX(),e.getY(),zoom);
					if(!viewPane.contains(e.getX(), e.getY())){
						viewPane.revalidate();
						Rectangle2D.Float bounds = selected.getExtendedBounds(zoom);
						viewPane.scrollRectToVisible(new Rectangle(
							(int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height));
					}
					
					viewPane.repaint();
				} else if(selectedConnector != null) {
					selectedConnector.onDrag(e.getX(), e.getY(), zoom);
					viewPane.repaint();
				}
			}
		} catch(Exception ex){
			new ExceptionDisplay(viewer,ex);
		} 
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
	}
	
    /**
     * Gets the currently selected symbol (if any).
     * @return Returns the selected symbol or null if none selected.
     */
    public Symbol getSelectedSymbol() {
        if(selected instanceof Symbol){
            return (Symbol)selected;
        } 
        return null;
    }
    
    /**
     * Gets the currently selected GraphicalObject (if any).
     * @return the currently selected GraphicalObject or <code>null</code>
     * if none selected.
     */
    public GraphicalObject getSelectedObject(){
        return selected;
    }
    
    /**
     * Gets the currently selected Connector (if any).
     * @return Returns the selectedConnector or null if <code>none</code> selected.
     */
    public Connector getSelectedConnector() {
        return selectedConnector;
    }
}