/*
 * ExportMappingChangeListener.java
 * Project: EATool
 * Created on 31-Dec-2005
 *
 */
package alvahouse.eatool.repository.mapping;

/**
 * ExportMappingChangeListener
 * 
 * @author rbp28668
 */
public interface ExportMappingChangeListener {

    /**
     * @param e
     */
    void MappingAdded(MappingChangeEvent e);

    /**
     * @param e
     */
    void MappingEdited(MappingChangeEvent e);

    /**
     * @param e
     */
    void MappingDeleted(MappingChangeEvent e);

}
