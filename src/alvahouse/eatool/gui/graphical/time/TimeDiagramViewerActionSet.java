/*
 * TimeDiagramViewerActionSet.java
 * Project: EATool
 * Created on 26-Oct-2006
 *
 */
package alvahouse.eatool.gui.graphical.time;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.gui.graphical.DiagramViewerActionSet;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;

/**
 * TimeDiagramViewerActionSet is the set of actions for a TimeDiagramViewer.
 * 
 * @author rbp28668
 */
public class TimeDiagramViewerActionSet extends DiagramViewerActionSet {

    private Model model;

    /**
     * 
     */
    public TimeDiagramViewerActionSet(TimeDiagramViewer viewer, Application app, Repository repository) {
        super(viewer,app.getSettings(), repository);
        this.model = repository.getModel();
        
		addAction("AddNewItem", actionAddNewItem);
		addAction("AddSymbols", actionAddSymbols);
		addAction("SearchAndAdd", actionSearchAndAdd);

		addAction("SortDefault", actionSortDefault); 
		addAction("SortAlphabetical", actionSortAlphabetical);
		addAction("SortTime", actionSortTime);
		addAction("ScaleDays", actionScaleDays); 
		addAction("ScaleWeeks", actionScaleWeeks); 
		addAction("ScaleMonths", actionScaleMonths); 
		addAction("ScaleYears", actionScaleYears); 

    }

	private final Action actionAddNewItem = new AbstractAction() {
	    
       private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
		    
			try {
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionAddSymbols = new AbstractAction() {
        
	    private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    try{
			        
				    Property[] properties = selectProperties(model);

					if(properties != null){
					    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
					    for(int i=0; i<properties.length; ++i){
					        diagram.addProperty(properties[i]);
					    }
					    viewer.repaint();
					}
				    
				} catch(Exception ex) {
					new ExceptionDisplay(getViewer(),ex);
				}
			    
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionSearchAndAdd = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionSortDefault = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.sortDefault();
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionSortAlphabetical = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.sortAlphabetical();
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionSortTime = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.sortEarliestFirst();
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionScaleDays = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_DAY);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionScaleWeeks = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_WEEK);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionScaleMonths = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_MONTH);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	private final Action actionScaleYears = new AbstractAction() {
        
        private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
		    
			try {
		        TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
			    TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
			    diagram.setTimeAxis(TimeDiagram.SCALE_YEAR);
			    viewer.repaint();
			} catch(Exception ex) {
				new ExceptionDisplay(getViewer(),ex);
			}
		    
		}
	};

	
    private Property[] selectProperties(Model model) throws LogicException {
        
        	TimeDiagramViewer viewer = (TimeDiagramViewer)getViewer();
        	TimeDiagram diagram = (TimeDiagram)viewer.getDiagram();
        	TimeDiagramType type = (TimeDiagramType)diagram.getType();
      		
    		PropertySelectionDialog dlg = new PropertySelectionDialog(viewer, type, model);
    		dlg.setVisible(true);

    		Property[] properties = null;
    		
    		if(dlg.isPropertySelected()){
    		    properties = dlg.getAllSelected();
    		}
    		    
    		return properties;
        }

}
