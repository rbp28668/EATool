/*
 * MetaModelDiagramTypes.java
 * Project: EATool
 * Created on 17-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.metamodel;

import alvahouse.eatool.repository.graphical.DiagramTypeFamily;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.util.UUID;

/**
 * MetaModelDiagramTypes is a collection of MetaModelDiagramType.  The
 * collection will only have a single instance of the MetaModelDiagramType
 * and MetaModelDiagramFamily;
 * 
 * @author rbp28668
 */
public class MetaModelDiagramTypes extends DiagramTypes {

	public static final UUID FAMILY_KEY = new UUID("7E1EE25C-9E11-4c68-A44B-FF3F401916DC");
    private MetaModelDiagramFamily family;
    
    /**
     * constructor that sets up the single MetaModelDiagramType.
     */
    public MetaModelDiagramTypes(DiagramTypePersistence persistence){
        super(persistence);
        family = new MetaModelDiagramFamily(this);
        addDiagramFamily(family);
    }
    
    public void addDefaultType() throws Exception {
        family.add(MetaModelDiagramType.getInstance());
    }
    
    /**
     * MetaModelDiagramFamily is a DiagramTypeFamily hard-wired just to contain
     * a MetaModelDiagramType.
     * 
     * @author rbp28668
     */
    private static class MetaModelDiagramFamily extends DiagramTypeFamily {
        
        /**
         * @param types is the parent DiagramTypes.
         */
        public MetaModelDiagramFamily(DiagramTypes types) {
            super(MetaModelDiagramType.class,FAMILY_KEY);
            setParent(types);
            setName("Meta-Model Diagram Types");
        }
    }
}
