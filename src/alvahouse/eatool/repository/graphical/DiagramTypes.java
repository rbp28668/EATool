package alvahouse.eatool.repository.graphical;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
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

	private List<DiagramTypeFamily> diagramClasses = new LinkedList<DiagramTypeFamily>(); // of DiagramTypeFamily.
	private Map<UUID,DiagramType> typeLookup = new HashMap<UUID,DiagramType>(); // of DiagramType by UUID
	private List<DiagramsChangeListener> changeListeners = new LinkedList<DiagramsChangeListener>(); // of DiagramsChangeListener
	private Map<UUID,DiagramTypeFamily> familyLookup = new HashMap<UUID,DiagramTypeFamily>(); // of DiagramTypeFamily by UUID.

	
	
	/**
	 * Constructor for DiagramTypes.  This sets up the list of 
	 * DiagramTypeClasses from the config file - this list should not
	 * change while the program is running so additions will not trigger
	 * events.
	 * @throws ClassNotFoundException
	 */
	public DiagramTypes(){
	    super();
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
	 * Register a DiagramType so that it can be looked up by its key using
	 * get(UUID).
	 * @param dt is the DiagramType to register.
	 */
	public void registerType(DiagramType dt)  throws Exception {
		if(dt == null) {
			throw new NullPointerException("Adding null diagram type");
		}
		typeLookup.put(dt.getKey(),dt);
		fireDiagramTypeAdded(dt);
	}
	
	/**
	 * Removes the registration for a DiagramType.  After calling this
	 * it will no longer be able to look it up using get(UUID).
	 * @param dt is the DiagramType to un-register.
	 */
	public void unregisterType(DiagramType dt)  throws Exception {
		if(dt == null) {
			throw new NullPointerException("Can't remove null diagram type");
		}
		typeLookup.remove(dt.getKey());
		fireDiagramTypeDeleted(dt);
	}

   /**
     * @param diagramType
     */
    public void delete(DiagramType diagramType) throws Exception {
        DiagramTypeFamily family = diagramType.getFamily();
        family.remove(diagramType); // calls unregisterType(DiagramType);
    }

	/**
     * Allows type lookup by UUID.  Needed for effective de-serialization.
     * @param typeID is the UUID of the type we want.
     * @return the DiagramType given by the typeID or null if not found.
     */
    public DiagramType get(UUID typeID) {
        return typeLookup.get(typeID);
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
	    typeLookup.clear();
	    for(DiagramTypeFamily family : diagramClasses){
	        family.deleteContents();
	    }
		fireTypesUpdated();
	}
	
	
	/**
	 * Writes the Diagram types out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("DiagramTypes");
		for( DiagramType dt : typeLookup.values()) {
			dt.writeXML(out);
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
        MetaEntity meta = (MetaEntity)e.getSource();
        for(DiagramType type : typeLookup.values()){
            type.removeSymbolsFor(meta);
        }
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    @Override
    public void metaRelationshipChanged(MetaModelChangeEvent e) {
        // If the roles change attached type then potentially 
        // some meta-roles are invalid.
        MetaRelationship meta = (MetaRelationship)e.getSource();
        for(DiagramType type : typeLookup.values()){
            type.validate(meta);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.MetaModelChangeAdapter#metaRelationshipDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
     */
    @Override
    public void metaRelationshipDeleted(MetaModelChangeEvent e) {
        // Must remove associated ConnectorTypes.
        MetaRelationship meta = (MetaRelationship)e.getSource();
        for(DiagramType type : typeLookup.values()){
            type.removeConnectorsFor(meta);
        }
        
    }

     /**
      * Call if a meta-role has changed to allow validation of parent relationship.
     * @param e
     */
    public void metaRoleChanged(MetaModelChangeEvent e) {
        // If Attached class has changed then invalidate 
        MetaRole meta = (MetaRole)e.getSource();
        for(DiagramType type :  typeLookup.values()){
            type.validate(meta.getMetaRelationship());
        }
        
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

     
}
