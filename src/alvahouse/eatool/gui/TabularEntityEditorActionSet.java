/*
 * TabularEntityEditorActionSet.java
 * Project: EATool
 * Created on 21-Apr-2007
 *
 */
package alvahouse.eatool.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * TabularEntityEditorActionSet
 * 
 * @author rbp28668
 */
public class TabularEntityEditorActionSet extends ActionSet {

    private TabularEntityEditor editor;
    
    /**
     * 
     */
    public TabularEntityEditorActionSet(TabularEntityEditor editor) {
        super();
        this.editor = editor;
        
        addAction("SaveTable", actionSaveTable);
        addAction("CloseTable",actionCloseTable);
        addAction("AddRow", actionAddRow);
        addAction("InsertBefore", actionInsertBefore);
        addAction("InsertAfter", actionInsertAfter);
        addAction("DeleteSelected", actionDeleteSelected);

    }

    /**
     * Comment for <code>actionSaveTable</code>
     */
    private final Action actionSaveTable = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                editor.updateModel();
                editor.setVisible(false);
                editor.dispose();
            } catch(Throwable t) {
                new ExceptionDisplay(editor,t);
            }
            
        }
    };   

    /**
     * Comment for <code>actionClose</code>
     */
    private final Action actionCloseTable = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                editor.setVisible(false);
                editor.dispose();
            } catch(Throwable t) {
                new ExceptionDisplay(editor,t);
            }
            
        }
    };   
    
    /**
     * Comment for <code>actionAddRow</code>
     */
    private final Action actionAddRow = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                editor.appendNew(1);
            } catch(Throwable t) {
                new ExceptionDisplay(editor,t);
            }
            
        }
    };   

    /**
     * Comment for <code>actionInsertBefore</code>
     */
    private final Action actionInsertBefore = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                if(editor.hasSelection()){
                    editor.insertBefore();
                }
            } catch(Throwable t) {
                new ExceptionDisplay(editor,t);
            }
            
        }
    };   

    /**
     * Comment for <code>actionInsertAfter</code>
     */
    private final Action actionInsertAfter = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                if(editor.hasSelection()){
                    editor.insertAfter();
                }
            } catch(Throwable t) {
                new ExceptionDisplay(editor,t);
            }
            
        }
    };   
    /**
     * Comment for <code>actionDeleteSelected</code>
     */
    private final Action actionDeleteSelected = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            try {
                if(editor.hasSelection()){
                    if(Dialogs.question(editor,"Delete selected entities?")){
                        editor.deleteSelected();
                    }
                }
            } catch(Throwable t) {
                new ExceptionDisplay(editor,t);
            }
            
        }
    };   

}
