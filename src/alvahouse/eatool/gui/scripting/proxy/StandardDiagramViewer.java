/*
 * StandardDiagramViewer.java
 * Project: EATool
 * Created on 9 Dec 2007
 *
 */
package alvahouse.eatool.gui.scripting.proxy;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.gui.graphical.standard.model.ModelViewer;
import alvahouse.eatool.repository.Repository;

public class StandardDiagramViewer {

    private alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer viewer = null;
    private alvahouse.eatool.repository.graphical.standard.StandardDiagram diagram;
    private alvahouse.eatool.Application app;
    private Repository repository;
    
    StandardDiagramViewer(alvahouse.eatool.repository.graphical.standard.StandardDiagram diagram, alvahouse.eatool.Application app, Repository repository){
        this.diagram = diagram;
        this.app = app;
        this.repository = repository;
    }
    /**
     * Displays the diagram in a window.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public void show() throws InterruptedException, InvocationTargetException{
        if(viewer == null){
            EventQueue.invokeAndWait( new Runnable(){
                public void run(){
                    WindowCoordinator wc = app.getWindowCoordinator();
                    viewer = (alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer)wc.getFrame(diagram.getKey().toString(), new ModelViewer.WindowFactory( diagram, app, repository));             
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
