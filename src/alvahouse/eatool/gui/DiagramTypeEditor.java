/*
 * DiagramTypeEditor.java
 * Project: EATool
 * Created on 05-Oct-2006
 *
 */
package alvahouse.eatool.gui;

import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.metamodel.MetaModel;

/**
 * DiagramTypeEditor should be implemented by any diagram type editor.
 * Note that there is an implied constructor:
 * <code>DiagramTypeEditor(Component)</code>
 * 
 * @author rbp28668
 */
public interface DiagramTypeEditor {

    
    /**
     * @param diagramType
     * @param metaModel
     * @param diagramTypes
     */
    void init(DiagramType diagramType, MetaModel metaModel, DiagramTypes diagramTypes) throws Exception;

    /**
     * @param b
     */
    void setVisible(boolean b);

    /**
     * @return
     */
    boolean wasEdited();

}
