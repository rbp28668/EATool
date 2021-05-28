/*
 * TimeDiagramType.java
 * Project: EATool
 * Created on 26-Jul-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramDetailFactory;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypeDetailFactory;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.types.TimeSeriesType;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * TimeDiagramType is a diagram type for defining time series diagrams.
 * 
 * @author rbp28668
 */
public class TimeDiagramType extends DiagramType {

    /**
     * <code>targets</code> holds the list of TypeEntry that describes which
     * MetaEntities/MetaProperties can be displayed in this diagram.
     */
    private List<TypeEntry> targets = new LinkedList<TypeEntry>();
    
     /**
     * <code>typeLookup</code> allows the lookup of a TypeEntry by MetaProperty.
     */
    private Map<MetaProperty,TypeEntry> typeLookup = new HashMap<MetaProperty,TypeEntry>();
    
    private Scripts scripts;
    
    /**
     * Default constructor for instantiation using Class.newInstance().
     */
    public TimeDiagramType(Scripts scripts){
        super(new UUID(), scripts);
        this.scripts = scripts;
    }
    
    /**
     * Create a TimeDiagramType from a persisted state where the UUID is known.
     * @param uuid
     */
    public TimeDiagramType(UUID uuid, Scripts scripts) {
        super(uuid, scripts);
        this.scripts = scripts;
    }

    /**
     * Adds a target (TypeEntry - the meta-entity, meta-property and colours needed to display it) to
     * the diagram type.
     * @param entry is the TypeEntry to add.
     */
    public void addTarget(TypeEntry entry) throws Exception{
        assert(entry != null);
        assert(entry.getTargetType() != null);
        assert(entry.getTargetProperty() != null);
        assert(entry.getTargetType().getMetaProperty(entry.getTargetProperty().getKey()) != null);
        assert(entry.getTargetProperty().getMetaPropertyType() instanceof TimeSeriesType);

        for(TypeEntry existing : targets){
            if(existing.getTargetProperty().equals(entry.getTargetProperty())){
                throw new IllegalArgumentException("Can only specify a property once on time diagram");
            }
        }
        targets.add(entry);
        typeLookup.put(entry.targetProperty,entry);
    }


    /**
     * Clears the targets list. 
     */
    public void clearTargets(){
        targets.clear();
    }
    
    /**
     * Gets the collection of targets.
     * @return Collection of TypeEntry, maybe empty, not null.
     */
    public Collection<TypeEntry> getTargets(){
        return Collections.unmodifiableCollection(targets);
    }
    
    /**
     * Looks up a TypeEntry by its MetaProperty.
     * @param mp is the MetaProperty to look up by.
     * @return the corresponding TypeEntry (or null if not found).
     */
    public TypeEntry getTargetFor(MetaProperty mp){
        return (TypeEntry)typeLookup.get(mp);
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#newDiagram(alvahouse.eatool.util.UUID)
     */
    public Diagram newDiagram(UUID key) {
        TimeDiagram diagram = new TimeDiagram(this,key, scripts);
        return diagram;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
		startXML(out);
		for(TypeEntry entry  :  targets){
		    entry.writeXML(out);
		}
		out.stopEntity();
        
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#getDetailFactory()
     */
    public DiagramDetailFactory getDetailFactory() {
        return new TimeDiagramFactory();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#getTypeDetailFactory()
     */
    public DiagramTypeDetailFactory getTypeDetailFactory() {
        return new TimeDiagramTypeFactory();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#removeSymbolsFor(alvahouse.eatool.repository.metamodel.MetaEntity)
     */
    public void removeSymbolsFor(MetaEntity meta) {
        // Should only have one target for a given meta-entity so find it and
        // if found, remove it!
        TypeEntry toRemove = null;
        for(TypeEntry entry : targets){
            if(entry.targetType.equals(meta)){
                toRemove = entry;
                break;
            }
        }
        if(toRemove != null){
            targets.remove(toRemove);
        }
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#removeConnectorsFor(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public void removeConnectorsFor(MetaRelationship meta) {
        // NOP - connectors don't yet feature in time diagrams.
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.DiagramType#validate(alvahouse.eatool.repository.metamodel.MetaRelationship)
     */
    public void validate(MetaRelationship meta) {
        // NOP - as above;
    }

    /**
     * TypeEntry describes a MetaEntity/MetaProperty pair and colours needed to display 
     * that (time series) property.  The type of the meta property must be a TimeSeriesType.
     * 
     * @author rbp28668
     */
    public static class TypeEntry {
        private MetaEntity targetType;
        private MetaProperty targetProperty;
        private Vector<Color> colours;

        
        /** Create a TypeEntry with no colours defined.
         * @param targetType is the MetaEntity containing the targetProperty.
         * @param targetProperty is the MetaProperty to display.
         */
        TypeEntry( MetaEntity targetType, MetaProperty targetProperty) throws Exception{
            assert(targetType != null);
            assert(targetProperty != null);
            assert(targetType.getMetaProperty(targetProperty.getKey()) != null);
            assert(targetProperty.getMetaPropertyType() instanceof TimeSeriesType);
            this.targetType = targetType; 
            this.targetProperty = targetProperty;
        }

        /**
         * Get the target MetaProperty.
         * @return Returns the targetProperty.
         */
        MetaProperty getTargetProperty() {
            return targetProperty;
        }
        /**
         * Get the target MetaEntity.
         * @return Returns the targetType.
         */
        MetaEntity getTargetType() {
            return targetType;
        }
        
        /**
         * Gets the TimeSeriesType used for this property.  Needed to interpret any
         * property data for the corresponding properties in the model.
         * @return the time series type for this property.
         */
        TimeSeriesType getTimeLine(){
            return (TimeSeriesType)targetProperty.getMetaPropertyType();
        }
        
        /**
         * Sets the target property for this entry.
         * @param targetProperty is the new target property to set.
         */
        void setTargetProperty(MetaProperty targetProperty) throws Exception{
            assert(targetProperty != null);
            assert(targetType.getMetaProperty(targetProperty.getKey()) != null);
            this.targetProperty = targetProperty;
        }
        
        /**
         * Gets the collection of colours to display a property in.
         * @return Vector of Color with the colours.
         */
        public Vector<Color> getColours(){
            return colours;
        }

        /**
         * Sets the colours en-masse.
         * @param colours is a Collection of Color.
         */
        public void setColours(Collection<Color> colours) {
            assert(colours != null);
            assert(targetProperty != null);
            assert(((TimeSeriesType)targetProperty.getMetaPropertyType()).getIntervals().size() == colours.size()); 
            this.colours = new Vector<Color>(colours);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString(){
            return targetType.getName() + "(" + targetProperty.getName() + ")";
        }
        
        /**
         * Serialises this TypeEntry to XML.
         * @param out is the XMLWriter to write to.
         * @throws IOException
         */
        public void writeXML(XMLWriter out) throws IOException {
            out.startEntity("TypeEntry");
            out.addAttribute("entity", targetType.getKey().toString());
            out.addAttribute("property",targetProperty.getKey().toString());
            int count = colours.size();
            for(int i=0; i<count; ++i){
                Color colour = (Color)colours.get(i);
                FactoryBase.writeColour(out,"Colour",colour);
            }
            
            out.stopEntity();
        }

    }



}
