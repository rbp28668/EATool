/*
 * TimeDiagram.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import alvahouse.eatool.gui.graphical.time.TimeDiagram;
import alvahouse.eatool.repository.model.Entity;


/**
 * TimeDiagram is a proxy for the normal TimeDiagram class to provide a facade
 * for scripting.
 * 
 * @author rbp28668
 */
@Scripted(description="A diagram that displays items on a time-line.")
public class TimeDiagramProxy {

    private final TimeDiagram diagram;
    
    /**
     * Creates a TimeDiagram attached to the given underlying diagram.
     * @param diagram is the underlying diagram.
     */
    public TimeDiagramProxy(TimeDiagram diagram) {
        super();
        this.diagram = diagram;
    }

    public TimeDiagram getDiagram(){
        return diagram;
    }

 
    /**
     * Deletes the contents of the diagram.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Deletes the contents of the diagram.")
    public void clear() throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new Runnable(){
            public void run(){
                diagram.reset();
            }
        });
    }
    
    @Scripted(description="Sets the default colours on entities as per the diagram type.")
    public void setDefaultColours() throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new Runnable(){
            public void run(){
                diagram.resetPropertiesToDefaults();
            }
        });
    }
    
    /**
     * Sets the diagram name.
     * @param title is the new name of the diagram.
     */
    @Scripted(description="Sets the diagram title.")
    public void setTitle(String title)  throws Exception{
        diagram.setName(title);
    }
    
    
    /**
     * Adds the entities to the diagram if they are valid for
     * that diagram as determined by the diagram's diagram type.
     * @param entities is the EntitySet containing the entites to add.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Adds the given set of entities to the diagram. Entities will only"
    		+ " be added if they're included in the diagram type.")
    public void add(EntitySet entities) throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new AddEntitiesRunner(entities));
    }
    
    private class AddEntitiesRunner implements Runnable {

        private EntitySet entities;
        
        AddEntitiesRunner(EntitySet entities){
            this.entities = entities;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            for(Entity e : entities.getContents()){
                try {
					diagram.addNodeForObject(e);
				} catch (Exception e1) {
					// TODO - not clear there's a way of handling this exception.
					e1.printStackTrace();
				}
            }
        }
        
    }
    
    /**
     * Removes the entities from the diagram if they are in the diagram.
     * @param entities is the EntitySet containing the entites to remove.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Removes the entities in the set from the diagram.")
    public void remove(EntitySet entities) throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new RemoveEntitiesRunner(entities));
    }
    
    private class RemoveEntitiesRunner implements Runnable {

        private EntitySet entities;
        
        RemoveEntitiesRunner(EntitySet entities){
            this.entities = entities;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            for(Entity e : entities.getContents()){
                diagram.removeNodeForObject(e);
            }
        }
        
    }

 
	/**
	 * Set or clear the dynamic flag for the diagram.  A dynamic diagram is one where
	 * the contents are set by a script when it is displayed so any connectors or
	 * symbols should not be saved with the diagram.
	 * @param dynamic is the new dynamic state to set.
	 */
    @Scripted(description="Set or clear the dynamic flag for the diagram. "
    		+ " A dynamic diagram is one where the contents are set by a script"
    		+ " when it is displayed so any connectors or" 
    		+ "	symbols should not be saved with the diagram.")
    public void setDynamic(boolean dynamic){
        diagram.setDynamic(dynamic);
    }
    
 
}
