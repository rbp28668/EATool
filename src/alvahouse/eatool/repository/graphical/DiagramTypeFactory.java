package alvahouse.eatool.repository.graphical;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramTypeFamily;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.EventMapFactory;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * Handler to deserialise diagram types (and their sub objects such as
 * symbol types and connector types).  StandardDiagram types are added to the
 * StandardDiagram Types object as needed.
 * @author bruce.porteous
 *
  */
public class DiagramTypeFactory extends FactoryBase implements IXMLContentHandler {

	private MetaModel metaModel;
	private DiagramTypes types;
	private EventMapFactory eventMapFactory;
	private EventMap savedEventMap = null;
	private DiagramType currentDiagramType = null;
	private DiagramTypeFamily currentFamily = null;
	private DiagramTypeDetailFactory currentHandler = null;
    private ProgressCounter counter;

	
	/**
	 * Constructor for DiagramTypeFactory.
	 * @param counter
	 */
	public DiagramTypeFactory(ProgressCounter counter, DiagramTypes diagramTypes, MetaModel mm, EventMapFactory eventMapFactory) {
		super();
		types = diagramTypes;
        metaModel = mm; 
        this.eventMapFactory = eventMapFactory;
        this.counter = counter;
	}

	/**
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(String, String, Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs)
		throws InputException {

		if(local.equals("DiagramType")) {
			if(currentDiagramType != null) {
				throw new InputException("Nested diagram type found while loading XML");
			}
			
			try {
                // Figure out what type of DiagramType this is.  This is done
			    // using the family key which should reference type family.  If
			    // this attribute does not exist then it's probably an old file
			    // so StandardDiagramType should be used.
                UUID familyKey = null;
                String familyKeyText = attrs.getValue("family");
                if(familyKeyText != null){
                    familyKey = new UUID(familyKeyText);
                } else {
                    familyKey = StandardDiagramTypeFamily.FAMILY_KEY;
                }
                
                currentFamily = types.lookupFamily(familyKey);
                currentDiagramType = currentFamily.newDiagramType();
            } catch (InstantiationException e) {
                throw new InputException("Unable to create diagram type: " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new InputException("Unable to access diagram type: " + e.getMessage());
            }
			
            String attr = attrs.getValue("name");
            if(attr != null) currentDiagramType.setName(attr);
 			
			UUID uuid = getUUID(attrs);
			currentDiagramType.setKey(uuid);
			
			savedEventMap = eventMapFactory.getEventMap();
			eventMapFactory.setEventMap(currentDiagramType.getEventMap());
			
			currentHandler = currentDiagramType.getTypeDetailFactory();
			currentHandler.init(currentDiagramType,metaModel);
		} else{
		    currentHandler.startElement(uri,local,attrs);
		}
	}

	/**
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(String, String)
	 */
	public void endElement(String uri, String local) throws InputException {
		if(local.equals("DiagramType")) {
			currentFamily.add(currentDiagramType);
			currentDiagramType = null;
			eventMapFactory.setEventMap(savedEventMap);
			savedEventMap = null;
			currentHandler = null;
			counter.count("Diagram Type");
		} else {
		    currentHandler.endElement(uri, local);
		}
	}

	/**
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(String)
	 */
	public void characters(String str) throws InputException {
	    if(currentHandler != null){
	        currentHandler.characters(str);
	    }
	}


}
