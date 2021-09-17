/*
 * MetaModelDiagramTypes.java
 * Project: EATool
 * Created on 17-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.dto.graphical.DiagramTypeDto;
import alvahouse.eatool.repository.graphical.DiagramType;
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
    private DiagramType theType;
    
    /**
     * constructor that sets up the single MetaModelDiagramType.
     */
    public MetaModelDiagramTypes(Repository repository, DiagramTypePersistence persistence){
        super(repository, persistence);
        family = new MetaModelDiagramFamily(this);
        addDiagramFamily(family);
    }
    
    public void addDefaultType(Repository repository) throws Exception {
        family.add(MetaModelDiagramType.getInstance(repository));
    }
    
	/**
	 * Adds a DiagramType.
	 * @param dt is the DiagramType to register.
	 */
    @Override
	public void add(DiagramType dt)  throws Exception {
    	theType = dt;
    }

    @Override
	public void update(DiagramType dt)  throws Exception {
	}

   /**
     * @param diagramType
     */
    @Override
    public void delete(DiagramType diagramType) throws Exception {
    }

	/**
     * Allows type lookup by UUID.  Needed for effective de-serialization.
     * @param typeID is the UUID of the type we want.
     * @return the DiagramType given by the typeID or null if not found.
     */
    @Override
    public DiagramType get(UUID typeID) throws Exception{
    	return theType;
    }
	
    /**
     * @return
     * @throws Exception
     */
    public Collection<DiagramType> getDiagramTypes() throws Exception{
    	List<DiagramType> types = new ArrayList<>(1);
    	types.add(theType);
    	return types; 
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


		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.DiagramTypeFamily#newDiagramType(alvahouse.eatool.repository.Repository)
		 */
		@Override
		public DiagramType newDiagramType(Repository repository) throws Exception {
			MetaModelDiagramType type = new MetaModelDiagramType(repository);
			return type;
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.DiagramTypeFamily#newDiagramType(alvahouse.eatool.repository.Repository, alvahouse.eatool.repository.dto.graphical.DiagramTypeDto)
		 */
		@Override
		public DiagramType newDiagramType(Repository repository, DiagramTypeDto dto) throws Exception {
			// We're ignoring the dto as MetaModelDiagramType has fixed config.
			// Possibly should throw an unsupported operation exception
			MetaModelDiagramType type = new MetaModelDiagramType(repository);
			return type;
		}
		
		
    }
}
