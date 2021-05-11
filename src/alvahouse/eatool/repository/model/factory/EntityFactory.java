/*
 * EntityFactory.java
 *
 * Created on 18 January 2002, 21:21
 */

package alvahouse.eatool.repository.model.factory;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.ProgressCounter;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.PropertyContainerFactory;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;


/**
 * A factory class for de-serialising Entities stored in an XML file.  This
 * responds to SAX events routed from the parser to populate the given Model 
 * with Entities.
 * @author  rbp28668
 */
public class EntityFactory extends PropertyContainerFactory implements IXMLContentHandler {

    /** The Model any created Entities will be attached to */
    private Model model = null;
    
    /** The MetaModel any create Entities should belong to */
    private MetaModel metaModel = null;
    
    /** The Entity being read in at the current time */
    private Entity currentEntity = null;
    
    /** Whether the entity is a new one - may not be if merge */
    private boolean isNewEntity;
    
    private ProgressCounter counter;

    
    /** Creates new EntityFactory 
     * @param counter
     * @param m is the model to populate with Entites.
     * @param mm is the MetaModel used to build the Entities.  UUIDs in the 
     * <code>instanceof</code> attribute identify the corresponding MetaEntity
     * to each Entity. 
     */
    public EntityFactory(ProgressCounter counter, Model m, MetaModel mm) {
        model = m;
        metaModel = mm;
        this.counter = counter;
    }

    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String local, Attributes attrs) throws InputException {
        // Expect Entity followed by a number of Property
        if(local.equals("Entity")) {
            if(currentEntity != null) { // oops, nested entities
                throw new InputException("Nested Entry loading XML into repository");
            }
            
            UUID uuid = getUUID(attrs);

            String instance = attrs.getValue("instanceof");
            if(instance == null)
                throw new IllegalArgumentException("Missing instanceof attribute of Entity while loading XML");

            MetaEntity me = null;
            try {
            	me = metaModel.getMetaEntity(new UUID(instance));
            } catch (Exception e) {
            	throw new InputException("Unable to get meta entity from repository",e);
            }
            if(me == null)
                throw new IllegalArgumentException("Unknown class of entity while loading XML");

          	try {
           		currentEntity = new Entity(uuid,me);
           		isNewEntity = true; // TODO remove this concept
           	} catch (Exception e) {
           		throw new InputException("Unable to create new Entity", e);
           	}
            
        } else {
            startProperty(currentEntity,uri,local,attrs);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Entity")) {
            if(isNewEntity){
                try {
					model.addEntity(currentEntity);
				} catch (Exception e) {
					throw new InputException("Unable to add entity");
				}
            }
            currentEntity = null;
            counter.count("Entity");
        } else {
            endProperty(currentEntity,uri,local);
        }        
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) {
        setPropertyValue(str);
     }
    
}
