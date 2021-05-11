/*
 * MetaRelationshipEnd.java
 *
 * Created on 10 January 2002, 21:54
 */

package alvahouse.eatool.repository.metamodel;

import java.io.IOException;

import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * This describes a role at the end of a relationship. MetaRoles act as the glue
 * to link MetaRelationships to MetaEntities and also describe the allowed
 * multiplicity of that end of the relationship.
 * 
 * @author rbp28668
 */
public class MetaRole extends MetaPropertyContainer {

	/**
	 * The parent MetaRelationship - this MetaRole describes one end of the
	 * relationship
	 */
	private MetaRelationship metaRelationship = null;
	/** Multiplicity of this relationship */
	private Multiplicity multiplicity = Multiplicity.fromString("one");
	/**
	 * The MetaEntity this MetaRole (and hence the parent MetaRelationship) connects
	 * to.
	 */
	private MetaEntityProxy connection = null;

	/**
	 * Creates new MetaRole with the given UUID and parent meta-relationship.
	 * 
	 * @param uuid is the identifier to use for this MetaRole.
	 */
	public MetaRole(MetaRelationship mr, UUID uuid) {
		super(uuid);
		if (mr == null) {
			throw new NullPointerException("Can't create MetaRole with a null MetaRelationship");
		}
		metaRelationship = mr;
		connection = new MetaEntityProxy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		MetaRole copy = new MetaRole(metaRelationship, getKey());
		cloneTo(copy);
		return copy;
	}

//    /**
//     * Updates this MetaRole from a copy.  Typically the copy will have been
//     * cloned, edited and is now being used to update the main meta-model.
//     * This fires  MetaModelChangeEvent on the meta model.
//     * @param copy is the copy to be updated from.
//     */
//    public void updateFromCopy(MetaRole copy) {
//        // copy back maintaining same parent.
//        MetaRelationship parent = metaRelationship;
//        copy.cloneTo(this);
//        metaRelationship = parent;
//     }

	/**
	 * Gets the allowed multiplicity for this meta-role.
	 * 
	 * @return the multiplicity.
	 */
	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	/**
	 * Sets the allowed multiplicity for this meta-role.
	 * 
	 * @param m is the multiplicity to set.
	 */
	public void setMultiplicity(Multiplicity m) {
		if (m == null) {
			throw new NullPointerException("Cannot set null multiplicity on MetaRole");
		}
		multiplicity = m;
	}

	/**
	 * Connects this MetaRole to a MetaEntity.
	 * 
	 * @param connection is the MetaEntity to connect to.
	 */
	public void setConnection(MetaEntity connection) {
		if (connection == null) {
			throw new NullPointerException("Cannot set null connection to MetaEntity on MetaRole");
		}
		this.connection.set(connection);
	}

	/**
	 * Sets the key for the meta-entity this meta-role connects to.
	 * 
	 * @param key is the non-null meta entity key.
	 */
	public void setConnectionKey(UUID key) {
		if (key == null) {
			throw new NullPointerException("Must set a non-null connection key on MetaRole");
		}
		this.connection.setKey(key);
	}

	/**
	 * Gets the MetaEntity this MetaRole connects to.
	 * 
	 * @return the associated MetaEntity.
	 */
	public MetaEntity connectsTo() throws Exception {
		return connection.get(metaRelationship.getModel());
	}

	/**
	 * Gets the key of the meta entity that this meta role connects to. 
	 * Doesn't need to go to the repository for this.
	 * @return
	 */
	public UUID connectionKey() {
		return connection.getKey();
	}
	/**
	 * Writes the MetaRole out as XML.
	 * 
	 * @param out is the XMLWriterDirect to write the XML to.
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("MetaRole");
		super.writeAttributesXML(out);
		out.addAttribute("multiplicity", multiplicity.toString());
		out.addAttribute("connects", connection.getKey().toString());
		super.writeXML(out);
		out.stopEntity();
	}

	/**
	 * Clones this MetaRole to a copy.
	 * 
	 * @param copy is the copy that will be modified to be a clone of this MetaRole.
	 */
	protected void cloneTo(MetaRole copy) {
		super.cloneTo(copy);
		copy.multiplicity = multiplicity;
		copy.connection = (MetaEntityProxy) connection.clone();
	}

	/**
	 * Gets the MetaRelationship this MetaRole belongs to.
	 * 
	 * @return the parent MetaRelationship.
	 */
	public MetaRelationship getMetaRelationship() {
		return metaRelationship;
	}

	/**
	 * Sets the parent MetaRelationhip.
	 * 
	 * @param mr is the parent to set.
	 */
	public void setMetaRelationship(MetaRelationship mr) {
		if (mr == null) {
			throw new NullPointerException("Cannot set null MetaRelationship on MetaRole");
		}
		metaRelationship = mr;
	}

}
