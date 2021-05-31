/*
 * ExportMappingActionSet.java
 * Project: EATool
 * Created on 31-Dec-2005
 *
 */
package alvahouse.eatool.gui.mappings;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.mapping.ExportMapping;
import alvahouse.eatool.repository.mapping.ExportMappings;

/**
 * ExportMappingActionSet
 * 
 * @author rbp28668
 */
public class ExportMappingActionSet extends ActionSet {

	private ExportMappingExplorer explorer;
	private ExportMappings mappings;

    /**
     * 
     */
    public ExportMappingActionSet(ExportMappingExplorer explorer, ExportMappings mappings) {
        super();
        if(explorer == null) {
            throw new NullPointerException("Can't create ExportMappingActionSet with null explorer");
        }
        if(mappings == null) {
            throw new NullPointerException("Can't explore null export mappings");
        }
        
        this.explorer = explorer;
        this.mappings = mappings;
        
		addAction("ExportMappingNew",actionExportMappingNew);
		addAction("ExportMappingEdit",actionExportMappingEdit);
		addAction("ExportMappingDelete",actionExportMappingDelete);
        
    }

    private final Action actionExportMappingNew = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
			    ExportMapping mapping = new ExportMapping();
			    ExportMappingDialog dialog = new ExportMappingDialog(explorer,"New Export Mapping",mapping);
			    dialog.setVisible(true);
			    if(dialog.wasEdited()){
			        mappings.add(mapping);
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   
	
	private final Action actionExportMappingEdit = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
                ExportMapping mapping = (ExportMapping)explorer.getSelectedNode().getUserObject();
                ExportMappingDialog dialog = new ExportMappingDialog(explorer,"New Export Mapping",mapping);
                dialog.setVisible(true);
                if(dialog.wasEdited()){
                    mappings.update(mapping);
                }
            } catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

	private final Action actionExportMappingDelete = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			try {
                ExportMapping mapping = (ExportMapping)explorer.getSelectedNode().getUserObject();
                if(JOptionPane.showConfirmDialog(null,"Delete mapping " + mapping.getName() + "?","EATool",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                    mappings.remove(mapping);
                }
			} catch(Throwable t) {
				new ExceptionDisplay(explorer,t);
			}
		}
	};   

    
}
