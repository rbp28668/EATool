/*
 * RelationshipImportFactory.java
 *
 * Created on 23 February 2002, 17:20
 */

package alvahouse.eatool.repository.mapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.ModelChangeListener;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * IXMLContentHandler to handle Relationship objects during an import of external data.
 * This handler implements ModelChangeListener so that it can be notified of any entities
 * being added to the model and update its internal cache accordingly.
 * @author  rbp28668
 */
public class RelationshipImportFactory implements IXMLContentHandler, ModelChangeListener{

	/** Model to add relationships to */
    private Model model;
    
    /** Current relationship being defined */
    private Relationship currentRelationship = null;
    
    /** Current role being defined */
    private Role currentRole = null;
    
    /** Placeholder to allow current role to connnect to something */
    private Entity currentEntity = null; 
    
    /** Translation map for current Role*/
    private RoleTranslation currentRoleTranslation = null;
    
    /** Translation for current relationship */
    private RelationshipTranslation currentRelationshipTranslation = null;
    
    /** map of all the relationship translations keyed by the import entity name*/
    private Map<String,RelationshipTranslation> translations = new HashMap<String,RelationshipTranslation>();
    
    /** fast lookup of possible entities (by natural key) to connect to given a meta-entity */
    private Map<MetaEntity,EntityKeyLookup> entityLookup = new HashMap<MetaEntity,EntityKeyLookup>();


    /** Creates new RelationshipImportFactory 
     * @param m is the model to import to
     * @mm is the meta-model to guide the import
     * transConfig is the configuration element that defines the relationship
     * translations to be used in this import.
     */
    public RelationshipImportFactory(ImportMapping mapping, Model m) {
        model = m;

        for(RelationshipTranslation rt : mapping.getRelationshipTranslations()){
            translations.put(rt.getTypeName(), rt);
        }
        
        model.addChangeListener(this);
    }

    /**
     * Cleanup - no more notifications from model.
     */
    public void dispose(){
    	model.removeChangeListener(this);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
     */
    public void characters(String str) {
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) {
        if(local.equals("Relationship")) {
            
            // currentRelationship should be set up with both roles linked to
            // entities.  Now need to find out whether this relationship already
            // exists in the model
            boolean duplicate = false;
            for(Relationship other : model.getRelationships()){
                if(currentRelationship.isDuplicate(other)) {
                    duplicate = true;
                    break;
                }
            }
            // and if it doesn't - add it.
            if(!duplicate) {
                try {
					model.addRelationship(currentRelationship);
				} catch (Exception e) {
					throw new InputException("Unable to add relationship");
				}
            }
            
            currentRelationship = null;
        } else if (local.equals("Role")) {
            // currentEntity should contain properties to form composite key
            // to identify actual entity to link to.
            
            // want key lookup to allow easy lookup of target entity by
            // natural composite key. 
            MetaEntity me = currentRoleTranslation.getMeta().connectsTo();
            EntityKeyLookup ec = entityLookup.get(me);
            if(ec == null) {
                ec = new EntityKeyLookup(model, currentRoleTranslation.getMeta().connectsTo(), currentRoleTranslation);
                entityLookup.put(me, ec);
            }
            
            String key = currentRoleTranslation.getKeyOf(currentEntity);
            Entity e = ec.get(key); // this is the actual entity the role should connect to.
            if(e == null)
                throw new InputException("Unable to find entity with key " + key);
            currentRole.setConnection(e);
            currentRelationship.setRole(currentRole);
            
            currentRole = null;
            currentEntity = null;
            currentRoleTranslation = null;
        }
    }
    
    public void startElement(String uri, String local, Attributes attrs) {
       if(local.equals("Relationship")) {
            if(currentRelationship != null)
                throw new IllegalArgumentException("Nested Relationships importing XML");

            
            String type = attrs.getValue("type");
            currentRelationshipTranslation = (RelationshipTranslation)translations.get(type);
            if(currentRelationshipTranslation == null)
                throw new InputException("Relationship type " + type + " not recognised importing XML");
            
            currentRelationship = new Relationship(new UUID(), currentRelationshipTranslation.getMeta()); // default position is to assume it's a new relationship
            
        } else if (local.equals("Role")) {
             if(currentRole != null)
                throw new IllegalArgumentException("Nested Role loading XML");

            String type = attrs.getValue("type");
            if(type == null) throw new InputException("Missing role type while importing XML");
            
            currentRoleTranslation = currentRelationshipTranslation.getRoleByTypename(type);
            MetaRole mr = currentRoleTranslation.getMeta();
            currentRole = new Role(new UUID(),mr);
            
            currentEntity = new Entity(new UUID(), mr.connectsTo()); // of correct type for role
  
        } else if (local.equals("PropertyKey")) {
            String type = attrs.getValue("type");
            if(type == null) throw new InputException("Missing property type while importing XML");
            
            String value = attrs.getValue("value");
            if(value == null) throw new InputException("Missing property value while importing XML");
            
            PropertyTranslation pt = currentRoleTranslation.getPropertyTranslationByType(type);
            if(pt == null) throw new InputException("Unrecognised property type while importing XML (" + type + ")");

            // Save the property value in the current entity (target of the current
            // role).  When all the key properties are read in, this entity will
            // provide the composite key that identifies the real target entity
            // for this role.
            UUID uuidMeta = pt.getMeta().getKey();
            Property p = currentEntity.getPropertyByMeta(uuidMeta);
            p.setValue(value);

//            propertyIDs.add(uuidMeta); // track which properties read in.
            
        }
        
        
    }

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#modelUpdated(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void modelUpdated(ModelChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityAdded(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void EntityAdded(ModelChangeEvent e) {
		Entity entity = (Entity)e.getSource();
		EntityKeyLookup lookup = entityLookup.get(entity.getMeta());
		if(lookup != null){
			lookup.add(entity);
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void EntityChanged(ModelChangeEvent e) {
		Entity entity = (Entity)e.getSource();
		EntityKeyLookup lookup = entityLookup.get(entity.getMeta());
		if(lookup != null){
			lookup.add(entity);
		}
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void EntityDeleted(ModelChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipAdded(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void RelationshipAdded(ModelChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void RelationshipChanged(ModelChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void RelationshipDeleted(ModelChangeEvent e) {
	}
    
	
	
}
