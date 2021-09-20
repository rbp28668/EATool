/*
 * TimeDiagramViewerMouseHandler.java
 * Project: EATool
 * Created on 27-Jan-2007
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.PositionalPopup;
import alvahouse.eatool.gui.graphical.EventErrorHandler;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.ScriptContext;
import alvahouse.eatool.repository.scripting.ScriptManager;
import alvahouse.eatool.scripting.proxy.ScriptWrapper;
import alvahouse.eatool.util.SettingsManager;

/**
 * TimeDiagramViewerMouseHandler
 * 
 * @author rbp28668
 */
public class TimeDiagramViewerMouseHandler extends MouseInputAdapter {

	private final TimeDiagramViewer viewer;
	private TimeBar selected = null;
    private SettingsManager.Element cfg;
    private Application app; 
    private Repository repository;

    /**
     * 
     */
    public TimeDiagramViewerMouseHandler(TimeDiagramViewer viewer, Application app, Repository repository) {
        super();
        this.viewer = viewer;
        this.app = app;
        this.repository = repository;
        
        cfg = app.getConfig().getElement("/TimeDiagramViewer/popups");

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
		try{
			if(e.getClickCount() == 1 ) {	
			    if(e.getButton() == MouseEvent.BUTTON1) {
					if(e.isControlDown()){
					    // Ctrl-click - if on selected item then edit settings
					    // of selected item else add new item.
						
					} else if (e.isShiftDown()) {
					    // Shift-click
						TimeBar bar = getSelectedTimeBar(e);
						if(bar != null) {
						    Object item = bar.getItem();
						    String event = item.getClass().getName();
						    int idx = event.lastIndexOf('.');
						    if(idx >= 0){
						        event = event.substring(idx + 1);
						    }
						    idx = event.lastIndexOf('$');
						    if(idx >= 0){
						        event = event.substring(idx + 1);
						    }
						    Object target = ScriptWrapper.wrapObject(item);
						    Object diagram = ScriptWrapper.wrap(viewer.getDiagram());
						    
						    EventMap eventMap = viewer.getDiagram().getEventMap();
						    if(eventMap.hasHandler(event)) {
						    	ScriptContext context = eventMap.getContextFor(event, repository.getScripts());
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
					    
					    TimeBar bar = getSelectedTimeBar(e);
					    if(bar != null) {
					        selected = bar.getItem();
					    }
					    
					    PositionalPopup popup = null;
		                if(selected != null) {
                            popup = getPopupFor(viewer, selected.getClass());
		                } else {
		                    popup = getBackgroundPopup(viewer);
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
					TimeBar bar = getSelectedTimeBar(e);
					if(bar != null) {
						// Double click on symbol
					    boolean wasEdited = editItem(viewer, bar.getItem());
						if(wasEdited){
							viewer.repaint();
						}
					} else {
						// Double click on background
						addBar(e);
					}
				}
			}
		}catch(Throwable t) {
			new ExceptionDisplay(viewer,t);
		}
    }
  
    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseDragged(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseEntered(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseExited(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseMoved(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mousePressed(arg0);
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        super.mouseReleased(arg0);
    }

    
	/**
	 * Gets the symbol at the current mouse position.
	 * 
	 * @param e is the MouseEvent to use for hit-testing
	 * @return if there is a symbol at the given mouse position then it
	 * is returned, otherwise null is returned.
	 */
	private TimeBar getSelectedTimeBar(MouseEvent e) {
		TimeBar selected = null;
		float zoom = viewer.getZoom();
		TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
		for(TimeBar item : diagram.getBars()){
			if(item.hitTest(e.getX(),e.getY(),zoom)){
				selected = item;
				break;
			}
		}
		return selected;
	}
    
	private PositionalPopup getPopupFor(TimeDiagramViewer viewer, Class<?> targetClass){
	    ActionSet actions = new TimeDiagramViewerActionSet(viewer, app, repository);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,targetClass);
        return popup;
	}
	
	private PositionalPopup getBackgroundPopup(TimeDiagramViewer viewer){
        ActionSet actions = new TimeDiagramViewerActionSet(viewer,app, repository);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,"Background");
       return popup;
	    
	}

	  /**
     * @param e
     */
    private void addBar(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param viewer2
     * @param item
     * @return
     */
    private boolean editItem(TimeDiagramViewer viewer2, KeyedItem item) {
        // TODO Auto-generated method stub
        return false;
    }
	
}
