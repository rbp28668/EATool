/*
 * GraphicalMetaModel.java
 *
 * Created on 12 February 2002, 16:57
 */

package alvahouse.eatool.gui.graphical.standard.metamodel;

import java.util.Iterator;

import alvahouse.eatool.gui.graphical.layout.Arc;
import alvahouse.eatool.gui.graphical.layout.Node;
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

    /** Creates new GraphicalMetaModel */
    public GraphicalMetaModel(MetaModel mm, StandardDiagramType diagramType, UUID key) {
    	super(diagramType, key);
        
        metaModel = mm;
        update();
        metaModel.addChangeListener(changeListener);
    }

    public void dispose() {
        metaModel.removeChangeListener(changeListener);
    }
    
    public void update() {
        reset();
        Iterator iter = metaModel.getMetaEntities().iterator();
        while(iter.hasNext()) {
            MetaEntity me = (MetaEntity)iter.next(); 
            addMetaEntity(me);
        }
        
        iter = metaModel.getMetaRelationships().iterator();
        while(iter.hasNext()) {
            MetaRelationship mr = (MetaRelationship)iter.next();
            addMetaRelationship(mr);
        }
    }

    
    private GraphicalProxy addMetaEntity(MetaEntity me) {
        return addNodeForObject(me);
    }
    
    private Arc addMetaRelationship(MetaRelationship mr) {
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
        public void metaRelationshipAdded(MetaModelChangeEvent e) {
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

    private MetaModel metaModel;
}
