package alvahouse.eatool.repository.graphical.standard;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramDetailFactory;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeDetailFactory;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * StandardDiagramType defines a single type of diagram.  It determines (via SymbolType
 * and ConnectorType) which types of entities and relationships can appear on a 
 * diagram.
 * @author bruce.porteous
 *
 */
public class StandardDiagramType extends DiagramType {

	private List<SymbolType> symbolTypes = new LinkedList<SymbolType>();  // of SymbolType
	private List<ConnectorType> connectorTypes = new LinkedList<ConnectorType>(); // of ConnectorType
	private Map<MetaEntity,SymbolType> symbolLookup = new HashMap<MetaEntity,SymbolType>(); // by meta type
	private Map<MetaRelationship,ConnectorType> connectorLookup = new HashMap<MetaRelationship,ConnectorType>(); // by meta type
	private Map<UUID,SymbolType> symbolLookupByUUID = new HashMap<UUID,SymbolType>();
	private Map<UUID,ConnectorType> connectorLookupByUUID = new HashMap<UUID,ConnectorType>();
	
	
	
	/**
	 * Constructs a new StandardDiagramType.
	 */
	public StandardDiagramType() {
		super(new UUID());
	}

	/**
	 * Constructs an existing diagram type.
	 * @param name is the name of the diagram type.
	 * @param uuid is the unique ID of the diagram type.
	 */
	public StandardDiagramType(String name, UUID uuid) {
		super(name,uuid);
	}

	
//	/**
//	 * @param copy
//	 */
//	protected void cloneTo(StandardDiagramType copy){
//	    super.cloneTo(copy);
//		
//		// Do deep copy as child objects mutable.
//		copy.symbolTypes.clear();
//		for(SymbolType st : symbolTypes){
//			copy.symbolTypes.add((SymbolType)st.clone());
//			copy.symbolLookup.put(st.getRepresents(),st);
//			copy.symbolLookupByUUID.put(st.getKey(),st);
//		}
//		copy.connectorTypes.clear();
//		for(ConnectorType ct : connectorTypes ){
//			copy.connectorTypes.add((ConnectorType)ct.clone());
//			copy.connectorLookup.put(ct.getRepresents(),ct);
//			copy.connectorLookupByUUID.put(ct.getKey(),ct);
//		}
//		
//	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#clone()
//	 */
//	public Object clone() {
//		StandardDiagramType copy = new StandardDiagramType(getName(), getKey());
//		cloneTo(copy);
//		return copy;
//	}
//	
//	/**
//	 * updateFromCopy updates this diagram type from a copy.  It's the inverse
//	 * of clone and used when updating from an edited copy.
//	 * @param copy is the StandardDiagramType to update from.
//	 */
//	public void updateFromCopy(StandardDiagramType copy){
//		copy.cloneTo(this);
//	}
	
	/**
	 * Method add adds a symbol type to the diagram type.
	 * @param st
	 */
	public void add(SymbolType st) {
		if(st == null) {
			throw new NullPointerException("Adding null symbol type to diagram type");
		}
		if(symbolLookup.containsKey(st.getRepresents())){
			throw new IllegalStateException("StandardDiagram type already contains a symbol type for meta entity " 
			+ st.getRepresents().getName());
		}
		symbolTypes.add(st);
		symbolLookup.put(st.getRepresents(),st);
		symbolLookupByUUID.put(st.getKey(),st);
	}
	
	/**
	 * Method getSymbolTypes.
	 * @return Collection
	 */
	public Collection<SymbolType> getSymbolTypes() {
		return Collections.unmodifiableCollection(symbolTypes);
	}
	
	/**
	 * Gets the symbol type corresponding to a given meta-entity.
	 * @param me is the meta-entity to get the symbol type for.
	 * @return the corresponding symbol type.
	 * @throws NullPonterException - if me is null
	 * @throws IllegalArgumentException - if me does not correspond to a known symbol type.
	 */
	public SymbolType getSymbolTypeFor(MetaEntity me) {
		SymbolType st = (SymbolType)symbolLookup.get(me);
		if(st == null) {
			throw new IllegalArgumentException("Meta entity does not correspond to a known symbol type");
		}
		return st;
	}
	
	/**
	 * Checks whether the given meta entity has a corresponding symbol.
	 * @param me is the meta entity to check for.
	 * @return true if there is a symbol that corresponds to the meta-entity.
	 * @throws NullPonterException - if me is null
	 */
	public boolean hasSymbolTypeFor(MetaEntity me) {
		return symbolLookup.containsKey(me);
	}
	
