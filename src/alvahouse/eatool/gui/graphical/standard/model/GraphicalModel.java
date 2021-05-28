/*
 * GraphicalModel.java
 *
 * Created on 06 March 2002, 22:05
 */

package alvahouse.eatool.gui.graphical.standard.model;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeAdapter;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.ModelChangeListener;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.UUID;
/**
 * GraphicalModel povides a basic graphical display of a model including all the
 * entities and relationships in that model.  Note that aim of this is to display
 * ALL the entities in the model.
 * @author  rbp28668
 */
public class GraphicalModel extends StandardDiagram {

    private final Model model;
    private final Repository repository;
    
    /** Creates new GraphicalModel */
    public GraphicalModel(Repository rep, Model m, StandardDiagramType diagramType, UUID key, Scripts scripts)  throws Exception{
    	super(diagramType,key, scripts);
        model = m;
        repository = rep;
        
        setDynamic(true);
        update();
        model.addChangeListener(changeListener);
    }

    public void dispose() {
        model.removeChangeListener(changeListener);
    }
     
    /** updates the graphical model from whatever the
     * underlying model is
     * @throws Exception 
     */
    public void update() throws Exception {
        reset();
        
        for(Entity e : model.getEntities()) {
            addEntity(e);
        }
        
        for(Relationship r : model.getRelationships()) {
            addRelationship(r);
        }
    }

    /* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.Diagram#scriptsUpdated()
	 */
	@Override
	public void scriptsUpdated() throws Exception {
		getEventMap().cloneTo(repository.getModelViewerEvents()); // as now changed
	}
    
    private GraphicalProxy addEntity(Entity e)  {
       	return addNodeForObject(e);
    }
    
    private Arc addRelationship(Relationship r) throws Exception {
        return addArcForObject(r, r.start().connectsTo(),r.finish().connectsTo());
    }
    
    /** Change listener keeps the diagram synchronised with any changes
     * to the underlying model.
     */
    private ModelChangeListener changeListener = new ModelChangeAdapter() {
        /** signals a major update to the  model 
         * @throws Exception 
         * @ param e is the event that references the object being changed
         */
        public void modelUpdated(ModelChangeEvent e) throws Exception {   
            update();
        }

        /*======= ENTITIES ================================================*/

        /** signals that an entity has been added
         * @ param e is the event that references the object being changed
         */
        public void EntityAdded(ModelChangeEvent evt) {
            Entity e = (Entity)evt.getSource();
            addEntity(e);
        }

        /** signals that a  entity has been changed
         * @ param e is the event that references the object being changed
         */
        public void EntityChanged(ModelChangeEvent e) {
            // currently NOP.
        }

        /** signals that a  entity has been deleted
         * @ param e is the event that references the object being changed
         */
        public void EntityDeleted(ModelChangeEvent e) {
            deleteNode(lookupNode((KeyedItem)e.getSource()));
        }

        
    };
    
    
}
