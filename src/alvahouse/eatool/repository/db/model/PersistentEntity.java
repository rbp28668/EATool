/*
 * Entity.java
 *
 * Created on 11 January 2002, 01:36
 */

package alvahouse.eatool.repository.db.model;

import alvahouse.eatool.repository.db.metamodel.PersistentMetaEntity;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
/**
 * This models a "thing" in the model.  Entities are of a given type described
 * by an attached @link alvahouse.eatool.repository.metamodel.MetaEntity.
 * @author  rbp28668
 */
/**
 * Entity
 * 
 * @author rbp28668
 */
public class PersistentEntity extends PersistentPropertyContainer implements Versionable {

    private PersistentMetaEntity meta;
    private PersistentModel model;                                // that this belongs to
    private VersionImpl version = new VersionImpl();
    private PersistentScenario scenario;
    
    /** Creates new Entity of a given type.
     * @param me is the  MetaEntity that describes the type of the entity.
    */
    public PersistentEntity(PersistentMetaEntity me) {
        super(new UUID());
        meta = me;
    }
    
    /** Creates new Entity where the uuid is already known - such as when
     * de-serialising a model. 
     * @param uuid is the UUID that should be used to uniquely identify
     * this Entity.
     * @param me is the MetaEntity that describes the type of the entity.
     * */
    public PersistentEntity(UUID uuid, PersistentMetaEntity me) {
        super(uuid);
        meta = me;
    }

     
    /** gets the meta entity that this entity is an instance of.
     * @return the associated meta entity
     */
    public PersistentMetaEntity getMeta() {
        return meta;
    }
    
 	/**
	 * @param meta the meta to set
	 */
	public void setMeta(PersistentMetaEntity meta) {
		this.meta = meta;
	}


     
     /**
	 * @return the scenario
	 */
	public PersistentScenario getScenario() {
		return scenario;
	}

	/**
	 * @param scenario the scenario to set
	 */
	public void setScenario(PersistentScenario scenario) {
		this.scenario = scenario;
	}

	/** Sets the parent model
     * @param m is the model to set
     */
    void setModel(PersistentModel m) {
        model = m;
    }
    
    /** Gets the parent model.
     * @return the parent model.
     */
    public PersistentModel getModel() {
        return model;
    }

 
   /* (non-Javadoc)
 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
 */
public Version getVersion() {
        return version;
    }
    

}
