/*
 * DiagramFactory.java
 * Created on 01-May-2004
 * By Bruce.Porteous
 *
 */
package alvahouse.eatool.repository.graphical;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import alvahouse.eatool.gui.graphical.standard.metamodel.MetaModelDiagramType;
import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.EventMapFactory;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * DiagramFactory is a handler to deserialise diagrams (and their sub objects such as
 * symbols and connectors).  Diagrams are added to the supplied diagrams as needed.
 * @author Bruce.Porteous
 *
 */
public class DiagramFactory extends FactoryBase implements IXMLContentHandler {

	private final Repository repository;
    private Diagrams diagrams;
    private DiagramTypes diagramTypes;
    private MetaModel metaModel;
    private Model model;
    private Images images;
    private EventMapFactory eventMapFactory;
    private EventMap savedEventMap;
    private Map<DiagramType, DiagramDetailFactory> detailFactories = new HashMap<>(); // DiagramDetailFactory keyed by DiagramType
    
    private Diagram currentDiagram = null;
    private DiagramDetailFactory currentHandler = null;
    private ProgressCounter counter;

    
	/**
	 * Create a DiagramFactory for a given diagrams collection and meta model.
	 * @param counter
	 * @param diagrams will have any de-serialized diagrams added to it.
	 * @param diagramTypes provides the types of any diagram.
	 * @param metaModel is the meta-model that the digrams belong to.
	 */
	public DiagramFactory(ProgressCounter counter, Diagrams diagrams, DiagramTypes diagramTypes, Repository repository, EventMapFactory eventMapFactory) {
		super();
		this.repository = repository;
		this.diagrams = diagrams;
		this.diagramTypes = diagramTypes;
		this.eventMapFactory = eventMapFactory;
		this.counter = counter;

		this.metaModel = repository.getMetaModel();
		this.model = repository.getModel();
		this.images = repository.getImages();

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String local, Attributes attrs)
		throws InputException {
		if(local.equals("Diagram")) {
			if(currentDiagram != null) {
				throw new InputException("Nested diagram found while loading XML");
			}

			UUID uuid = getUUID(attrs);
			
			String attr = attrs.getValue("ofType");
			if(attr == null){
			    throw new InputException("Missing ofType attribute in Diagram");
			}
			UUID typeID = new UUID(attr);
			
			DiagramType type = null;
			try {
				type = diagramTypes.get(typeID);
				if(type == null){
				    type = MetaModelDiagramType.getInstance(repository);
				}
			} catch (Exception e) {
				throw new InputException("Unable to get diagram types whilst loading XML",e);
			}
			
			try {
				currentDiagram = type.newDiagram(uuid);
				setupHandler(type);
			} catch (Exception e) {
				throw new InputException("Unable to create diagram whilst loading XML",e);
			}
			
            attr = attrs.getValue("name");
            if(attr != null){
                currentDiagram.setName(attr);
            }
			savedEventMap = eventMapFactory.getEventMap();
			eventMapFactory.setEventMap(currentDiagram.getEventMap());
 			
		} else if(local.equals("Version")){
			VersionImpl.readXML(attrs, currentDiagram);
		} else {
		    if(currentHandler != null){
		        currentHandler.startElement(uri,local,attrs);
		    }
		} 
	}


    /**
     * @param type
     */
    private void setupHandler(DiagramType type) {
        currentHandler = (DiagramDetailFactory)detailFactories.get(type);
        if(currentHandler == null){
            currentHandler = type.getDetailFactory();
            detailFactories.put(type,currentHandler);
        }
        currentHandler.init(currentDiagram, type, metaModel, model, images ); 
    }

    /* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String local) throws InputException {
		if(local.equals("Diagram")) {
			try {
				diagrams._add(currentDiagram);
				currentDiagram.deferLayout(false);
				currentDiagram = null;
				eventMapFactory.setEventMap(savedEventMap);
				savedEventMap = null;
				currentHandler = null;
				counter.count("Diagram");
			} catch (Exception e) {
				throw new InputException("Unable to add diagram",e);
			}
		} else {
		    if(currentHandler != null) {
		        currentHandler.endElement(uri,local);
		    }
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
	 */
	public void characters(String str) throws InputException {
	    if(currentHandler != null){
	        currentHandler.characters(str);
	    }
	}

}
