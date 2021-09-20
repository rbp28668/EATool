/*
 * MetaModelDiagramType.java
 * Project: EATool
 * Created on 10-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.metamodel;

import java.awt.Color;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.BasicConnector;
import alvahouse.eatool.repository.graphical.standard.ConnectorType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.repository.graphical.symbols.RectangularSymbol;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.util.UUID;

/**
 * MetaModelDiagramType is a special StandardDiagramType for displaying meta-models.
 * 
 * @author rbp28668
 */
public class MetaModelDiagramType extends StandardDiagramType {

    private SymbolType symbolType;
    private ConnectorType connectorType;
    private static MetaModelDiagramType instance = null;
    
    
    /**
     * 
     */
    MetaModelDiagramType(Repository repository) {
        super(repository);
        init();
    }

    /**
     * @param name
     * @param uuid
     */
    private MetaModelDiagramType(Repository repository, String name, UUID uuid) {
        super(repository, name, uuid);
        init();
    }

    /**
     * Singleton accessor.
     * @return the singleton instance of MetaModelDiagramType.
     */
    public static MetaModelDiagramType getInstance(Repository repository){
        if(instance == null){
            instance = new MetaModelDiagramType(repository, "Meta-Model Diagrams", new UUID());
        }
        return instance;
    }
    
    private final void init(){
        setName("Meta Model");
        symbolType = new SymbolType(RectangularSymbol.class, "Meta Entity");
        symbolType.setBackColour(new Color(153,255,255));

        connectorType = new ConnectorType(BasicConnector.class, "Meta Relationship");
        
        defineEventMap(getEventMap());
    }

    public static void defineEventMap(EventMap eventMap) {
    	eventMap.clear();
	    eventMap.ensureEvent(StandardDiagram.ON_DISPLAY_EVENT);
	    eventMap.ensureEvent(StandardDiagram.ON_CLOSE_EVENT);
	    eventMap.ensureEvent("MetaEntity");
	    eventMap.ensureEvent("MetaRelationship");
    }
    
    /**
     * @return Returns the connectorType.
     */
    public ConnectorType getConnectorType() {
        return connectorType;
    }
    /**
     * @return Returns the symbolType.
     */
    public SymbolType getSymbolType() {
        return symbolType;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#add(alvahouse.eatool.gui.graphical.ConnectorType)
     */
    public void add(ConnectorType ct) {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#add(alvahouse.eatool.gui.graphical.SymbolType)
     */
    public void add(SymbolType st) {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#cloneTo(alvahouse.eatool.gui.graphical.StandardDiagramType)
     */
    protected void cloneTo(StandardDiagramType copy) {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#getConnectorTypeFor(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public ConnectorType getConnectorTypeFor(MetaRelationship mr) {
        if(mr != null){
            throw new UnsupportedOperationException();
        }
        return getConnectorType();
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#getSymbolTypeFor(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public SymbolType getSymbolTypeFor(MetaEntity me) {
        if(me != null){
            throw new UnsupportedOperationException();
        }
        return getSymbolType();
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#hasConnectorTypeFor(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public boolean hasConnectorTypeFor(MetaRelationship mr) {
        return mr == null;
    }
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.StandardDiagramType#hasSymbolTypeFor(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public boolean hasSymbolTypeFor(MetaEntity me) {
        return me == null;
    }

    // Note that as this is immutable we just return this.
    @Override
    public Object clone() {
    	return this;
    }
    
}
