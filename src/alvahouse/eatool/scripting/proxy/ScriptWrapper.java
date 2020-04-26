/*
 * ScriptWrapper.java
 * Project: EATool
 * Created on 24-Mar-2006
 *
 */
package alvahouse.eatool.scripting.proxy;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.gui.scripting.proxy.StandardDiagramViewerProxy;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
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

    public static Object wrap(
            StandardDiagram diagram,
            Repository repository){
        return new StandardDiagramProxy(diagram, repository);
    }
 
    public static Object wrap(
    		StandardDiagramViewer viewer, 
    		StandardDiagram diagram, 
    		Application app, 
    		Repository repository) {
    	return new StandardDiagramViewerProxy(viewer, diagram, app, repository);
    }

    public static Object wrap(Object target){
        if(target instanceof Entity){
            EntitySet set = new EntitySet();
            set.add((Entity)target);
            return set;
        }

        if(target instanceof Relationship){
            RelationshipSet set = new RelationshipSet();
            set.add((Relationship)target);
            return set;
        }

        if(target instanceof MetaEntity){
            MetaEntitySet set = new MetaEntitySet();
            set.add((MetaEntity)target);
            return set;
        }

        if(target instanceof MetaRelationship){
            MetaRelationshipSet set = new MetaRelationshipSet();
            set.add((MetaRelationship)target);
            return set;
        }
        
        return target;
    }
}
