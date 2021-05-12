/*
 * Relationship.java
 *
 * Created on 11 January 2002, 01:36
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;

import alvahouse.eatool.repository.base.TooltipProvider;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Relationship models a relationship between to Entities. The Entities are
 * linked by means of Roles. The Relationship is directional hence the 2 Roles
 * are labelled start and finish.
 * 
 * @author rbp28668
 */
public class Relationship extends PropertyContainer implements TooltipProvider, Versionable {

	private MetaRelationship meta;
	private Model model; // that this belongs to
	private Role ends[] = new Role[2];
	private VersionImpl version = new VersionImpl();

	/**
	 * Method Relationship creates a new relationship corresponding to an existing
	 * relationship where the UUID is known ( such as from persistent store). This
	 * assumes that the roles will be added later by the persistence mechanism).
	 * 
	 * @param uuid is the key to the existing relationship.
	 * @param mr   is the corresponding meta-relationship.
	 */
	public Relationship(UUID uuid, MetaRelationship mr) throws Exception {
		super(uuid);
		addDefaultProperties(mr);
		meta = mr;
	}

	/**
	 * 
	 * @param uuid
	 */
	private Relationship(UUID uuid) {
		super(uuid);
	}

	/**
	 * Method Relationship creates a new, disconnected relationship.
	 * 
	 * @param mr is the corresponding meta-relationship.
	 */
	public Relationship(MetaRelationship mr) throws Exception {
		super(new UUID());
		addDefaultProperties(mr);
		meta = mr;
		ends[0] = new Role(new UUID(), meta.start());
		ends[1] = new Role(new UUID(), meta.finish());

	}

//    /** sets the meta Relationship that this Relationship is an instance of
//     * @param me is the associated meta Relationship
//     */
//    public void setMeta(MetaRelationship mr) {
//        meta = mr;
//    }

	/**
	 * gets the meta Relationship that this Relationship is an instance of
	 * 
	 * @return the associated meta Relationship
	 */
	public MetaRelationship getMeta() {
		return meta;
	}

	/**
	 * gets the string representation of the relationship.
	 * 
	 * @return the relationship's string representation.
	 */
	public String toString() {
		String decode = "UNKNOWN";
		try {
			decode = start().getMeta().getName() + ": " + start().connectsTo().toString() + " related to "
					+ finish().getMeta().getName() + ": " + finish().connectsTo().toString();
		} catch (Exception e) {
			decode = start().getMeta().getName() + ": UNKNOWN related to " + finish().getMeta().getName() + ": UNKNOWN";
		}
		return decode;
	}

	/**
	 * Creates a copy of the relationship
	 * 
	 * @return a new relationship (with the same key)
	 */
	public Object clone() {
		Relationship copy = new Relationship(getKey());
		cloneTo(copy);
		return copy;
	}

//    /** Updates this meta entity from a copy.  Used for editing - the 
//     * copy should be edited (use clone to get the copy) and only if the
//     * edit is successful should the original be updated by calling this
//     * method on the original meta-entity.  This ensures that any listeners
//     * are updated properly.
//     * @param copy is the meta-entity to copy from
//     */
//    public void updateFromCopy(Relationship copy) {
//        Model parent = getModel();
//        copy.cloneTo(this);
//        setModel(parent);
//       
//        if(model != null)
//            model.fireRelationshipChanged(this);
//    }

	/**
	 * Writes the Relationship out as XML
	 * 
	 * @param out is the XMLWriterDirect to write the XML to
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("Relationship");
		out.addAttribute("uuid", getKey().toString());
		out.addAttribute("instanceof", meta.getKey().toString());
		version.writeXML(out);
		super.writeXML(out);
		start().writeXML(out);
		finish().writeXML(out);
		out.stopEntity();
	}

	/**
	 * Gets a role by uuid.
	 * 
	 * @param key is the key for the role
	 * @return the role
	 */
	public Role getRole(UUID key) {
		// Return any match of existing role
		for (int i = 0; i < 2; ++i) {
			if (ends[i] != null) {
				if (ends[i].getKey().equals(key))
					return ends[i];
			}
		}
		return null;
	}

	/**
	 * sets the role in the first empty role in this relation
	 * 
	 * @param r is the role to set
	 * @return the role
	 */
	public Role setRole(Role r) {
		// Now look for free slot & create new
		for (int i = 0; i < 2; ++i) {
			if (ends[i] == null) {
				ends[i] = r;
				ends[i].setRelationship(this);
				return ends[i];
			} else if (ends[i].getKey().equals(r.getKey())) {
				ends[i] = r;
				ends[i].setRelationship(this);
				return ends[i];
			}
		}
		throw new IllegalArgumentException("Unable to add new role to relationship in model");
	}

	public Role start() {
		return ends[0];
	}

	public Role finish() {
		return ends[1];
	}

	/**
	 * Sets the Role at the start of the relationship.
	 * 
	 * @param r is the Role to set.
	 * @throws NullPointerException     if r is null.
	 * @throws IllegalArgumentException if r is of the wrong type as determined by
	 *                                  the metamodel.
	 */
	public void setStart(Role r) {
		if (r == null) {
			throw new NullPointerException("Can't set a null start role on a relationship");
		}

		if (!r.getMeta().equals(getMeta().start())) {
			throw new IllegalArgumentException("Adding start role of wrong type - expected "
					+ getMeta().start().getName() + ", got " + r.getMeta().getName());
		}
		ends[0] = r;
	}

	/**
	 * Sets the Role at the finish of the relationship.
	 * 
	 * @param r is the Role to set.
	 * @throws NullPointerException     if r is null.
	 * @throws IllegalArgumentException if r is of the wrong type as determined by
	 *                                  the metamodel.
	 */
	public void setFinish(Role r) {
		if (r == null) {
			throw new NullPointerException("Can't set a null finish role on a relationship");
		}

		if (!r.getMeta().equals(getMeta().finish())) {
			throw new IllegalArgumentException("Adding start role of wrong type - expected "
					+ getMeta().finish().getName() + ", got " + r.getMeta().getName());
		}
		ends[1] = r;

	}

	/**
	 * Sets the parent model
	 * 
	 * @param m is the model to set
	 */
	void setModel(Model m) {
		model = m;
	}

	/**
	 * Gets the parent model that this relationship belongs to.
	 * 
	 * @return the parent Model.
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * determines whether 2 relationships are duplicates. They are duplicates if
	 * they have the same meta-relationship and their connected entities match by
	 * key.
	 */
	public boolean isDuplicate(Relationship other) {
		return (getMeta() == other.getMeta()) 
				&& start().connectionKey().equals(other.start().connectionKey())
				&& finish().connectionKey().equals(other.finish().connectionKey());
	}

	/**
	 * copies this entity to a copy
	 * 
	 * @param copy is the entity to copy to
	 */
	protected void cloneTo(Relationship copy) {
		super.cloneTo(copy);
		copy.meta = meta; // must be same type
		copy.model = null; // always disconnect copies from model
		super.cloneTo(copy);
		copy.ends[0] = (Role) ends[0].clone();
		copy.ends[1] = (Role) ends[1].clone();
		copy.ends[0].setRelationship(copy);
		copy.ends[1].setRelationship(copy);
		version.cloneTo(copy.version);
	}

	/**
	 * gets the tooltip for this object when displayed in a gui or diagram.
	 * 
	 * @return String with tooltip text.
	 */
	public String getTooltip() {
		return getMeta().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
	 */
	public Version getVersion() {
		return version;
	}

}
