/*
 * ControlledListType.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.awt.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JComboBox;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.ClassUtils;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * ControlledListType is a type which constrains the allowed values to entries
 * in a controlled list.  Usually displayed as a drop-down list.
 * 
 * @author rbp28668
 */
public class ControlledListType extends ExtensibleMetaPropertyType {

    private LinkedList<String> values = new LinkedList<String>();
    private String defaultValue = "";
    
    /**
     * 
     */
    public ControlledListType() {
        super();
    }

    /**
     * @param key
     */
    public ControlledListType(UUID key) {
        super(key);
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity(ClassUtils.baseClassNameOf(this));
        writeAttributesXML(out);

        for(String value : values){
            out.textEntity("Value",value);
        }
        out.textEntity("Default", defaultValue);
        out.stopEntity();
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#validate(java.lang.String)
     */
    public String validate(String value) throws IllegalArgumentException {
        if(!values.contains(value)){
            throw new IllegalArgumentException("Invalid value for controlled list");
        }
        return value;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#initialise()
     */
    public String initialise() {
        return defaultValue;
    }

    /**
     * Adds a possible value to the list.
     * @param value is the value to add.
     */
    public void add(String value){
        if(!values.contains(value)){
            if(values.isEmpty()){
                defaultValue = value;
            }
            values.add(value);
        }
    }
    
    /**
     * Empties the list.
     */
    public void clear(){
        values.clear();
    }
    
    /**
     * Removes a value from the list.
     * @param value is the entry to remove.
     */
    public void remove(String value){
        if(defaultValue != null && value.equals(defaultValue)){
            defaultValue = (values.isEmpty()) ? "" : (String)values.getFirst();
        }
        values.remove(value);
    }
    
    /**
     * Sets the default value for this type.
     * @param def is the default value - note that it must exist in the list.
     */
    public void setDefault(String def){
        if(!values.contains(def)){
            throw new IllegalArgumentException("Default value is not in list");
        }
        defaultValue = def;
    }
    
    /**
     * Gets the list of allowed values.
     * @return an unmodifiable Collection of the allowed values.
     */
    public Collection<String> getValues() {
        return Collections.unmodifiableCollection(values);
    }
    
    /**
     * Moves an entry up the list.
     * @param value is the value to move up the list.
     */
    public void moveUp(String value){
        int idx = values.indexOf(value);
        if(idx == -1){
            throw new IllegalArgumentException(value + " does not exist in list");
        }
        if(idx == 0){
            throw new IllegalArgumentException("Can't move up first value in list");
        }
        values.remove(value);
        values.add(idx-1,value);
    }
    
    /**
     * Moves an entry down the list.
     * @param value is the value to move down the list.
     */
    public void moveDown(String value){
        int idx = values.indexOf(value);
        if(idx == -1){
            throw new IllegalArgumentException(value + " does not exist in list");
        }
        if(idx == values.size()-1){
            throw new IllegalArgumentException("Can't move end entry down list");
        }
        
        values.remove(value);
        values.add(idx+1,value);
        
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#getEditor(java.lang.Object)
     */
    public Component getEditor(Object value) {
        JComboBox combo = new JComboBox(values.toArray());
        combo.setSelectedItem(value);
        combo.setEditable(false);	// select only
        return combo;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#getEditValue(java.awt.Component)
     */
    public String getEditValue(Component component) {
        if(!(component instanceof JComboBox)){
            throw new IllegalArgumentException("Invalid type of component");
        }
        JComboBox combo = (JComboBox)component;
        
        return (String)combo.getSelectedItem();
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Value")){
            add(getXMLValue());
        } else if(local.equals("Default")){
            setDefault(getXMLValue());
        }
    }

}
