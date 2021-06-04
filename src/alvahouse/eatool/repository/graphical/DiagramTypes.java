package alvahouse.eatool.repository.graphical;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.persist.DiagramTypePersistence;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * Collection class to keep track of all diagram types.  It listens for
 * changes to the meta-model so can modify diagram types if necessary.
 * @author bruce.porteous
 *
 */
public class DiagramTypes extends MetaModelChangeAdapter {

	private final DiagramTypePersistence persistence;

	// Manage type families locally rather than in persistence
	private List<DiagramTypeFamily> diagramClasses = new LinkedList<DiagramTypeFamily>(); // of DiagramTypeFamily.
	private Map<UUID,DiagramTypeFamily> familyLookup = new HashMap<UUID,DiagramTypeFamily>(); // of DiagramTypeFamily by UUID.

//	private Map<UUID,DiagramType> typeLookup = new HashMap<UUID,DiagramType>(); // of DiagramType by UUID
	private List<DiagramsChangeListener> changeListeners = new LinkedList<DiagramsChangeListener>(); // of DiagramsChangeListener
	
	
	
	/**
	 * Constructor for DiagramTypes.  This sets up the list of 
	 * DiagramTypeClasses from the config file - this list should not
	 * change while the program is running so additions will not trigger
	 * events.
	 * @param diagramTypePersistence 
	 * @throws ClassNotFoundException
	 */
	public DiagramTypes(DiagramTypePersistence diagramTypePersistence){
	    super();
	    this.persistence = diagramTypePersistence;
	}
	
	/**
	 * Sets up the DiagramTypeFamilies from a configuration.
	 * @param config is the configuration to setup.
	 */
	public void setFamilies(SettingsManager config){
	    
	    SettingsManager.Element root = config.getElement("/DiagramFamilies");
	    for(SettingsManager.Element entry : root.getChildren()){
	        if(entry.getName().equals("DiagramFamily")){
	            String name = entry.attributeRequired("name");
	            String className = entry.attributeRequired("class");
	            
	            try {
                    @SuppressWarnings("unchecked")
					Class<? extends DiagramTypeFamily> familyClass = (Class<? extends DiagramTypeFamily>) Class.forName(className);
                    
                    DiagramTypeFamily typeFamily = (DiagramTypeFamily)familyClass.newInstance();
                    typeFamily.setName(name);
                    
                    
                    addDiagramFamily(typeFamily);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(className + " not found ");
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException(className + " cannot be instantiated ");
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(className + " cannot be accessed ");
                }
	        }
	    }
	}
	
	/**
	 * Adds a DiagramTypeFamily to the list of available type families.
	 * @param typeClass
	 */
	protected void addDiagramFamily(DiagramTypeFamily typeClass){
	    if(typeClass == null){
	        throw new NullPointerException("Can't add null DiagramTypeFamily");
	    }
	    typeClass.setParent(this);
	    diagramClasses.add(typeClass);
	    familyLookup.put(typeClass.getKey(),typeClass);
	}
	
	/**
	 * Adds a DiagramType.
	 * @param dt is the DiagramType to register.
	 */
	public void add(DiagramType dt)  throws Exception {
		if(dt == null) {
			throw new NullPointerException("Adding null diagram type");
		}
 		String user = System.getProperty("user.name");
		dt.getVersion().createBy(user);

		persistence.addDiagramType(dt);
		fireDiagramTypeAdded(dt);
	}

	/**
	 * Adds a diagram type without setting version, firing events etc.
	 * @param dt
	 * @throws Exception
	 */
	public void _add(DiagramType dt)  throws Exception {
		if(dt == null) {
			throw new NullPointerException("Adding null diagram type");
		}
		persistence.addDiagramType(dt);
	}

	/**
	 * @param dt
	 * @throws Exception
	 */
	public void update(DiagramType dt)  throws Exception {
		if(dt == null) {
			throw new NullPointerException("Adding null diagram type");
		}
 		String user = System.getProperty("user.name");
		dt.getVersion().modifyBy(user);

		persistence.updateDiagramType(dt);
		fireDiagramTypeChanged(dt);
	}


   /**
     * @param diagramType
     */
    public void delete(DiagramType diagramType) throws Exception {
		if(diagramType == null) {
			throw new NullPointerException("Can't remove null diagram type");
		}
        persistence.delete(diagramType.getKey());
		fireDiagramTypeDeleted(diagramType);
    }

	/**
     * Allows type lookup by UUID.  Needed for effective de-serialization.
     * @param typeID is the UUID of the type we want.
     * @return the DiagramType given by the typeID or null if not found.
     */
    public DiagramType get(UUID typeID) throws Exception{
        return persistence.getDiagramType(typeID);
    }
	
    /**
     * @return
     * @throws Exception
     */
    public Collection<DiagramType> getDiagramTypes() throws Exception{
    	return persistence.getTypes();
    }
	/**
	 * Adds a listener to be notified of any changes to diagram types.
	 * @param listener is the listener to add.
	 */
	public void addChangeListener(DiagramsChangeListener listener){
	    if(listener == null){
	        throw new NullPointerException("Can't add null DiagramsChangeListener");
	    }
		changeListeners.add(listener);
	}

