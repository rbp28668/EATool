/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaPropertyContainer;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.repository.model.PropertyContainer;

/*=================================================================*/
// PropertiesPanel is the edit panel for the entity's properties
/*=================================================================*/
public class PropertiesPanel extends JPanel{
    
    private static final long serialVersionUID = 1L;
    private Map<Property,Component> components = new HashMap<Property,Component>();

    /**
	 * Creates a new PropertiesPanel and initialises the contained
	 * JTable & PropertiesTableModel that provides the display and 
	 * editing of the entity.
	 * @param e is the Entity to be edited.
	 */
    PropertiesPanel(PropertyContainer pc, MetaPropertyContainer mpc) {
        
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0f;
        c.weighty = 0.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 10;
        c.insets = new Insets(5,2,5,2);

        
        setBorder(new StandardBorder());
        
        Component firstEdit = null;
        for(MetaProperty mp : mpc.getMetaProperties()){
            
            c.gridwidth = 1; // start of row.
            c.fill = GridBagConstraints.HORIZONTAL;
            
            
            JLabel name = new JLabel(mp.getName());
            name.setToolTipText(mp.getDescription());
            layout.setConstraints(name,c);
            add(name);

            JLabel type = new JLabel("(" + mp.getMetaPropertyType().getTypeName() + ")");
            type.setToolTipText(mp.getDescription());
            layout.setConstraints(type,c);
            add(type);
            
            Property p = pc.getPropertyByMeta(mp.getKey());
            Component component;
            if(mp.isReadOnly()) {
                component = mp.getMetaPropertyType().getRenderer(p.getValue());
            } else {
                component = mp.getMetaPropertyType().getEditor(p.getValue());
                if(firstEdit == null){
                    firstEdit = component;
                }
            }
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = GridBagConstraints.REMAINDER; //end row
            layout.setConstraints(component,c);
            add(component);
            
            components.put(p,component);
        }
        c.gridwidth = 1;
        c.weighty = 1.0f;
        JLabel padding = new JLabel();
        layout.setConstraints(padding,c);
        add(padding);
        
        if(firstEdit != null){
            firstEdit.requestFocus();
        }
    }
    
	/**
	 * Method onOK - called when the OK button is clicked
	 *  to tidy up any current edit.
	 */
    void onOK() {
        for(Map.Entry<Property, Component> entry: components.entrySet()){
            Property p = entry.getKey();
            Component c = entry.getValue();
            MetaProperty meta = p.getMeta();
            if(!meta.isReadOnly()){
                String value = meta.getMetaPropertyType().getEditValue(c);
                p.setValue(value);
            }
        }
    }
    
    /**
     * Validates the properties in the model.  If invalid a message is displayed
     * and the offending row highlighted.
     * @return true if valid, false if not.
     */
    public boolean validateInput(){
        boolean valid = true;
        for(Map.Entry<Property, Component> entry: components.entrySet()){
            Property p = entry.getKey();
            Component c = entry.getValue();
            MetaProperty meta = p.getMeta();
            
            String value = meta.getMetaPropertyType().getEditValue(c);
            if(value.trim().length() == 0){
                if(meta.isMandatory()){
                    Dialogs.warning(this,"Must supply a value for " + meta.getName());
                    c.requestFocusInWindow();
                    valid = false;
                    break;
                }
            } else {
                MetaPropertyType type = meta.getMetaPropertyType();
                try {
                    type.validate(value);
                } catch (Exception e){
                    Dialogs.warning(this,"Value " + value + " is not a valid " + type.getTypeName());
                    c.requestFocusInWindow();
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

}