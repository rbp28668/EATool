/*
 * Entity.java
 *
 * Created on 11 January 2002, 01:36
 */

package alvahouse.eatool.repository.model;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;

import alvahouse.eatool.repository.dto.model.EntityDto;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityDisplayHint;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;
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
public class Entity extends PropertyContainer implements Versionable {

    private MetaEntity meta;
    private Model model;                                // that this belongs to
    private VersionImpl version = new VersionImpl();
    
    /** Creates new Entity of a given type.
     * @param me is the  MetaEntity that describes the type of the entity.
    */
    public Entity(MetaEntity me) throws Exception{
        super(new UUID());
        addDefaultProperties(me);
        meta = me;
    }
    
    /** Creates new Entity where the uuid is already known - such as when
     * de-serialising a model. 
     * @param uuid is the UUID that should be used to uniquely identify
     * this Entity.
     * @param me is the MetaEntity that describes the type of the entity.
     * */
    public Entity(UUID uuid, MetaEntity me) throws Exception{
        super(uuid);
        addDefaultProperties(me);
        meta = me;
    }

	/**
	 * Copy constructor for clone.
	 * @param source is the Entity to clone from.
	 */
	protected Entity(Entity source){
		super(source.getKey());
		source.cloneTo(this);
	}
	
	public Entity(EntityDto dao, MetaEntity me) throws Exception {
		super(dao, me);
		this.meta = me;
		this.version = new VersionImpl(dao.getVersion());
	}
	
	public EntityDto toDao() {
		EntityDto dao = new EntityDto();
		copyTo(dao);
		return dao;
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        MetaEntity me = getMeta();
        MetaEntityDisplayHint dh = null;
        try {
        	dh = me.getDisplayHint();
        } catch (Exception e) {
        	// NOP
        }
        
        if(dh != null)
            return getName(dh);
        else
            return getKey().toString();
    }

    /** Uses the keys to work out what the (display) name for an
     * entity should be
     * @param e is the entity to get the name for
     * @returns the name for the entity
     */
    private String getName(MetaEntityDisplayHint dh) {
        
        StringBuffer buff = new StringBuffer();
        for(UUID key : dh.getKeys()){
            Property p = getPropertyByMeta(key);
            if(p != null) {
                if(buff.length() > 0)
                    buff.append(":");
                buff.append(p.getValue());
            }
        }
        
        if(buff.length() == 0)
            return getKey().toString();
        else
            return buff.toString();
    }

    /** Creates a copy of the entity
     * @return a new entity (with the same key)
     */
    public Object clone() {
        Entity copy = new Entity(this);
        return copy;
    }
    
    
    /** gets the meta entity that this entity is an instance of.
     * @return the associated meta entity
     */
    public MetaEntity getMeta() {
        return meta;
    }
    
     /**
     * Writes the Entity out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Entity");
        out.addAttribute("uuid",getKey().toString());
        out.addAttribute("instanceof",meta.getKey().toString());
        version.writeXML(out);
        super.writeXML(out);
        
        out.stopEntity();
    }
  
     /** Sets the parent model
     * @param m is the model to set
     */
    void setModel(Model m) {
        model = m;
    }
    
    /** Gets the parent model.
     * @return the parent model.
     */
    public Model getModel() {
        return model;
    }

    
    /** This gets the set of relationships that are connected to this entity in the model
     * that the entity belongs to.  Note - this is in no-way optimised.
     * @return the set of connected relationships.
     */
    public Set<Relationship> getConnectedRelationships() throws Exception {
        if(model == null){
            throw new IllegalStateException("Cannot get connected relationships for Entity not connected to a model");
        }
        return model.getConnectedRelationships(this);
    }

    /** This gets the set of relationships that are connected to this entity in the model
     * that the entity belongs to.  Note - this is in no-way optimised.
     * @return the set of connected relationships.
     */
    public Set<Relationship> getConnectedRelationshipsOf(MetaRelationship meta) throws Exception{
        if(model == null){
            throw new IllegalStateException("Cannot get connected relationships for Entity not connected to a model");
        }
        return model.getConnectedRelationshipsOf(this, meta);
    }
    
     /** copies this entity to a copy.
     * @param copy is the entity to copy to.
     */
    protected void cloneTo(Entity copy) {
        copy.meta = meta;   // must be same type
        copy.model = null;  // always disconnect copies from model
        version.cloneTo(copy.version);
        super.cloneTo(copy);
    }

    /** copies this entity to a DAO.
    * @param copy is the entity dao to copy to.
    */
   protected void copyTo(EntityDto dao) {
	   super.copyTo(dao);
       dao.setMetaEntityKey(meta.getKey());
       dao.setVersion(version.toDto());
   }
    

    /**
     * CompareByName is a Comparator class to allow sorting etc of Entities. 
     * 
     * @author rbp28668
     */
    public static class Compare implements Comparator<Entity> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Entity arg0, Entity arg1) {
            return arg0.toString().compareToIgnoreCase(arg1.toString());
        }
        
    }

   /* (non-Javadoc)
 * @see alvahouse.eatool.repository.version.Versionable#getVersion()
 */
public Version getVersion() {
        return version;
    }
    

}
