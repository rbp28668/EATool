/*
 * MatrixActionSet.java
 * Created on 30-Jul-2005
 *
 */
package alvahouse.eatool.gui.matrix;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.ExceptionDisplay;

/**
 * MatrixActionSet is an ActionSet for the MatrixViewer actions.
 * 
 * @author rbp28668
 * Created on 30-Jul-2005
 */
public class MatrixActionSet extends ActionSet {

    /** The MatrixViewer these actions will act upon.*/
    private MatrixViewer viewer;
    
	
    /**
     * Creates a new set of actions for a MatrixViewer.
     * @param viewer is the MatrixViewer these actions act upon.
     */
    public MatrixActionSet(MatrixViewer viewer) {
        super();
        this.viewer = viewer;
        
        addAction("MatrixSave", actionMatrixSave);
        addAction("MatrixClose", actionMatrixClose);
        addAction("MatrixAlphabetical",actionMatrixAlphabetical);
        addAction("MatrixDensity",actionMatrixDensity);
        addAction("MatrixSimilarity",actionMatrixSimilarity);
        addAction("RowAlphabetical",actionRowAlphabetical);
        addAction("RowDensity",actionRowDensity);
        addAction("RowSimilarity",actionRowSimilarity);
        addAction("ColumnAlphabetical",actionColumnAlphabetical);
        addAction("ColumnDensity",actionColumnDensity);
        addAction("ColumnSimilarity",actionColumnSimilarity);
    }

    private final Action actionMatrixSave = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    List faults = viewer.verify();
			    if(faults.size() == 0){
				   viewer.save();
				   viewer.close();
			    } else {
			        showFaultList(null, faults);
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};

    private final Action actionMatrixClose = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    if(viewer.isModified()){
			        if(JOptionPane.showConfirmDialog(null,"Relationships have been modified. Save?","EATool",
			                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					    List faults = viewer.verify();
					    if(faults.size() == 0){
						   viewer.save();
						   viewer.close();
					    } else {
					        showFaultList(null, faults);
					    }
			        } else {
						   viewer.close();
			        }
			    } else { // not modified.
					   viewer.close();
			    }
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionMatrixAlphabetical</code> that sorts the matrix
     * alphabetically.
     */
    private final Action actionMatrixAlphabetical = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortMatrix(new AlphabeticSort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionMatrixDensity</code> that sorts the matrix
     * by density.
     */
    private final Action actionMatrixDensity = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortMatrix(new DensitySort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionMatrixSimilarity</code> that sorts the matrix
     * by similarity.
     */
    private final Action actionMatrixSimilarity = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortMatrix(new SimilaritySort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionRowAlphabetical</code> that sorts just the rows
     * alphabetically.
     */
    private final Action actionRowAlphabetical = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortRows(new AlphabeticSort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionRowDensity</code> that sorts just the rows
     * by density.
     */
    private final Action actionRowDensity = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortRows(new DensitySort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
	/**
	 * Action <code>actionRowSimilarity</code> that sorts just the rows
	 * by similarity.
	 */
	private final Action actionRowSimilarity = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortRows(new SimilaritySort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionColumnAlphabetical</code> that sorts just the columns
     * alphabetically.
     */
    private final Action actionColumnAlphabetical = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortColumns(new AlphabeticSort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionColumnDensity</code> that sorts just the columns
     * by density.
     */
    private final Action actionColumnDensity = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortColumns(new DensitySort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
	
    /**
     * Action <code>actionColumnSimilarity</code> that sorts just the
     * columns by similarity.
     */
    private final Action actionColumnSimilarity = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			try {
			    viewer.sortColumns(new SimilaritySort());
			} catch(Throwable t) {
				new ExceptionDisplay(viewer,t);
			}
		}
	};
    
	private void showFaultList(Component parent, List faults){
	    JList list = new JList();
        list.setListData(faults.toArray());
        list.setEnabled(false); // no point selecting items.
        JScrollPane scroll = new JScrollPane(list);
	    JOptionPane.showMessageDialog(parent,scroll,"Invalid Matrix", JOptionPane.WARNING_MESSAGE);
	}
}
