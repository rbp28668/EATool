/*
 * StandardDiagramViewerProxy.java
 * Project: EATool
 * Created on 9 Dec 2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.gui.graphical.standard.model.ModelViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.scripting.proxy.ScriptWrapper;
import alvahouse.eatool.scripting.proxy.Scripted;
import alvahouse.eatool.scripting.proxy.StandardDiagramProxy;

@Scripted(description="Viewer for a standard diagram.")
public class StandardDiagramViewerProxy {

    private StandardDiagramViewer viewer = null;
    private final StandardDiagram diagram;
    private final Application app;
    private final Repository repository;
    
    StandardDiagramViewerProxy(StandardDiagram diagram, Application app, Repository repository){
        this(null, diagram, app, repository);
    }

    public StandardDiagramViewerProxy(StandardDiagramViewer viewer, StandardDiagram diagram, Application app, Repository repository){
    	this.viewer = viewer; // may be null if not displayed
    	this.diagram = diagram;
        this.app = app;
        this.repository = repository;
    }

    @Scripted(description="Gets the underlying standard diagram.")
    public StandardDiagramProxy getDiagram() {
    	return ScriptWrapper.wrap(diagram);
    }
    
    /**
     * Displays the diagram in a window.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Displays the diagram in a window if not already displayed.")
    public void show() throws InterruptedException, InvocationTargetException{
        if(viewer == null){
            EventQueue.invokeAndWait( new Runnable(){
                public void run(){
                    WindowCoordinator wc = app.getWindowCoordinator();
                    viewer = (StandardDiagramViewer)wc.getFrame(diagram.getKey().toString(), new ModelViewer.WindowFactory( diagram, app, repository));             
                    viewer.refresh();
                    viewer.show();
                }
            });
        }

    }
    
    /**
     * Hides the window of a diagram.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Hides the diagram if visible.")
    public void hide() throws InterruptedException, InvocationTargetException{
        if(viewer != null){
            EventQueue.invokeAndWait( new Runnable(){
                public void run(){
                    viewer.setVisible(false);
                    viewer.dispose();
                    viewer = null;
                }
            });
        }
    }

    /**
     * Repaints the diagram.
     */
    @Scripted(description="Repaints the diagram.")
    public void repaint() {
        if(viewer != null) {
            viewer.repaint();
        }
    }
    
    /**
     * Runs the action on a diagram.  Note that the diagram must
     * be displayed in a window (produced by show) for this to 
     * work.
     * @param actionName is the name of the action to run.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Runs the action with the given name."
    		+ " Note the diagram must be displayed otherwise this will do nothing.")
    public void runAction(String actionName) throws InterruptedException, InvocationTargetException{
        if(viewer != null){
            EventQueue.invokeAndWait( new ActionRunner(actionName));
        }
    }
    
    private class ActionRunner implements Runnable {

        private String actionName;
        
        ActionRunner(String actionName){
            this.actionName = actionName;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            ActionSet actions = viewer.getActions();
            Action action = actions.getAction(actionName);
            if(action != null){
                ActionEvent evt = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,actionName);
                action.actionPerformed(evt);
            }
        }
        
    }

}
