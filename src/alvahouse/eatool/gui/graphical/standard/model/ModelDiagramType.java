/*
 * ModelDiagramType.java
 * Project: EATool
 * Created on 10-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.model;

import java.awt.Color;
import java.util.Iterator;

import alvahouse.eatool.repository.graphical.standard.BasicConnector;
import alvahouse.eatool.repository.graphical.standard.ConnectorType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.repository.graphical.symbols.RectangularSymbol;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.util.UUID;

/**
 * ModelDiagramType is a StandardDiagramType that is initialised to cover the complete model.
 * Used for the "big view" diagram where all the entities known to the model are shown.
 * This uses the HSB colour model to paint each entity type a different hue.
 * 
 * @author rbp28668
 */
public class ModelDiagramType extends StandardDiagramType {

    /**
     * 
     */
    public ModelDiagramType(MetaModel metaModel) {
        super();
        init(metaModel);
    }

    /**
     * @param name
     * @param uuid
     */
    public ModelDiagramType(MetaModel metaModel, String name, UUID uuid) {
        super(name, uuid);
        init(metaModel);
    }

    /**
     * This sets up a diagram type with each meta entity given a different colour.
     * @param metaModel is the meta-model that is used to set up a complete set
     * of symbol types and connector types.
     */
    private void init(MetaModel metaModel) {
        
        float h = 0.0f;
        float s = 0.3f;
        float b = 1.0f;
        
        float dh = 1.0f / (1.0f + metaModel.getMetaEntityCount());
        
        for(Iterator iter = metaModel.getMetaEntities().iterator(); iter.hasNext();){
            MetaEntity me = (MetaEntity)iter.next();
            SymbolType st = new SymbolType(new UUID(),me,RectangularSymbol.class, me.getName());
            st.setBackColour(Color.getHSBColor(h, s, b));
            h += dh;
            
            add(st);
        }
        
        for(Iterator iter = metaModel.getMetaRelationships().iterator(); iter.hasNext();){
            MetaRelationship mr = (MetaRelationship) iter.next();
            ConnectorType ct = new ConnectorType(new UUID(), mr, BasicConnector.class, mr.getName());
            add(ct);
        }
        
        EventMap eventMap = getEventMap();
        eventMap.clear();
	    eventMap.addEvent(StandardDiagram.ON_DISPLAY_EVENT);
	    eventMap.addEvent(StandardDiagram.ON_CLOSE_EVENT);
	    eventMap.addEvent("Entity");
	    eventMap.addEvent("Relationship");
        
    }

}
