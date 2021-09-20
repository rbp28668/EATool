/*
 * CountHandler.java
 * Project: EATool
 * Created on 05-Mar-2007
 *
 */
package alvahouse.eatool.repository;

import java.io.IOException;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.XMLWriter;

/**
 * CountHandler writes out, and reads in subsequently, counts of various
 * components in the repository to allow reasonable estimates of progress during
 * loading.
 * 
 * @author rbp28668
 */
public class CountHandler implements IXMLContentHandler {

	private Repository repository;
	private ProgressStatus status;

	private transient int value = 0;
	private int metaEntityCount;
	private int metaRelationshipCount;
	private int entityCount;
	private int relationshipCount;
	private int scriptCount;
	private int eventCount;
	private int propertyCount;
	private int extensibleTypeCount;
	//private boolean hasCounts = false;

	/**
	 * 
	 */
	public CountHandler(Repository repository) {
		super();
		this.repository = repository;
	}

	public CountHandler(ProgressStatus status) {
		super();
		this.status = status;
	}

	/**
	 * Writes the EventMap out as XML
	 * 
	 * @param out is the XMLWriterDirect to write the XML to
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("RepositorySizes");

		try {
			out.textEntity("MetaEntityCount", Integer.toString(repository.getMetaModel().getMetaEntityCount()));
			out.textEntity("MetaRelationshipCount",
					Integer.toString(repository.getMetaModel().getMetaRelationshipCount()));
			out.textEntity("EntityCount", Integer.toString(repository.getModel().getEntityCount()));
			out.textEntity("RelationshipCount", Integer.toString(repository.getModel().getRelationshipCount()));
			out.textEntity("ScriptCount", Integer.toString(repository.getScripts().getScriptCount()));
			out.textEntity("EventCount", Integer.toString(repository.getEventMap().getEventCount()));
			out.textEntity("PropertyCount", Integer.toString(repository.getProperties().getPropertyCount()));
			out.textEntity("ExtensibleTypeCount", Integer.toString(repository.getExtensibleTypes().getTypeCount()));
		} catch (Exception e) {
			throw new IOException("Unable to get counts from repository", e);
		}

		out.stopEntity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String,
	 * java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs) throws InputException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String,
	 * java.lang.String)
	 */
	public void endElement(String uri, String local) throws InputException {
		if (local.equals("MetaEntityCount")) {
			metaEntityCount = value;
		} else if (local.equals("MetaRelationshipCount")) {
			metaRelationshipCount = value;
		} else if (local.equals("EntityCount")) {
			entityCount = value;
		} else if (local.equals("RelationshipCount")) {
			relationshipCount = value;
		} else if (local.equals("ScriptCount")) {
			scriptCount = value;
		} else if (local.equals("EventCount")) {
			eventCount = value;
		} else if (local.equals("PropertyCount")) {
			propertyCount = value;
		} else if (local.equals("ExtensibleTypeCount")) {
			extensibleTypeCount = value;
		} else if (local.equals("RepositorySizes")) {
			int total = metaEntityCount + metaRelationshipCount + entityCount + relationshipCount + scriptCount
					+ eventCount + propertyCount + extensibleTypeCount;

			total += entityCount; // allow for search indexing.

			status.setIndeterminate(false);
			status.setRange(0, total);
			status.setPosition(0);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
	 */
	public void characters(String str) throws InputException {
		value = Integer.parseInt(str);
	}

}
