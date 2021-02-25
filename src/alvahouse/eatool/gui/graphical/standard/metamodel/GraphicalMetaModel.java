/*
 * GraphicalMetaModel.java
 *
 * Created on 12 February 2002, 16:57
 */

package alvahouse.eatool.gui.graphical.standard.metamodel;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.gui.graphical.layout.Node;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.GraphicalProxy;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaModelChangeListener;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.UUID;
/**
 * GraphicalMetaModel provides a basic graphical display of 
 * the meta model by including all the MetaEntities and MetaRelationships.
 * @author  rbp28668
 */
public class GraphicalMetaModel extends StandardDiagram {

    private final MetaModel metaModel;
    private final Repository repository;

    /** Creates new GraphicalMetaModel */
    public GraphicalMetaModel(Repository rep, MetaModel mm, StandardDiagramType diagramType, UUID key) throws Exception {
    	super(diagramType, key);
        repository = rep;
        metaModel = mm;
        setDynamic(true);
        update();
        metaModel.addChangeListener(changeListener);
    }

    public void dispose() {
        metaModel.removeChangeListener(changeListener);
    }
    
    public void update()  throws Exception{
        reset();
        for(MetaEntity me : metaModel.getMetaEntities()) {
            addMetaEntity(me);
        }

        for(MetaRelationship mr : metaModel.getMetaRelationships()) {
            addMetaRelationship(mr);
        }
    }

    
    /* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.Diagram#scriptsUpdated()
	 */
	@Override
	public void scriptsUpdated() {
		getEventMap().cloneTo(repository.getMetaModelViewerEvents());
	}

	private GraphicalProxy addMetaEntity(MetaEntity me) {
        return addNodeForObject(me);
    }
    
    private Arc addMetaRelationship(MetaRelationship mr) throws Exception{
        return addArcForObject(mr, mr.start().connectsTo(), mr.finish().connectsTo());
    }
    
    private MetaModelChangeListener changeListener = new MetaModelChangeAdapter() {
        /** signals that a meta entity has been added
         * @ param e is the event that references the object being changed
         */
        public void metaEntityAdded(MetaModelChangeEvent e) {
            MetaEntity me = (MetaEntity)e.getSource();
            addMetaEntity(me);
        }
    
        /** signals that a meta entity has been deleted
         * @ param e is the event that references the object being changed
         */
        public void metaEntityDeleted(MetaModelChangeEvent e) {
            MetaEntity me = (MetaEntity)e.getSource();
            System.out.println("Delete event for " + me.toString());
            Node node = lookupNode(me);
            deleteNode(node);
        }

        /** signals that a meta relationship has been added
         * @ param e is the event that references the object being changed
         */
        public void metaRelationshipAdded(MetaModelChangeEvent e) throws Exception {
            MetaRelationship mr = (MetaRelationship)e.getSource();
            addMetaRelationship(mr);
        }
        
        /** signals that a meta relationship has been deleted
         * @ param e is the event that references the object being changed
         */
        public void metaRelationshipDeleted(MetaModelChangeEvent e) {
            MetaRelationship mr = (MetaRelationship)e.getSource();
            Connector a = lookupArc(mr);
            deleteArc(a);
        }

    };

}
