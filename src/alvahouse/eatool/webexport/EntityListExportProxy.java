/*
 * EntityTableExportProxy.java
 * Project: EATool
 * Created on 10-May-2006
 *
 */
package alvahouse.eatool.webexport;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.XMLWriter;

/**
 * EntityListExportProxy exports a list of entities belonging to a given meta-entity.
 * 
 * @author rbp28668
 */
public class EntityListExportProxy implements ExportProxy {

    private MetaEntity me; 
    private Model model;
    
    /**
     * 
     */
    public EntityListExportProxy(Model model) {
        super();
        assert(model != null);
        this.model = model;
    }
    
    /**
     * @param me
     */
    public void setMetaEntity(MetaEntity me){
    	assert(me != null);
        this.me = me;
    }

    /* (non-Javadoc)
     * @see webexport.ExportProxy#export(alvahouse.eatool.util.XMLWriter)
     */
    public void export(XMLWriter out) throws IOException {

        if(me == null) {
            throw new IllegalStateException("MetaEntity not set for export");
        }
        out.startEntity("EntityList");
        
        out.textEntity("Name",me.getName());
        out.textEntity("UUID",me.getKey().toString());
        out.textEntity("Description",me.getDescription());

        out.startEntity("MetaProperties");
        for(MetaProperty mp : me.getMetaProperties()){
            out.startEntity("MetaProperty");
            out.addAttribute("summary",mp.isSummary()?"true":"false");
            out.addAttribute("mandatory",mp.isMandatory()?"true":"false");
            out.textEntity("Name",mp.getName());
            out.textEntity("UUID",mp.getKey().toString());
            out.textEntity("Description",mp.getDescription());
            out.textEntity("Type",mp.getMetaPropertyType().getTypeName());
            out.stopEntity();
        }
        out.stopEntity(); // MetaProperties

        List<Entity> entities =  model.getEntitiesOfType(me);
        Collections.sort(entities,new Entity.Compare());
        
        for(Entity e : entities){
            out.startEntity("Entity");
            out.textEntity("Name", e.toString());
            out.textEntity("UUID", e.getKey().toString());

            out.startEntity("Properties");
            for(MetaProperty mp : me.getMetaProperties()){
                Property p = e.getPropertyByMeta(mp.getKey());
                out.startEntity("Property");
                out.addAttribute("summary",mp.isSummary()?"true":"false");
                out.textEntity("Value",p.getValue());
                out.textEntity("Name",mp.getName());
                out.textEntity("Type",mp.getMetaPropertyType().getTypeName());
                out.stopEntity(); // Property
            }
            out.stopEntity(); // Properties

            out.stopEntity(); // Entity
        }
        out.stopEntity(); // EntityList

    }

}
