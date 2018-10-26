/*
 * ExtensibleTypeDialog.java
 * Project: EATool
 * Created on 12-Jul-2006
 *
 */
package alvahouse.eatool.gui.types;

import java.awt.Component;

import javax.swing.JDialog;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;

/**
 * ExtensibleTypeDialog
 * 
 * @author rbp28668
 */
public abstract class ExtensibleTypeDialog extends BasicDialog {

    /**
     * @param parent
     * @param title
     */
    public ExtensibleTypeDialog(JDialog parent, String title) {
        super(parent, title);
    }

    /**
     * @param parent
     * @param title
     */
    public ExtensibleTypeDialog(Component parent, String title) {
        super(parent, title);
    }


    /**
     * @param type
     */
    public abstract void setMetaPropertyType(ExtensibleMetaPropertyType type);


    /**
     * @return Returns the type.
     */
    public abstract ExtensibleMetaPropertyType getMetaPropertyType();
}