	/**
	 * Method add.
	 * @param ct
	 */
	public void add(ConnectorType ct) {
		if(ct == null) {
			throw new NullPointerException("Adding null connector type to diagram type");
		}
		if(connectorLookup.containsKey(ct.getRepresents())){
			throw new IllegalStateException("StandardDiagram type already contains a connector type for meta relationship " 
			+ ct.getRepresents().getName());
		}
		connectorTypes.add(ct);
		connectorLookup.put(ct.getRepresents(),ct);
		connectorLookupByUUID.put(ct.getKey(),ct);
	}
	
	/**
	 * Method getConnectorTypes.
	 * @return Collection
	 */
	public Collection<ConnectorType> getConnectorTypes() {
		return Collections.unmodifiableCollection(connectorTypes);
	}
	
	/**
	 * Gets the connector type corresponding to a given relationship.
	 * @param mr is the relationship to get the connector type for.
	 * @return the corresponding connector type.
	 * @throws NullPonterException - if mr is null
	 * @throws IllegalArgumentException - if me does not correspond to a known connector type.
	 */
	public ConnectorType getConnectorTypeFor(MetaRelationship mr) {
		ConnectorType ct = (ConnectorType)connectorLookup.get(mr);
		if(ct == null) {
			throw new IllegalArgumentException("Meta relationship does not correspond to a known connector type");
		}
		return ct;
	}
	
	/**
	 * Check whether a given meta relationship has a corresponding connector
	 * @param mr is the relationship to check
	 * @return true if there isa corresponding connector type.
	 * @throws NullPonterException - if mr is null
	 */
	public boolean hasConnectorTypeFor(MetaRelationship mr) {
		return connectorLookup.containsKey(mr);
	}
	

	/**
	 * Creates a new diagram of this type.
	 * @param key is the UUID to use as a key for this diagram.
	 * @return a new diagram.
	 */
	public Diagram newDiagram(UUID key) {
		StandardDiagram diagram = new StandardDiagram(this, key);
		return diagram;
	}
	
	/**
	 * Writes the StandardDiagram type out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		startXML(out);

		for(SymbolType st : getSymbolTypes()){
			st.writeXML(out);
		}
		
		for(ConnectorType ct : getConnectorTypes()){
			ct.writeXML(out);
		}
		out.stopEntity();
	}


    /**
     * Gets a SymbolType given its key.
     * @param typeUUID is the key of the SymbolType to get.
     * @return the corresponding symbol type.
     */
    public SymbolType getSymbolType(UUID typeUUID) {
        SymbolType st = (SymbolType)symbolLookupByUUID.get(typeUUID);
        return st;
    }

    /**
     * Gets a ConnectorType given its key.
     * @param typeUUID is the key of the ConnectorType to get.
     * @return the corresponding connector type.
     */
    public ConnectorType getConnectorType(UUID typeUUID) {
        ConnectorType ct = (ConnectorType)connectorLookupByUUID.get(typeUUID);
        return ct;
    }


    /**
     * Removes all the symbol types from the StandardDiagramType. 
     */
    public void clearSymbolTypes() {
    	symbolTypes.clear();
    	symbolLookup.clear();
    	symbolLookupByUUID.clear();
    }


    /**
     * Removes all the connector types from the StandardDiagramType.
     */
    public void clearConnectorTypes() {
    	connectorTypes.clear();
    	connectorLookup.clear();
    	connectorLookupByUUID.clear();
    }


    /**
     * Removes any symbol type corresponding to the given meta entity.
     * @param meta is the meta-entity to have its symbol type removed.
     */
    public void removeSymbolsFor(MetaEntity meta) {
        SymbolType st = (SymbolType)symbolLookup.get(meta);
        if(st != null){
            symbolTypes.remove(st);
            symbolLookupByUUID.remove(st.getKey());
            symbolLookup.remove(meta);
        }
    }

    /**
     * Removes any connector type corresponding to a given meta relationship
     * @param meta is the meta-relationship to have its connector type removed.
     */
    public void removeConnectorsFor(MetaRelationship meta) {
        ConnectorType st = (ConnectorType)connectorLookup.get(meta);
        if(st != null){
            connectorTypes.remove(st);
            connectorLookupByUUID.remove(st.getKey());
            connectorLookup.remove(meta);
        }
    }


    /**
     * Checks a meta-relationship so that if it's specified in
     * the diagram type, but it's connected meta-entities are not,
     * then it is removed.
     * @param meta is the MetaRelationship to check.
     */
    public void validate(MetaRelationship meta) {
        MetaEntity start = meta.start().connectsTo();
        MetaEntity finish = meta.finish().connectsTo();
        
        if(! (symbolLookup.containsKey(start) && symbolLookup.containsKey(finish))){
            removeConnectorsFor(meta);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#getDetailFactory()
     */
    public DiagramDetailFactory getDetailFactory() {
        return new StandardDiagramFactory();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#getTypeDetailFactory()
     */
    public DiagramTypeDetailFactory getTypeDetailFactory() {
        return new StandardDiagramTypeFactory();
    }

     
}
