/*
 * ScriptWrapper.java
 * Project: EATool
 * Created on 24-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.gui.graphical.time.TimeDiagram;
import alvahouse.eatool.gui.graphical.time.TimeDiagramViewer;
import alvahouse.eatool.gui.html.HTML;
import alvahouse.eatool.gui.html.HTMLDisplay;
import alvahouse.eatool.gui.scripting.proxy.HTMLDisplayProxy;
import alvahouse.eatool.gui.scripting.proxy.HTMLPageScriptProxy;
import alvahouse.eatool.gui.scripting.proxy.HTMLProxy;
import alvahouse.eatool.gui.scripting.proxy.StandardDiagramViewerProxy;
import alvahouse.eatool.gui.scripting.proxy.TimeDiagramViewerProxy;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.html.HTMLPage;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;

/**
 * ScriptWrapper wraps objects with their scripting proxy equivalents.
 * 
 * @author rbp28668
 */
public class ScriptWrapper {

    /**
     * 
     */
    public ScriptWrapper() {
        super();
    }

    public static StandardDiagramProxy wrap( StandardDiagram diagram){
        return new StandardDiagramProxy(diagram);
    }
 
    public static StandardDiagramViewerProxy wrap(
    		StandardDiagramViewer viewer, 
    		StandardDiagram diagram, 
    		Application app, 
    		Repository repository) {
    	return new StandardDiagramViewerProxy(viewer, diagram, app, repository);
    }

    public static TimeDiagramProxy wrap(TimeDiagram diagram) {
    	return new TimeDiagramProxy(diagram);
    }

    public static TimeDiagramViewerProxy wrap(
    		TimeDiagramViewer viewer, 
    		TimeDiagram diagram, 
    		Application app, 
    		Repository repository) {
    	return new TimeDiagramViewerProxy(viewer, diagram, app, repository);
    }

    public static HTMLProxy wrap(HTML html){
    	return new HTMLProxy(html);
    }

    public static HTMLPageProxy wrap(HTMLPage page){
    	return new HTMLPageProxy(page);
    }

    public static HTMLPageScriptProxy wrap(HTMLDisplay display, Application app, Repository repository){
    	return new HTMLDisplayProxy(display, app, repository );
    }

    public static EntitySet wrap(Entity target){
        EntitySet set = new EntitySet();
        set.add((Entity)target);
        return set;
    }

    
    public static RelationshipSet wrap(Relationship target){
        RelationshipSet set = new RelationshipSet();
        set.add((Relationship)target);
        return set;
    }

    public static MetaEntitySet wrap( MetaEntity target){
        MetaEntitySet set = new MetaEntitySet();
        set.add((MetaEntity)target);
        return set;
    }

    public static MetaRelationshipSet wrap( MetaRelationship target){
        MetaRelationshipSet set = new MetaRelationshipSet();
        set.add((MetaRelationship)target);
        return set;
    }

	/**
	 * @param item
	 * @return
	 */
	public static Object wrapObject(Object item) {
		if(item instanceof Entity) {
			return wrap((Entity)item);
		} else if(item instanceof Relationship) {
			return wrap((Relationship) item);
		} else if(item instanceof MetaEntity) {
			return wrap ((MetaEntity)item);
		} else if(item instanceof MetaRelationship) {
			return wrap ((MetaRelationship)item);
		}
		
		throw new IllegalArgumentException("Don't know how to wrap " + item.getClass().getName());
	}
        
}
