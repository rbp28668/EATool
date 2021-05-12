/*
 * ModelBrowser.java
 * Project: EATool
 * Created on 24-Feb-2006
 *
 */
package alvahouse.eatool.gui;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import alvahouse.eatool.Application;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.types.Keys;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;

/**
 * ModelBrowser is a HTMLProxy based browser for simple browsing of the model or
 * meta-model.
 * 
 * @author rbp28668
 */
public class ModelBrowser extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private DisplayPane display;
    private URLResolver resolver;
    private Application app;
    
    private final static String WINDOW_SETTINGS = "/Windows/ModelBrowser";
    
    private final static String NAME_TABLE_DATA = "<td style=\"white-space:nowrap; display:inline;\">";

    /**
     * Creates a new, empty browser.
     */
    public ModelBrowser(Application app, Repository repository){
        this.app = app;
        
        setTitle("Model Browser");
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        resolver = new URLResolver(app,repository);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        display = new DisplayPane();
        JScrollPane scrollPane = new JScrollPane(display);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
    }
    
    /**
     * Browses the meta-model.
     * @param metaModel is the meta-model to browse.
     */
    public void browse(MetaModel metaModel) throws Exception{
        display.setPage(metaModel);
    }

    public void browse(MetaEntity me, Model model) throws Exception{
        display.setPage(me,model);
    }
    
    public void browseAsTable(MetaEntity me, Model model) throws Exception{
        display.setPageAsTable(me,model);
    }
    
    public void browse(Entity e) throws Exception{
        display.setPage(e);
    }
    
    public void browse(Entity[] entities) throws Exception{
        display.setPage(entities);
    }

	/**
	 * @param entities
	 */
	public void browse(Collection<Entity> entities)  throws Exception{
		browse(entities.toArray(new Entity[entities.size()]));
	}

    public void browse(MetaEntity[] metaEntities) throws Exception{
        display.setPage(metaEntities);
    }
    
    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS, app);
        app.getWindowCoordinator().removeFrame(this);
        super.dispose();
    }

    private class DisplayPane extends JEditorPane{
        private static final long serialVersionUID = 1L;

        public DisplayPane() {
            super();
            setEditable(false);
            setContentType("text/html");
            
            addHyperlinkListener( new HyperlinkListener() {
               public void hyperlinkUpdate(HyperlinkEvent ev){
                   try{
	                   if(ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
	                       URL url = ev.getURL();
	                       if(url != null){
	                           resolver.resolve(url,ModelBrowser.this);
	                       }
	                   }
                   } catch (Exception e){
                       new ExceptionDisplay(ModelBrowser.this,e);
                   }
                   
               }
            });
         }

        public void setPage(MetaModel metaModel) throws Exception{
            ToHTML html = new ToHTML(metaModel);
             setText(html.toString());
        }

        public void setPage(MetaEntity me, Model model)  throws Exception{
            ToHTML html = new ToHTML(me, model);
             setText(html.toString());
        }

        public void setPageAsTable(MetaEntity me, Model model) throws Exception{
            ToHTML html = new ToHTML(me, model, true);
             setText(html.toString());
        }

        public void setPage(Entity e) throws Exception{
            ToHTML html = new ToHTML(e);
             setText(html.toString());
        }
        
        public void setPage(Entity[] entities) throws Exception{
            ToHTML html = new ToHTML(entities);
            setText(html.toString());
        }
        
        public void setPage(MetaEntity[] metaEntities) throws Exception{
            ToHTML html = new ToHTML(metaEntities);
            setText(html.toString());
        }
    }
    
    /**
     * Inner class to generate HTMLProxy for various types of "thing" or styles of display.
     */

    public static class ToHTML {
        private StringBuffer buff = new StringBuffer(512);

        /**
         * Creates HTMLProxy that describes the Meta-Entities in the meta model.
         * @param m is the meta-model to show.
         */
        public ToHTML(MetaModel m) throws Exception{
            header();
            
            h1("Entity Types");
            buff.append("<dl>");
            List<MetaEntity> metaEntities = new LinkedList<MetaEntity>();
            metaEntities.addAll(m.getMetaEntities());
            Collections.sort(metaEntities, new MetaEntity.Compare());
            for(MetaEntity me : metaEntities){
                if(!me.isAbstract() && me.getDisplayHint() != null){
	                buff.append("<dt>");
	                a("http://localhost/metaEntity/" + me.getKey().toString(), me.getName());
	                a("http://localhost/metaEntityTable/" + me.getKey().toString(), " (view as table) ");
	                buff.append("</dt>");
	                if(me.getDescription() != null){
	                    buff.append("<dd>");
	                    buff.append(me.getDescription());
	                    buff.append("<dd>");
	                }
                }
            }
            buff.append("</dl>");
            
            footer();
        }

        /**
         * Creates HTMLProxy that describes a subset of the Meta-Entities in the meta model.
         * @param metaEntities are the meta-entities to show to show.
         */
        public ToHTML(MetaEntity[] metaEntities) throws Exception{
            header();
            
            h1("Entity Types");
            buff.append("<dl>");
            
            for(int i = 0; i<metaEntities.length; ++i){
                MetaEntity me = metaEntities[i];
                if(!me.isAbstract() && me.getDisplayHint() != null){
	                buff.append("<dt>");
	                a("http://localhost/metaEntity/" + me.getKey().toString(), me.getName());
	                a("http://localhost/metaEntityTable/" + me.getKey().toString(), " (view as table) ");
	                buff.append("</dt>");
	                if(me.getDescription() != null){
	                    buff.append("<dd>");
	                    buff.append(me.getDescription());
	                    buff.append("<dd>");
	                }
                }
            }
            buff.append("</dl>");
            
            footer();
        }

        /**
         * Creates HTMLProxy for the list of entities of a given type.
         * @param me is the MetaEntity that describes the type of Entities
         * to be browsed.
         * @param model is the Model to get the Entities to be browsed.
         */
        public ToHTML(MetaEntity me, Model model) throws Exception{
            this(me,model,false);
        }

        /**
         * Creates HTMLProxy for the list of entities of a given type.
         * @param me is the MetaEntity that describes the type of Entities
         * to be browsed.
         * @param model is the Model to get the Entities to be browsed.
         */
        public ToHTML(MetaEntity me, Model model, boolean table) throws Exception{
            if(table){
                entityTable(me,model);
            } else {
                entityList(me,model);
            }
        }

        /**
         * Display an arbitrary array of entities.  These are grouped by meta-entity and sorted
         * within each group.
         * @param entities is the array of entities to display.
         */
        public ToHTML(Entity[] entities) throws Exception{
            header();
            
            h1("Entities");
            hr();
            a("http://localhost/metaModel","Up to List of Entity Types");
            hr();
            
            Map<MetaEntity,List<Entity>> meta = new HashMap<MetaEntity,List<Entity>>();
            for(int i=0; i<entities.length; ++i){
                Entity e = entities[i];
                List<Entity> values = meta.get(e.getMeta());
                if(values == null){
                    values = new LinkedList<Entity>();
                    meta.put(e.getMeta(),values);
                }
                values.add(e);
            }
            
            for( Map.Entry<MetaEntity,List<Entity>> entry : meta.entrySet()){
            	MetaEntity me = entry.getKey();
                h2(me.toString());
                buff.append("<ul>");

                // See if any of the meta-properties is marked as summary.
                boolean hasSummary = false;
                for(MetaProperty mp : me.getMetaProperties()){
                	if(mp.isSummary()){
                		hasSummary = true;
                		break;
                	}
                }
                
                // Get and sort the list of entities for the current meta entity.
                List<Entity> values = entry.getValue();
                Collections.sort(values, new Comparator<Entity>(){
                	public int compare(Entity e0, Entity e1) {
                		return e0.toString().compareTo(e1.toString());
                	}
                });
                
                // Display the entities.
                for(Entity e : values){
                    buff.append("<li>");
                    a("http://localhost/entity/" + e.getKey().toString(), e.toString());
                    
                    if(hasSummary){
	                    buff.append("<table>");
	                    for(Property p : e.getProperties()){ 
	                    	MetaProperty mp = p.getMeta();
	                    	if(mp.isSummary()) {
		                        buff.append("<tr>");
		                        td(p.getMeta().getName());
		                        td(p.getValue());
		                        buff.append("</tr>");
	                    	}
	                    }
	                    buff.append("</table>");
                    }
                    
                    buff.append("</li>");

                }

                buff.append("</ul>");
                
            }
            footer();
        }
        
        /**
         * Creates the HTMLProxy for a single Entity.
         * @param e is the Entity to create HTMLProxy for.
         */
        public ToHTML(Entity e) throws Exception{
            header();

            MetaEntity me = e.getMeta();

            h1(e.toString() + " (" + me.getName() + ")");

            hr();
            a("http://localhost/metaEntity/" + me.getKey().toString(), "All Entities of type " +  me.getName());
            buff.append(" - ");
            a("http://localhost/metaModel","Up to List of Entity Types");
            hr();
            
            buff.append("<table>");
            for(Property p : e.getProperties()){
                buff.append("<tr>");
                td(p.getMeta().getName());
                td(p.getValue());
                buff.append("</tr>");
            }
            buff.append("</table>");
            
            Set<MetaRelationship> metaRelationships = new TreeSet<MetaRelationship>();
            for(Relationship r : e.getConnectedRelationships()){
                metaRelationships.add(r.getMeta());
            }
            
            for(MetaRelationship mr : metaRelationships){
                
                h2(mr.getName());
                Set<Relationship> connected = e.getConnectedRelationshipsOf(mr);
                buff.append("<table>");
                for(Relationship r : connected){
                    buff.append("<tr>");
                    Role role;
                    if(r.start().connectsTo().equals(e)){
                        role = r.finish();
                    } else {
                        role = r.start();
                    }
                    
                    td(role.getMeta().getName());
                    buff.append(NAME_TABLE_DATA);
                    Entity other = role.connectsTo();
                    a("http://localhost/entity/" + other.getKey().toString(), other.toString());
//                    a("local://localhost/entity/" + role.connectsTo().getKey().toString(), role.connectsTo().toString());
                    buff.append("</td>");
                    
                    // Display any summary properties of the linked entity
                    for(Property p : other.getProperties()){
                    	if(p.getMeta().isSummary()){
                            td(p.getValue());
                    	}
                    }
                    
                    // and add any properties of the relationship itself
                    // boolean gets specially treatment as instead of Preferred:true we just have Preferred or blank.
                    for(Property p : r.getProperties()) {
                    	if(p.getMeta().getMetaPropertyType().getKey().equals(Keys.TYPE_BOOLEAN)) {
                        	td( (p.getValue().equals("true")) ? p.getMeta().getName() : "&nbsp;" );
                    	} else {
                    		td(p.getMeta().getName() + ": " + p.getValue());
                    	}
                    }
                    
                    
                    
                    buff.append("</tr>");
                }
                buff.append("</table>");
            }
            
            footer();
        }

        private void entityList(MetaEntity me, Model model){
            header();
            
            h1(me.getName());
            hr();
            a("http://localhost/metaModel","Up to List of Entity Types");
            hr();
            buff.append("<ul>");
            List<Entity> entities = model.getEntitiesOfType(me);
            Collections.sort(entities, new Entity.Compare());
            for(Entity e : entities){
                buff.append("<li>");
                a("http://localhost/entity/" + e.getKey().toString(), e.toString());
                buff.append(" ");
                for(Property p : e.getProperties()){
                	if(p.getMeta().isSummary()){
                		buff.append(p.getValue());
                	}
                }
                buff.append("</li>");
            }
            buff.append("</ul>");
            
            footer();
        }

        /**
         * Creates HTMLProxy for the list of entities of a given type.
         * @param me is the MetaEntity that describes the type of Entities
         * to be browsed.
         * @param model is the Model to get the Entities to be browsed.
         */
        private void entityTable(MetaEntity me, Model model) throws Exception{
            header();
            
            h1(me.getName());
            hr();
            a("http://localhost/metaModel","Up to List of Entity Types");
            hr();
            buff.append("<table>");
           
            buff.append("<tr>");
            for(MetaProperty mp : me.getMetaProperties()){
                buff.append("<th>");
                buff.append(mp.getName());
                buff.append("</th>");
            }
            buff.append("</tr>");

            List<Entity> entities = model.getEntitiesOfType(me);
            Collections.sort(entities, new Entity.Compare());
            for(Entity e : entities){
                buff.append("<tr>");
                for(MetaProperty mp : me.getMetaProperties()){
                    Property p = e.getPropertyByMeta(mp.getKey());
                    buff.append("<td>");
                    buff.append(p.getValue());
                    buff.append("</td>");
                }
                buff.append("</tr>");
            }
            buff.append("</table>");
            
            footer();
        }
        
        
        private void header(){
            buff.append("<html><head></head><body>");
        }
        
        private void footer(){
            buff.append("</body></html>");
        }
 
        private void hr(){
            buff.append("<hr></hr>");
        }
        
        private void td(String s){
            buff.append("<td>");
            buff.append(s);
            buff.append("</td>");
        }

        private void h1(String h1){
            buff.append("<h1>");
            buff.append(h1);
            buff.append("</h1>");
        }

        private void h2(String h2){
            buff.append("<h2>");
            buff.append(h2);
            buff.append("</h2>");
        }

        private void h3(String h3){
            buff.append("<h3>");
            buff.append(h3);
            buff.append("</h3>");
        }
        
        private void a(String href, String text){
            buff.append("<a href=\"" + href + "\">");
            buff.append(text);
            buff.append("</a>");
        }
        
        public String toString(){
            return buff.toString();
        }
        
    }

 
}
