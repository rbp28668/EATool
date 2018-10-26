/*
 * EntityExportProxy.java
 * Project: EATool
 * Created on 10-May-2006
 *
 */
package alvahouse.eatool.webexport;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.util.XMLWriter;



/**
 * EntityExportProxy
 * 
 * @author rbp28668
 */
public class EntityExportProxy implements ExportProxy {

    private Entity e;
    
    public EntityExportProxy(){
        super();
    }
    
    public void setEntity(Entity e){
        this.e = e;
    }
    
    /* (non-Javadoc)
     * @see webexport.ExportProxy#export(alvahouse.eatool.util.XMLWriter)
     */
    public void export(XMLWriter out) throws IOException {
        if(e == null) {
            throw new IllegalStateException("Entity not set for export");
        }
        
        MetaEntity me = e.getMeta();
        out.startEntity("Entity");
        out.textEntity("Name",e.toString());
        out.textEntity("MetaName",me.getName());
        out.textEntity("MetaUUID",me.getKey().toString());
        
        out.startEntity("Properties");
        for(Property p : e.getProperties()){
            out.startEntity("Property");
            out.addAttribute("name",p.getMeta().getName());
            out.addAttribute("type",p.getMeta().getMetaPropertyType().getTypeName());
            out.addAttribute("summary",p.getMeta().isSummary()?"true":"false");
            out.text(p.getValue());
            out.stopEntity(); // Property
        }
        out.stopEntity(); // Properties

        out.startEntity("Relationships");

        
        
        Set<MetaRelationship> metaRelationships = new TreeSet<MetaRelationship>();
        for(Relationship r : e.getConnectedRelationships()){
            metaRelationships.add(r.getMeta());
        }
        
        for(MetaRelationship mr : metaRelationships){
            
            out.startEntity("RelationshipType");
            out.textEntity("Name",mr.getName());
            out.textEntity("UUID",mr.getKey().toString());
            out.textEntity("Description",mr.getDescription());
            
            Set<Relationship> connected = e.getConnectedRelationshipsOf(mr);
            for(Relationship r : connected){

                Role role;
                if(r.start().connectsTo() == e){
                    role = r.finish();
                } else {
                    role = r.start();
                }
                
                out.startEntity("Role");
                out.textEntity("Name",role.getMeta().getName());
                
                Entity other = role.connectsTo();
                out.startEntity("ConnectedEntity");
                out.textEntity("ConnectedUUID", other.getKey().toString());
                out.textEntity("Name",other.toString());
                for(Property p : other.getProperties()){
                	if(p.getMeta().isSummary()){
                		out.startEntity("SummaryProperty");
                		out.addAttribute("name", p.getMeta().getName());
                		out.text(p.getValue());
                		out.stopEntity();
                	}
                }
                out.stopEntity(); // ConnectedEntity
                
                out.stopEntity(); // Role
            }
            out.stopEntity(); // RelationshipType
        }
        out.stopEntity(); // Relationships
        
        out.stopEntity(); // Entity
        
    }

}