	/**
	 * Removes a listener.
	 * @param listener is the listener to remove.
	 */
	public void removeChangeListener(DiagramsChangeListener listener){
		changeListeners.remove(listener);
	}
	
	
	/**
	 * Allows anything (such as a dialog) that edits a DiagramType to
	 * fire the event to any registered listeners.
	 * @param dt is the edited DiagramType.
	 */
	public void signalEdited(DiagramType dt) throws Exception{
		fireDiagramTypeChanged(dt);
	}
	
	/**
	 * Get the collection of all DiagramTypeFamily.  Iterate through
	 * this collection, and through the children of DiagramTypeFactory
	 * to get a complete set of DiagramType.
	 * @return DiagramTypeFamily Collection. Maybe empty, never null.
	 */
	public Collection<DiagramTypeFamily> getDiagramTypeFamilies() {
		return Collections.unmodifiableCollection(diagramClasses);
	}
	
	/**
	 * deletes all the diagram types.
	 */
	public void deleteContents()  throws Exception{
	    persistence.dispose();
		fireTypesUpdated();
	}
	
	
	/**
	 * Writes the Diagram types out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("DiagramTypes");
		try {
			for( DiagramType dt : getDiagramTypes()) {
				dt.writeXML(out);
			}
		} catch (Exception e) {
			throw new IOException("Unable to write Diagram Types to XML",e);
		}
		out.stopEntity();
	}
	

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#modelUpdated(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
	@Override
    public void modelUpdated(MetaModelChangeEvent e)  throws Exception {
        // Should only occur in situations where major change 
        // is occuring and that means deletion of everything.
        MetaModel meta = (MetaModel)e.getSource();
        if(meta.getMetaEntityCount() == 0 && meta.getMetaRelationshipCount() == 0){
            deleteContents();
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    @Override
    public void metaEntityDeleted(MetaModelChangeEvent e) {
        // Need to remove any SymbolType that reference the deleted
        // Meta Entity.
    	// TODO review this approach
//        MetaEntity meta = (MetaEntity)e.getSource();
//        for(DiagramType type : typeLookup.values()){
//            type.removeSymbolsFor(meta);
//        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    @Override
    public void metaRelationshipChanged(MetaModelChangeEvent e) throws Exception{
        // If the roles change attached type then potentially 
        // some meta-roles are invalid.
//        MetaRelationship meta = (MetaRelationship)e.getSource();
//        for(DiagramType type : typeLookup.values()){
//            type.validate(meta);
//        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter#metaRelationshipDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    @Override
    public void metaRelationshipDeleted(MetaModelChangeEvent e) {
        // Must remove associated ConnectorTypes.
//        MetaRelationship meta = (MetaRelationship)e.getSource();
//        for(DiagramType type : typeLookup.values()){
//            type.removeConnectorsFor(meta);
//        }
        
    }

     /**
      * Call if a meta-role has changed to allow validation of parent relationship.
     * @param e
     */
    public void metaRoleChanged(MetaModelChangeEvent e) throws Exception {
        // If Attached class has changed then invalidate 
//        MetaRole meta = (MetaRole)e.getSource();
//        for(DiagramType type :  typeLookup.values()){
//            type.validate(meta.getMetaRelationship());
//        }
        
    }
    
	private void fireDiagramTypeAdded(DiagramType dt) throws Exception {
		DiagramsChangeEvent event = new DiagramsChangeEvent(dt);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramTypeAdded(event);
		}
	}
	
	private void fireDiagramTypeChanged(DiagramType dt) throws Exception {
		DiagramsChangeEvent event = new DiagramsChangeEvent(dt);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramTypeChanged(event);
		}
	}
	
	private void fireDiagramTypeDeleted(DiagramType dt)  throws Exception {
		DiagramsChangeEvent event = new DiagramsChangeEvent(dt);
		for(DiagramsChangeListener listener : changeListeners){
			listener.diagramTypeDeleted(event);
		}
	}

	private void fireTypesUpdated()  throws Exception {
		DiagramsChangeEvent event = new DiagramsChangeEvent(null);
		for(DiagramsChangeListener listener : changeListeners){
			listener.typesUpdated(event);
		}
	}

    /**
     * Look up a DiagramTypeFamily by its key.  Provides the mechanism of identifying the proper
     * polymorphic type of a DiagramType when deserialising XML.
     * @param familyKey is the UUID of the DiagramTypeFamily to lookup.
     * @return the corresponding DiagramTypeFactory.
     * @throws IllegalArgumentException if the UUID does not correspond to a known DiagramTypeFamily.
     */
    public DiagramTypeFamily lookupFamily(UUID familyKey) {
        DiagramTypeFamily family = (DiagramTypeFamily)familyLookup.get(familyKey);
        if(family == null){
            throw new IllegalArgumentException("Key does not correspond to a known Diagram Type Family");
        }
        return family;
     }

	/**
	 * Gets diagram types belonging to a given diagram type family.
	 * @param diagramTypeFamily of types to fetch.
	 * @return Collection of diagram types in that factory. May be empty, never null.
	 */
	public Collection<DiagramType> getDiagramTypesOfFamily(DiagramTypeFamily diagramTypeFamily) throws Exception{
		return persistence.getDiagramTypesOfFamily(diagramTypeFamily);
	}

	/**
	 * Deletes diagram types belonging to a given diagram type family.
	 * @param diagramTypeFamily is the family of types to delete.
	 */
	public void deleteDiagramTypesOfFamily(DiagramTypeFamily diagramTypeFamily) throws Exception {
		persistence.deleteDiagramTypesOfFamily(diagramTypeFamily);		
	}

     
}
