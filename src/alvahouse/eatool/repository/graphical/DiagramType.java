/*
 * DiagramType.java
 * Project: EATool
 * Created on 20-Aug-2006
 *
 */
package alvahouse.eatool.repository.graphical;

import java.io.IOException;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * DiagramType is the common base class for all DiagramTypes. It handles the
 * common operations of handling the event map and naming.
 * 
 * @author rbp28668
 */
public abstract class DiagramType extends NamedRepositoryItem implements Versionable{

	private EventMap eventMap;
	private DiagramTypeFamily family = null;
	private VersionImpl version = new VersionImpl();

	/**
	 * @param key
	 */
	public DiagramType(UUID key) {
		super(key);
		init();
	}

	public DiagramType(DiagramTypeFamily family, DiagramTypeDto dto) {
		super(dto);
		this.family = family;
		eventMap = new EventMap(dto.getEventMap());
		version.fromDto(dto.getVersion());
	}
	
	/**
	 * Constructs an existing diagram type.
	 * 
	 * @param name is the name of the diagram type.
	 * @param uuid is the unique ID of the diagram type.
	 */
	public DiagramType(String name, UUID uuid) {
		super(uuid);
		setName(name);
		init();
	}

	private void init() {
		eventMap = new EventMap();
		eventMap.ensureEvent(StandardDiagram.ON_DISPLAY_EVENT);
		eventMap.ensureEvent(StandardDiagram.ON_CLOSE_EVENT);
		eventMap.ensureEvent("Entity");
		eventMap.ensureEvent("Relationship");
	}

//	/**
//	 * @param copy
//	 */
//	protected void cloneTo(DiagramType copy){
//	    super.cloneTo(copy);
//		eventMap.cloneTo(copy.eventMap);
//	}

	/**
	 * Gets the event map.
	 * 
	 * @return the event map.
	 */
	public EventMap getEventMap() {
		return eventMap;
	}

	/**
	 * Creates a new diagram of this type.
	 * 
	 * @return a new diagram.
	 */
	public abstract Diagram newDiagram(UUID key);

	public String toString() {
		return getName();
	}

	/**
	 * Writes the StandardDiagram type out as XML
	 * 
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void startXML(XMLWriter out) throws IOException {
		out.startEntity("DiagramType");
		super.writeAttributesXML(out);
		out.addAttribute("family", getFamilyKey().toString());
		version.writeXML(out);
		eventMap.writeXMLUnversioned(out);
	}

	public abstract void writeXML(XMLWriter out) throws IOException;

	/**
	 * Get a DiagramDetailFactory that will de-serialise detail for Diagrams of this
	 * type.
	 * 
	 * @return an appropriate DiagramDetailFactory.
	 */
	public abstract DiagramDetailFactory getDetailFactory();

	/**
	 * DiagramTypeDetailFactory get the DiagramTypeDetailFactory that will
	 * de-serialise one of these.
	 * 
	 * @return an appropriate DiagramTypeDetailFactory.
	 */
	public abstract DiagramTypeDetailFactory getTypeDetailFactory();

	public abstract void removeSymbolsFor(MetaEntity meta);

	public abstract void removeConnectorsFor(MetaRelationship meta);

	public abstract void validate(MetaRelationship meta) throws Exception;

	/**
	 * Helper class to identify (by key) the family this type belongs to.
	 * 
	 * @return the parent family's key.
	 */
	public UUID getFamilyKey() {
		return family.getKey();
	}

	/**
	 * @return Returns the family.
	 */
	public DiagramTypeFamily getFamily() {
		return family;
	}

	/**
	 * @param family The family to set.
	 */
	public void setFamily(DiagramTypeFamily family) {
		this.family = family;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
	 */
	@Override
	public Version getVersion() {
		return version;
	}
	
	// Make clone visible but child objects must over-ride
	@Override
	public abstract Object clone() ;
	
	protected void cloneTo(DiagramType copy) {
		super.cloneTo(copy);
		copy.eventMap = (EventMap)eventMap.clone();
		copy.family = family; // No clone as these are fixed and can be shared.
		version.cloneTo(copy.version);
	}
	
	protected void copyTo(DiagramTypeDto dto) {
		super.copyTo(dto);
		dto.setEventMap(eventMap.toDto());
		dto.setVersion(version.toDto());
	}
	

}
