/*
 * EntityImportFactory.java
 *
 * Created on 21 February 2002, 21:14
 */

package alvahouse.eatool.repository.mapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;


/**
 * Class to process XML import files.
 * @author  rbp28668
 */
public class EntityImportFactory implements IXMLContentHandler  {

    private Model model = null;

    // current state of input... 
    private EntityTranslation currentEntityTranslation = null;
    private Entity currentEntity = null;
    private Property currentProperty = null;

    /** track which properties have been read in */
    private List<UUID> propertyIDs = new LinkedList<UUID>();
    
    /** map of all the entity translations keyed by the import entity name*/
    private Map<String,EntityTranslation> translations = new HashMap<String,EntityTranslation>();
    
    /** cache for fast entity lookup given a natural key.  Cache is indexed by
     * type, then key */
    private Map<EntityTranslation,EntityKeyLookup> entityLookup = new HashMap<EntityTranslation,EntityKeyLookup>();
    
    /** Creates new EntityImportFactory. 
     * @param mapping is the ImportMapping to use.
     * @param m is the model to import into.
     */
    public EntityImportFactory(ImportMapping mapping, Model m) {
        model = m;

        for(EntityTranslation et : mapping.getEntityTranslations()){
            translations.put(et.getTypeName(), et);
        }
    }
    
    /**
     * Cleanup.
     */
    public void dispose()  {
    	
    }
    
    public void startElement(String uri, String local, Attributes attrs) {
        // Expect Entity followed by a number of Property
        if(local.equals("Entity")) {
            
            if(currentEntityTranslation != null) { // oops, nested entities
                throw new InputException("Nested Entry importing XML into repository");
            }
            
            String type = attrs.getValue("type");
            if(type == null) {
                throw new InputException("Missing type attribute on " + local);
            }
            currentEntityTranslation = (EntityTranslation)translations.get(type);
            if(currentEntityTranslation == null)
                throw new InputException("Entity type " + type + " not recognised importing XML");
            
            currentEntity = new Entity(new UUID(), currentEntityTranslation.getMeta()); // default position is to assume it's a new entity
            
        } else if (local.equals("Property")) {
            
            if(currentEntityTranslation == null) 
                throw new InputException("Property outside Entity while importing XML");
                        
            String type = attrs.getValue("type");
            if(type == null) throw new InputException("Missing property type while importing XML");
            
            String value = attrs.getValue("value");
            if(value == null) throw new InputException("Missing property value while importing XML");
            
            PropertyTranslation pt = currentEntityTranslation.getPropertyTranslationByType(type);
            if(pt == null) {
                //System.out.println("Property type " + type + " is not mapped");
                //throw new InputException("Unrecognised property type while importing XML");
            } else {

	            //System.out.println("Property " + type + " = " + value);
	            
	            UUID uuidMeta = pt.getMeta().getKey();
	            propertyIDs.add(uuidMeta); // track which properties read in.
	            
	            currentProperty = currentEntity.getPropertyByMeta(uuidMeta);
	            currentProperty.setValue(value);
            }
        }
    }

    public void endElement(String uri, String local) {
        if(local.equals("Entity")) {
            
            // the imported entity is in currentEntity.  Using the fields marked as keys
            // we need to try to find it in the model.  If it's in the model we need to
            // update the fields that are present, if it's not in the model, we need to 
            // verify that all the mandatory properties are set, add default values for 
            // any missing properties, and add it.
            
            EntityKeyLookup ec = (EntityKeyLookup)entityLookup.get(currentEntityTranslation);
            if(ec == null) {
                ec = new EntityKeyLookup(model, currentEntityTranslation.getMeta(), currentEntityTranslation);
                entityLookup.put(currentEntityTranslation, ec);
            }
            
            String key = currentEntityTranslation.getKeyOf(currentEntity);
            Entity e = ec.get(key);
            if(e != null) {
                // Only want to update the values just read in.
                for(UUID uuidMeta : propertyIDs){
                    e.getPropertyByMeta(uuidMeta).setValue( currentEntity.getPropertyByMeta(uuidMeta).getValue() );
                }
            } else {
                try {
					model.addEntity(currentEntity);
					ec.add(currentEntity);  // in case of duplicates in input
				} catch (Exception e1) {
					throw new InputException("Unable to add entity",e1);
				}
            }
            
            //model.addEntity(currentEntity);
            
            currentEntity = null;
            currentEntityTranslation = null;
            propertyIDs.clear();
        } else if (local.equals("Property")) {
            currentProperty = null;
        }        
    }
    
    public void characters(String str) {
        if(currentProperty != null) {
            currentProperty.setValue(str);
        }
    }

    
 }
