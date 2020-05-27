/*
 * StandardDiagram.java
 * Project: EATool
 * Created on 06-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.ImageDisplay;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.util.UUID;


/**
 * StandardDiagram is a proxy for the normal StandardDiagram class to provide a facade
 * for scripting.
 * 
 * @author rbp28668
 */
@Scripted(description="Standard boxes and line diagram.")
public class StandardDiagramProxy {

    private final StandardDiagram diagram;
    
    /**
     * Creates a StandardDiagram attached to the given underlying diagram.
     * @param diagram is the underlying diagram.
     */
    public StandardDiagramProxy(StandardDiagram diagram) {
        super();
        this.diagram = diagram;
    }

    public alvahouse.eatool.repository.graphical.standard.StandardDiagram getDiagram(){
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
    
    @Scripted(description="Sets the default colours on all the symbols on the diagram.")
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
    @Scripted(description="Sets the title of the diagram.")
    public void setTitle(String title){
        diagram.setName(title);
    }
    
    
    /**
     * Adds the entities to the diagram if they are valid for
     * that diagram as determined by the diagram's diagram type.
     * @param entities is the EntitySet containing the entites to add.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Adds all the entities from the given set that are valid"
    		+ " for this diagram according to the diagram type.")
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
                diagram.addNodeForObject(e);
            }
        }
        
    }
    
    /**
     * Removes the entities from the diagram if they are in the diagram.
     * @param entities is the EntitySet containing the entites to remove.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Removes a given set of entities from the diagram if they are in the diagram.")
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
     * Colour the entities on the digram.
     * @param entities is the EntitySet containing the entites to colour.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Colours the given set of entities the colour determined by the red/green/blue parameters (each 0-255).")
    public void colour(EntitySet entities, int red, int green, int blue) throws InterruptedException, InvocationTargetException{
        Color colour = new Color(red,green,blue);
        EventQueue.invokeAndWait( new ColourEntitiesRunner(entities,colour));
    }
    
    private class ColourEntitiesRunner implements Runnable {

        private EntitySet entities;
        private Color colour;
        
        ColourEntitiesRunner(EntitySet entities, Color colour){
            this.entities = entities;
            this.colour = colour;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            for(Entity e : entities.getContents()){
                diagram.setSymbolColourFor(e,colour);
            }
        }
        
    }

    /**
     * Colour a given entity on the digram.
     * @param entity is the Entity to colour.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Colours the given entity the colour determined by the red/green/blue parameters (each 0-255).")
    public void colourEntity(EntityProxy entity, int red, int green, int blue) throws InterruptedException, InvocationTargetException{
        Color colour = new Color(red,green,blue);
        EventQueue.invokeAndWait( new ColourEntityRunner(entity.get(),colour));
    }
    
    private class ColourEntityRunner implements Runnable {

        private Entity entity;
        private Color colour;
        
        ColourEntityRunner(Entity entity, Color colour){
            this.entity = entity;
            this.colour = colour;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            diagram.setSymbolColourFor(entity,colour);
        }
        
    }

    
    /**
     * Adds all possible connectors between the nodes already on
     * the diagram.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Adds all possible connectors between the nodes already on the diagram.")
    public void addConnectors() throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new Runnable(){
            public void run(){
                diagram.addConnectors();
            }
        });
    }
    
    /**
     * Adds all possible connectors between the nodes already on the 
     * diagram providing their types are given in the set of types.
     * @param types is a MetaRelationshipSet that determines which
     * types of connectors to add to the diagram.
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    @Scripted(description="Adds all possible connectors between the nodes already on the" + 
    		" diagram providing their types are given in the set of types.")
    public void addConnectorsOf(MetaRelationshipSet types) throws InterruptedException, InvocationTargetException{
        EventQueue.invokeAndWait( new AddConnectorsRunner(types));
    }
    
    private class AddConnectorsRunner implements Runnable {

        private MetaRelationshipSet types;
        
        AddConnectorsRunner(MetaRelationshipSet types){
            this.types = types;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            diagram.addConnectors(types.getContents());
        }
        
    }

	/**
	 * Set or clear the dynamic flag for the diagram.  A dynamic diagram is one where
	 * the contents are set by a script when it is displayed so any connectors or
	 * symbols should not be saved with the diagram.
	 * @param dynamic is the new dynamic state to set.
	 */
    @Scripted(description="Set or clear the dynamic flag for the diagram. "
    		+ " A dynamic diagram is one where the contents are set by a"
    		+ " script when it is displayed so any connectors or" 
    		+ " symbols should not be saved with the diagram.")
    public void setDynamic(boolean dynamic){
        diagram.setDynamic(dynamic);
    }
    
    /**
     * Adds an image to the diagram and returns an ImageDisplay which can
     * subsequently be used to manipulate the image on the diagram.
     * @param image is the image to add.
     * @return is the ImageDisplay that captures the image position on the diagram.
     */
    @Scripted(description="Adds an image to the diagram and returns an ImageDisplay which can" + 
    		" subsequently be used to manipulate the image on the diagram.")
    public ImageDisplayProxy addImage(ImageProxy image){
        ImageDisplay display = new ImageDisplay(new UUID());
        display.setImage(image.getImage());
        diagram.addImage(display);
        return new ImageDisplayProxy(display);
        
    }
    
    /**
     * Gets the set of all the entities that appear on the diagram
     * @return an EntitySet with all the entities.
     */
    @Scripted(description="Gets the set of all the entities that appear on the diagram.")
    public EntitySet entities() {
    	EntitySet set = new EntitySet();
    	for(Symbol symbol : diagram.getSymbols()) {
    		set.add((Entity)symbol.getItem());
    	}
    	return set;
    }
    
    /**
     * Gets the set of all the relationships that appear on the diagram
     * @return a RelationshipSet with all the relationships.
     */
    @Scripted(description="Gets the set of all the relationships that appear on the diagram.")
    public RelationshipSet relationships() {
    	RelationshipSet set = new RelationshipSet();
    	for(Connector connector : diagram.getConnectors()) {
    		set.add((Relationship)connector.getItem());
    	}
    	return set;
    }

}
