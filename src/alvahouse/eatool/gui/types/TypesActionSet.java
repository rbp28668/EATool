/*
 * TypesActionSet.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.gui.types;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.metamodel.types.ControlledListType;
import alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType;
import alvahouse.eatool.repository.metamodel.types.ExtensibleTypeList;
import alvahouse.eatool.repository.metamodel.types.RegexpCheckedType;
import alvahouse.eatool.repository.metamodel.types.TimeSeriesType;

/**
 * TypesActionSet provides UI actions for creating, editing and deleting extensible types.
 * 
 * @author rbp28668
 */
public class TypesActionSet extends ActionSet {

    private TypesExplorer explorer;
    private Map< Class<? extends ExtensibleMetaPropertyType>, Class<? extends ExtensibleTypeDialog>> editorClasses = new HashMap<>();
    /**
     * 
     */
    public TypesActionSet(TypesExplorer explorer) {
        super();
        this.explorer = explorer;
        addAction("NewType", actionNewType);
        addAction("EditType", actionEditType);
        addAction("DeleteType", actionDeleteType);
        
        // Setup lookup to identify appropriate dialogs to use.
        editorClasses.put(ControlledListType.class,ControlledListTypeDialog.class);
        editorClasses.put(RegexpCheckedType.class, RegexpCheckedTypeDialog.class);
        editorClasses.put(TimeSeriesType.class, TimeSeriesTypeDialog.class);
    }
    
    private final Action actionNewType = new AbstractAction() {
 		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            try {
                ExtensibleTypeList types = (ExtensibleTypeList)explorer.getSelectedNode().getUserObject();
                ExtensibleMetaPropertyType type = types.createNew();
                
                ExtensibleTypeDialog editor = getEditorFor(type);
                editor.setVisible(true);
                
                if(editor.wasEdited()){
                    explorer.getTypes().addType(type);
                }
                
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
            
        }
    };   

    private final Action actionEditType = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                ExtensibleMetaPropertyType type = (ExtensibleMetaPropertyType)explorer.getSelectedNode().getUserObject();
                
                ExtensibleTypeDialog editor = getEditorFor( type);
                editor.setVisible(true);
                
                if(editor.wasEdited()){
                    explorer.getTypes().updateType(type);

                }
                
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
            
        }
    };   

    private final Action actionDeleteType = new AbstractAction() {
		private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            try {
                ExtensibleMetaPropertyType type = (ExtensibleMetaPropertyType)explorer.getSelectedNode().getUserObject();
                if(Dialogs.question(explorer,"Delete type " + type.getTypeName())){
                    explorer.getTypes().deleteType(type);
                }
            } catch(Throwable t) {
                new ExceptionDisplay(explorer,t);
            }
            
        }
    };   

    /**
     * @param types
     * @param type
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     */
    private ExtensibleTypeDialog getEditorFor(ExtensibleMetaPropertyType type) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<? extends ExtensibleMetaPropertyType> implementingClass = type.getClass();
        Class<? extends ExtensibleTypeDialog> editorClass = editorClasses.get(implementingClass);
        Class<?>[] paramTypes = new Class[] {Component.class, String.class};
        Constructor<? extends ExtensibleTypeDialog> constructor = editorClass.getConstructor(paramTypes);
        Object[] params = new Object[] {explorer, "Edit " + type.getTypeName()};
        ExtensibleTypeDialog editor = (ExtensibleTypeDialog) constructor.newInstance(params);
        editor.setMetaPropertyType(type);
        return editor;
    }

}
