/*
 * RelationshipFactory.java
 *
 * Created on 18 January 2002, 21:24
 */

package alvahouse.eatool.repository.model.factory;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.PropertyContainer;
import alvahouse.eatool.repository.model.PropertyContainerFactory;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * A factory class for de-serialising Relationships stored in an XML file. This
 * responds to SAX events routed from the parser to populate the given Model
 * with Relationships.
 * 
 * @author rbp28668
 */
public class RelationshipFactory extends PropertyContainerFactory implements IXMLContentHandler {

	/** The model any new relationships will be added to */
	private Model model;

	/** The MetaModel any relationships should correspond to */
	private MetaModel metaModel;

	/** The Relationship being read in at the current time */
	private Relationship currentRelationship = null;

	/** The Role being read in at the current time */
	private Role currentRole = null;

	/** Whether this Relationship is new or not */
	private boolean isNewRelationship;

	private ProgressCounter counter;

	/**
	 * Creates new RelationshipFactory.
	 * 
	 * @param counter
	 * @param m       is the Model to add Relationships to.
	 * @param mm      is the MetaModel the Relationships must belong to.
	 */
	public RelationshipFactory(ProgressCounter counter, Model m, MetaModel mm) {
		model = m;
		metaModel = mm;
		this.counter = counter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String,
	 * java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs) throws InputException {
		if (local.equals("Relationship")) {
			if (currentRelationship != null)
				throw new IllegalArgumentException("Nested Relationships loading XML");

			UUID uuid = getUUID(attrs);

			String instance = attrs.getValue("instanceof");
			if (instance == null)
				throw new IllegalArgumentException("Missing instanceof attribute of Relationship while loading XML");

			MetaRelationship mr = null;
			try {
				mr = metaModel.getMetaRelationship(new UUID(instance));
			} catch (Exception e) {
				throw new InputException("Unable to fetch meta relationship from repository", e);
			}

			if (mr == null)
				throw new IllegalArgumentException("Invalid meta-relationship key (instanceof) while reading XML");

			try {
				currentRelationship = new Relationship(uuid, mr);
				isNewRelationship = true; // TODO remove eventually
			} catch (Exception e) {
				throw new InputException("Unable to load relationship",e);
			}
//			currentRelationship = model.getRelationship(uuid);
//			isNewRelationship = currentRelationship == null;
//			if (isNewRelationship) {
//				try {
//					currentRelationship = new Relationship(uuid, mr);
//				} catch (Exception e) {
//					
//				}
//			}

		} else if (local.equals("Role")) {
			if (currentRole != null)
				throw new IllegalArgumentException("Nested Role loading XML");

			UUID uuid = getUUID(attrs);

			String instance = attrs.getValue("instanceof");
			if (instance == null)
				throw new IllegalArgumentException("Missing instanceof attribute of Role while loading XML");

			MetaRole mr = currentRelationship.getMeta().getMetaRole(new UUID(instance));
			currentRole = currentRelationship.getRole(uuid);
			if (currentRole == null) {
				try {
					currentRole = new Role(uuid, mr);
				} catch (Exception e) {
					throw new InputException("Unable to create Role", e);
				}
			}

			String connect = attrs.getValue("connects"); // Entity this role connects to
			if (connect == null)
				throw new IllegalArgumentException("Missing connection uuid for Role loading XML");
			currentRole.setConnectionKey(new UUID(connect));
			
        } else if(local.equals("Version")){
        	VersionImpl.readXML(attrs, currentRelationship);
		} else {
			PropertyContainer container = currentRelationship;
			if (currentRole != null) {
				container = currentRole;
			}
			startProperty(container, uri, local, attrs);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String,
	 * java.lang.String)
	 */
	public void endElement(String uri, String local) throws InputException {
		if (local.equals("Relationship")) {
			if (isNewRelationship) {
				try {
					model._add(currentRelationship);
				} catch (Exception e) {
					throw new InputException("Unable to add relationship", e);
				}
			}
			currentRelationship = null;
			counter.count("Relationship");
		} else if (local.equals("Role")) {
			currentRelationship.setRole(currentRole);
			currentRole = null;
		} else {
			PropertyContainer container = currentRelationship;
			if (currentRole != null) {
				container = currentRole;
			}
			endProperty(container, uri, local);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
	 */
	public void characters(String str) {
		setPropertyValue(str);
	}

}
