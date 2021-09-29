package alvahouse.eatool.repository.graphical.standard;

import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.graphical.ConnectorTypeDto;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRelationshipProxy;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Models a type of a connector used in a diagram.  It does this primarily
 * by tying together a MetaRelationship and the rendering class used to
 * draw instances of that relationship on the diagram.
 * @author bruce.porteous
 *
 */
public class ConnectorType extends NamedRepositoryItem{

	private Class<? extends Connector> connectorClass = null;
	private MetaRelationshipProxy represents = new MetaRelationshipProxy();


	/**
	 * Constructor for ConnectorType.
	 */
	public ConnectorType(UUID key) {
		super(key);
	}
	
	/**
	 * Constructor for ConnectorType.
	 * @param represents
	 * @param connectorClass
	 * @param name
	 */
	public ConnectorType(UUID key, MetaRelationship represents, Class<? extends Connector> connectorClass, String name){
	    super(key);
	    
		if(represents == null) {
			throw new NullPointerException("Null value for represented meta-relationship");
		}
		if(connectorClass == null) {
			throw new NullPointerException("Null value for connector class");
		}
		
		this.represents.set(represents);
		this.connectorClass = connectorClass;
		setName(name);
	}

	/**
	 * Constructor for ConnectorType for creating Connectors for MetaRelationships.
	 * @param connectorClass
	 * @param name
	 */
	public ConnectorType(Class<? extends Connector> connectorClass, String name){
	    super(new UUID());
	    
		if(connectorClass == null) {
			throw new NullPointerException("Null value for connector class");
		}
		
		this.represents = null;
		this.connectorClass = connectorClass;
		setName(name);
	}

	@SuppressWarnings("unchecked")
	public ConnectorType(ConnectorTypeDto dto) throws Exception {
		super(dto);
		this.represents.setKey(dto.getMetaRelationshipKey());
		this.connectorClass = (Class<? extends Connector>) Class.forName(dto.getConnectorClass());
	}
	
	public ConnectorTypeDto toDto() {
		ConnectorTypeDto dto = new ConnectorTypeDto();
		copyTo(dto);
		return dto;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		ConnectorType copy = new ConnectorType(getKey());
		cloneTo(copy);
		return copy;
	}
	
	/**
	 * updateFromCopy updates this connector type from a copy.  It's the inverse
	 * of clone and used when updating from an edited copy.
	 * @param copy is the ConnectorType to update from.
	 */
	public void updateFromCopy(ConnectorType copy){
		copy.cloneTo(this);
	}
	
	/**
	 * Method newConnector.
	 * @param key is the UUID that should identify this connector.
	 * @return Connector
	 * @throws LogicException
	 */
	public Connector newConnector(UUID key)
	throws LogicException {
		try {
			Connector con = (Connector)connectorClass.newInstance();
			con.setType(this);
			con.setKey(key);
			return con;
		} catch (Throwable t) {
			throw new LogicException("Unable to create connector " + getName(), t);
		}
		
	}

	/**
	 * Returns the name - if there is a name given to this connector type then
	 * use it, otherwise, use the meta-entities name.
	 * @return String
	 */
	public String getName(MetaModel mm) throws Exception {
		String name = super.getName();
		if(name != null) {
			return name;
		} else {
			return represents.get(mm).getName();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Returns the represents.
	 * @return MetaEntity
	 */
	public MetaRelationship getRepresents(MetaModel mm) throws Exception {
		return represents.get(mm);
	}

	/**
	 * Gets the key of the meta relationship that this connector type draws relationships for. 
	 * @return
	 */
	public UUID getRepresentsKey() {
		return represents.getKey();
	}
	
	/**
	 * Returns the Class of the ConnectorClass used to render this connector type.
	 * @return Class
	 */
	public Class<? extends Connector> getRenderClass() {
		return connectorClass;
	}


	/**
	 * Sets the represents.
	 * @param represents The represents to set
	 */
	public void setRepresents(MetaRelationship represents) {
		if(represents == null) {
			throw new NullPointerException("Null value for represented meta-relationship");
		}
		this.represents.set(represents);
	}

	/**
	 * Sets the class used to render this symnbol type.
	 * @param connectorClass The connectorClass to set
	 */
	public void setRenderClass(Class<? extends Connector> connectorClass) {
		if(connectorClass == null) {
			throw new NullPointerException("Null value for connector class");
		}
		
		if(!Connector.class.isAssignableFrom(connectorClass)) {
			throw new IllegalArgumentException("Connector Types's Rendering class must be Connector or a subclass of Connector");
		}
		this.connectorClass = connectorClass;
	}

	/**
	 * Writes the connector type out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("ConnectorType");
		writeAttributesXML(out);
		out.addAttribute("represents", represents.getKey().toString());
		out.addAttribute("renderClass", connectorClass.getName());
		out.stopEntity();
	}

	protected void cloneTo(ConnectorType copy){
		super.cloneTo(copy);
		copy.connectorClass = connectorClass;
		copy.represents = represents;
	}
	
	protected void copyTo(ConnectorTypeDto dto) {
		super.copyTo(dto);
		dto.setConnectorClass(connectorClass.getName());
		dto.setMetaRelationshipKey(represents.getKey());
	}

 
}
	


