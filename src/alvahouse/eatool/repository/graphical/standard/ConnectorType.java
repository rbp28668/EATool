package alvahouse.eatool.repository.graphical.standard;

import java.io.IOException;

import alvahouse.eatool.repository.base.RepositoryItem;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Models a type of a connector used in a diagram.  It does this primarily
 * by tying together a MetaRelationship and the rendering class used to
 * draw instances of that relationship on the diagram.
 * @author bruce.porteous
 *
 */
public class ConnectorType extends RepositoryItem{

	private String name = null;
	private Class connectorClass = null;
	private MetaRelationship represents = null;

	protected void cloneTo(ConnectorType copy){
		copy.name = name;
		copy.connectorClass = connectorClass;
		copy.represents = represents;
	}
	

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
	public ConnectorType(UUID key, MetaRelationship represents, Class connectorClass, String name){
	    super(key);
	    
		if(represents == null) {
			throw new NullPointerException("Null value for represented meta-relationship");
		}
		if(connectorClass == null) {
			throw new NullPointerException("Null value for connector class");
		}
		
		this.represents = represents;
		this.connectorClass = connectorClass;
		this.name = name;
	}

	/**
	 * Constructor for ConnectorType for creating Connectors for MetaRelationships.
	 * @param connectorClass
	 * @param name
	 */
	public ConnectorType(Class connectorClass, String name){
	    super(new UUID());
	    
		if(connectorClass == null) {
			throw new NullPointerException("Null value for connector class");
		}
		
		this.represents = null;
		this.connectorClass = connectorClass;
		this.name = name;
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
	public String getName() {
		if(name != null) {
			return name;
		} else {
			return represents.getName();
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
	public MetaRelationship getRepresents() {
		return represents;
	}

	/**
	 * Returns the Class of the ConnectorClass used to render this connector type.
	 * @return Class
	 */
	public Class getRenderClass() {
		return connectorClass;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		if(name == null) {
			throw new NullPointerException("Null name for ConnectorType");
		}
		this.name = name;
	}

	/**
	 * Sets the represents.
	 * @param represents The represents to set
	 */
	public void setRepresents(MetaRelationship represents) {
		if(represents == null) {
			throw new NullPointerException("Null value for represented meta-relationship");
		}
		this.represents = represents;
		if(name == null){
			name = represents.getName();
		}
	}

	/**
	 * Sets the class used to render this symnbol type.
	 * @param connectorClass The connectorClass to set
	 */
	public void setRenderClass(Class connectorClass) {
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
		if(name == null){
			name = represents.getName();
		}
		
		out.startEntity("ConnectorType");
		out.addAttribute("uuid",getKey().toString());
		out.addAttribute("represents", represents.getKey().toString());
		out.addAttribute("renderClass", connectorClass.getName());
		out.addAttribute("name", name);
		out.stopEntity();
	}


 
}
	


