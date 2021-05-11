/*
 * MetaRelationshipFactory.java
 *
 * Created on 18 January 2002, 21:16
 */

package alvahouse.eatool.repository.metamodel.factory;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyTypes;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 ** MetaRelationshipFactory class is a content handler for deserialising
 * meta-relationships (and meta-roles) from XML.
 * 
 * @author rbp28668
 */
public class MetaRelationshipFactory extends MetaPropertyContainerFactory implements IXMLContentHandler {

	/** The MetaModel any new MetaRelationships are added to */
	private MetaModel m_metaModel;

	/** The MetaRelationship currently being read in */
	private MetaRelationship m_currentMetaRelationship = null;

	/** The MetaRole currently being read in */
	private MetaRole m_currentMetaRole = null;

	/** Whether the meta relationship is a new one or not. */
	private boolean isNewMetaRelationship;

	private ProgressCounter counter;

	/**
	 * Creates new MetaRelationshipFactory
	 * 
	 * @param counter
	 * @param mm      is the MetaModel to add any new MetaRelationships to.
	 */
	public MetaRelationshipFactory(ProgressCounter counter, MetaPropertyTypes types, MetaModel mm) {
		super(types);
		m_metaModel = mm;
		this.counter = counter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String,
	 * java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs) throws InputException {
		if (local.equals("MetaRelationship")) {
			if (m_currentMetaRelationship != null)
				throw new IllegalArgumentException("Nested MetaRelationships loading XML");

			UUID uuid = getUUID(attrs);
			m_currentMetaRelationship = new MetaRelationship(uuid);
			isNewMetaRelationship = true; // TODO remove eventually
//			try {
//				m_currentMetaRelationship = m_metaModel.getMetaRelationship(uuid);
//			} catch (Exception e) {
//				throw new InputException("Unable to read meta relationship from repository with key " + uuid);
//			}
//			isNewMetaRelationship = m_currentMetaRelationship == null;
//			if (isNewMetaRelationship) {
//				m_currentMetaRelationship = new MetaRelationship(uuid);
//			}

			getCommonFields(m_currentMetaRelationship, attrs);

		} else if (local.equals("MetaRole")) {
			if (m_currentMetaRole != null)
				throw new IllegalArgumentException("Nested MetaRole loading XML");

			UUID uuid = getUUID(attrs);
			m_currentMetaRole = m_currentMetaRelationship.getMetaRole(uuid);
			getCommonFields(m_currentMetaRole, attrs);

			String attr = attrs.getValue("multiplicity");
			if (attr != null) {
				m_currentMetaRole.setMultiplicity(Multiplicity.fromString(attr));
			}

			attr = attrs.getValue("connects"); // MetaEntity this role connects to
			if (attr == null)
				throw new IllegalArgumentException("Missing connection uuid for MetaRole loading XML");
			m_currentMetaRole.setConnectionKey(new UUID(attr));
		} else {
			MetaPropertyContainer container = m_currentMetaRelationship;
			if (m_currentMetaRole != null) {
				container = m_currentMetaRole;
			}
			try {
				startMetaProperty(container, uri, local, attrs);
			} catch (Exception e) {
				throw new InputException("Problem loading XML", e);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
	 */
	public void characters(String str) {
		if (!setPropertyDescription(str)) {
			if (m_currentMetaRole != null) {
				m_currentMetaRole.setDescription(str);
			} else {
				m_currentMetaRelationship.setDescription(str);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String,
	 * java.lang.String)
	 */
	public void endElement(String uri, String local) {
		if (local.equals("MetaRelationship")) {
			if (isNewMetaRelationship) {
				try {
					m_metaModel.addMetaRelationship(m_currentMetaRelationship);
				} catch (Exception e) {
					throw new InputException("Unable to add meta relationship", e);
				}

			}
			m_currentMetaRelationship = null;
			counter.count("Meta Relationship");
		} else if (local.equals("MetaRole")) {
			m_currentMetaRole = null;
		} else {
			MetaPropertyContainer container = m_currentMetaRelationship;
			if (m_currentMetaRole != null) {
				container = m_currentMetaRole;
			}
			endMetaProperty(container, uri, local);
		}
	}

}
