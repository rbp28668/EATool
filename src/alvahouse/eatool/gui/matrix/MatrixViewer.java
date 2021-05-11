/*
 * Created on Jun 25, 2004
 *
 * MatrixViewer.java
 */
package alvahouse.eatool.gui.matrix;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.WindowCoordinator;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRelationshipRestriction;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;
import alvahouse.eatool.util.SettingsManager;

/**
 * @author rbp28668
 *
 * Viewer window for viewing matrices.
 */
public class MatrixViewer extends JInternalFrame  implements MatrixTableModelListener, ItemListener{

    private static final long serialVersionUID = 1L;


    private static class MatrixEntryRenderer extends JCheckBox implements TableCellRenderer{

        private static final long serialVersionUID = 1L;

        /* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Boolean state = (Boolean)value;
//			getModel().setPressed(state.booleanValue());
			getModel().setSelected(state.booleanValue());
			return this;
		}
	}

	/**
	 * MatrixEntryEditor allows editing of the grid.
	 * 
	 * @author rbp28668
	 */
	private static class MatrixEntryEditor extends JCheckBox implements  TableCellEditor {

        private static final long serialVersionUID = 1L;
        private transient List<CellEditorListener> listeners = new LinkedList<CellEditorListener>();
	    private transient boolean originalValue = false;
	    private int currentRow;
	    private int currentColumn;
	    
	    MatrixEntryEditor(ItemListener listener){
	        super();
	        super.addItemListener(listener);
	    }
	    
        /* (non-Javadoc)
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            currentColumn = column;
            
            if(value == null) {
                return this;
            } else {
                Boolean state = (Boolean)value;
                originalValue = state.booleanValue();
                getModel().setSelected(originalValue);
            }
            table.setRowSelectionInterval(row,row);
            table.setColumnSelectionInterval(column,column);
            return this;
            
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#getCellEditorValue()
         */
        public Object getCellEditorValue() {
            if(isSelected()){
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
         */
        public boolean isCellEditable(EventObject arg0) {
            return true;
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
         */
        public boolean shouldSelectCell(EventObject arg0) {
            return true;
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#stopCellEditing()
         */
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#cancelCellEditing()
         */
        public void cancelCellEditing() {
            fireEditingCancelled();
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
         */
        public void addCellEditorListener(CellEditorListener listener) {
            listeners.add(listener);
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
         */
        public void removeCellEditorListener(CellEditorListener listener) {
            listeners.remove(listener);
        }

        /**
         * Reverts the cell to its original value and alerts any listeners.
         */
        private void fireEditingCancelled() {
            setSelected(originalValue);
            ChangeEvent ce = new ChangeEvent(this);
            for(CellEditorListener listener : listeners){
                listener.editingCanceled(ce);
            }
        }


        /**
         * Alerts any listeners that editing has been stopped.
         */
        private void fireEditingStopped() {
            ChangeEvent ce = new ChangeEvent(this);
            List<CellEditorListener> copy = new LinkedList<CellEditorListener>(listeners);
            for(CellEditorListener listener : copy){
                listener.editingStopped(ce);
            }
        }
         
        /**
         * Get the row this editor is currently being used for.
         * @return the current row.
         */
        public int getCurrentRow(){
            return currentRow;
        }
        
        /**
         * Get the column this editor is currently being used for.
         * @return the current column.
         */
        public int getCurrentColumn() {
            return currentColumn;
        }
	}
	/**
	 * MatrixHeaderRenderer renders the header row of the matrix.
	 * 
	 * @author rbp28668
	 */
	private static class MatrixHeaderRenderer extends JLabel implements TableCellRenderer{

        private static final long serialVersionUID = 1L;
        private MatrixTableModel tableModel;
		private int height;
		
		MatrixHeaderRenderer(MatrixTableModel tableModel){
			assert(tableModel != null);
			this.tableModel = tableModel;

			int cols = tableModel.getColumnCount();
			height = 0;
			JLabel label = new JLabel();
			FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
			int offset = fontMetrics.charWidth('m');
			for(int i=0; i<cols; ++i){
				Entity entity = tableModel.getColumnEntity(i);
				String text = entity.toString();

				int h = offset + fontMetrics.stringWidth(text);
				if(h > height){
				    height = h;
				}
			}
			
			if(height > MAX_BORDER){
			    height = MAX_BORDER;
			}
		}
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Entity entity = tableModel.getColumnEntity(column);
			assert(entity != null);
			String text = entity.toString();
			setText(text);
			setToolTipText(text);
			setVerticalTextPosition(SwingConstants.BOTTOM);
			setHorizontalTextPosition(SwingConstants.LEFT);
			
			return this;
		}
		
		public Dimension getPreferredSize(){
			Dimension basic = super.getPreferredSize();
			return new Dimension(basic.height, height);
		}
		
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D)g;
			
//			AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI/2);
			g2d.rotate(-Math.PI/2);
			g2d.setColor(Color.black);

			g2d.translate(-getHeight(),getWidth());
			g2d.drawString(getText(),0,0);

			
		}
	}
	
	private static class MatrixTable extends JTable{
        private static final long serialVersionUID = 1L;

        MatrixTable(TableModel model, MatrixTableModel tableModel){
			super(model);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			MatrixEntryRenderer renderer = new MatrixEntryRenderer();
			Dimension size = renderer.getPreferredSize();
			TableColumnModel columnModel = getColumnModel();
			  
			for(int i=0; i<model.getColumnCount(); ++i){
				columnModel.getColumn(i).setPreferredWidth(size.width);
			}
			
			getTableHeader().setDefaultRenderer(new MatrixHeaderRenderer(tableModel));
		}
		
	}

	/**
	 * MatrixRowHeader is the component that implements the table header.
	 * This is responsible for painting all the rows
	 * @author rbp28668
	 */
	private static class MatrixRowHeader extends JComponent {
		
        private static final long serialVersionUID = 1L;
        //private Vector rowEntities;
	    private MatrixTableModel tableModel;
		private JTable table;
		private int width;
		
		MatrixRowHeader(JTable table, MatrixTableModel tableModel){
			assert(table != null);
			assert(tableModel != null);
			assert(table.getRowCount() == tableModel.getRowCount());
			
			this.table = table;
			this.tableModel = tableModel;
			setToolTipText("*");
			
			int rows = tableModel.getRowCount();
			width = 0;
			JLabel label = new JLabel();
			FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
			int offset = fontMetrics.charWidth('m');
			for(int i=0; i<rows; ++i){
				Entity entity = tableModel.getRowEntity(i);
				String text = entity.toString();

				int w = offset + fontMetrics.stringWidth(text);
				if(w > width){
				    width = w;
				}
			}
			
			if(width > MAX_BORDER){
			    width = MAX_BORDER;
			}
		}
		
		public Dimension getPreferredSize(){
			return new Dimension(width, tableModel.getRowCount() * table.getRowHeight());
		}
		
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D)g;
			
			int iy=0;
			for(int i=0; i<tableModel.getRowCount(); ++i){
				iy += table.getRowHeight(i);
				Entity entity = tableModel.getRowEntity(i);
				g2d.drawString(entity.toString(),0,iy);
			}
		}
		
		/* (non-Javadoc)
         * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
         */
        public String getToolTipText(MouseEvent evt) {
            int idx = evt.getY() / table.getRowHeight();
            String text = null;
            if(idx >= 0 && idx < tableModel.getRowCount()){
				Entity entity = tableModel.getRowEntity(idx);
	            text = entity.toString();
            }
            return text;
        }
		
		
		
	}
	
	private JScrollPane scrollPane;
	private MatrixTableModel tableModel;
	private MatrixActionSet actions = new MatrixActionSet(this);
	private Model model; // being edited.
	private MetaRelationship metaRelationship; // type of relationships in grid.
	private Application app;
	
	private static final String WINDOW_SETTINGS = "/Windows/MatrixViewer";
	private static final String MENU_CONFIG = "/MatrixViewer/menus";
	private static final int MAX_BORDER = 300;
	
	/**
	 * Constructor for the matrix viewer.  Private as this should only be
	 * created by the window factory in conjunction with the application's
	 * window coordinator.
	 * @param model
	 * @param metaRelationship
	 */
	private MatrixViewer(Model model, MetaRelationship metaRelationship, Application app) throws Exception{
		
		super("Matrix: " + metaRelationship.getName());

        this.model = model;
        this.metaRelationship = metaRelationship;
        this.app = app;

		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

		tableModel = buildTableModel(model,metaRelationship);

        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        JMenuBar menuBar = new JMenuBar();
        SettingsManager.Element cfg = app.getConfig().getElement(MENU_CONFIG);
        GUIBuilder.buildMenuBar(menuBar, actions, cfg);
        setJMenuBar(menuBar);

		
		JTable table = new MatrixTable(tableModel, tableModel);
		table.setDefaultRenderer(Boolean.class, new MatrixEntryRenderer());
		table.setDefaultEditor(Boolean.class, new MatrixEntryEditor(this));
		
		scrollPane = new JScrollPane(table);
		scrollPane.setRowHeaderView(new MatrixRowHeader(table,tableModel));
		
		MatrixImage image = new MatrixImage(tableModel);
		tableModel.addListener(image);
		tableModel.addListener(this);
		JLabel corner = new JLabel(image);
		scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,corner);
		getContentPane().add(scrollPane);
		
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
	    addInternalFrameListener(new InternalFrameAdapter() {
	        public void internalFrameClosing(InternalFrameEvent e) {
	                close();
	            }
	        });
	 		
		setVisible(true);
	}
	
	
	/**
	 * buildTableModel builds the tableModel from the model and the desired relationship
	 * @param model
     * @param metaRelationship
     * @return the new MatrixTableModel.
     */
    private MatrixTableModel buildTableModel(Model model, MetaRelationship metaRelationship) throws Exception{
		MetaEntity rowMeta = metaRelationship.start().connectsTo();
		MetaEntity colMeta = metaRelationship.finish().connectsTo();
		
		List<Entity> rowList = model.getEntitiesOfType(rowMeta);
		List<Entity> colList =  model.getEntitiesOfType(colMeta);
		
		MatrixTableModel tableModel = new MatrixTableModel(rowList, colList);
		
        for(int i=0; i<tableModel.getRowCount(); ++i){
			Entity e = tableModel.getRowEntity(i);
			
			Set<Relationship> relationships = e.getConnectedRelationshipsOf(metaRelationship);
			for(Relationship r : relationships){
				
				if( r.start().connectsTo() == e){
					tableModel.link(e,r.finish().connectsTo());
				}
			}
		}
        
        tableModel.setModified(false);
        return tableModel;
    }

    /** This updates the model from the matrix.  The model is compared to the matrix
     * and any relationships of the correct type that exist in the matrix, but not the
     * model are added to the model. Similarly, any relationships in the model which no
     * longer exist in the matrix are deleted.
     * @param model is the model to be updated.
     * @param metaRelationship is the type of relationship to be updated in the model.
     */
    public void updateModel(Model model, MetaRelationship metaRelationship) throws Exception{
        List<Relationship> relationshipsToAdd = new LinkedList<Relationship>();
        List<Relationship> relationshipsToDelete = new LinkedList<Relationship>();
        
        for(int row = 0; row < tableModel.getRowCount(); ++row){
            Entity rowEntity = tableModel.getRowEntity(row);
            Set<Relationship> relationships = rowEntity.getConnectedRelationshipsOf(metaRelationship);
            
            for(int col = 0; col < tableModel.getColumnCount(); ++col){
                Entity columnEntity = tableModel.getColumnEntity(col);
                
                boolean isLinked = tableModel.booleanValueAt(row,col);
                // if rowEntity isLinked to columnEntity
                if(isLinked){
                    // If linked in matrix, but there's no corresponding relationship
                    // in the model then need to create a new relationship, otherwise,
                    // leave alone.
                    Relationship r = find(relationships, rowEntity, columnEntity);
                    if(r == null) {
                        // then this row & column weren't linked before so note a new relationship
                        r = new Relationship(metaRelationship);
                        Role start = new Role(metaRelationship.start());
                        Role finish = new Role(metaRelationship.finish());
                        start.setConnection(rowEntity);
                        finish.setConnection(columnEntity);
                        r.setStart(start);
                        r.setFinish(finish);
                        relationshipsToAdd.add(r);
                    }
                } else {
                    // Not linked in matrix, but if linked in model then mark for deletion.
                    Relationship r = find(relationships, rowEntity, columnEntity);
                    if(r != null){
                        relationshipsToDelete.add(r);
                    }
                }
            }
        }
 
        // Add all the new relationships to the model.
        for(Relationship r : relationshipsToAdd){
            model.addRelationship(r);
        }
        
        // Now remove all relationships marked for deletion.
        for(Relationship r : relationshipsToDelete){
            r.start().disconnect();
            r.finish().disconnect();
            model.deleteRelationship(r.getKey());
         }
        
    }
    
    /**
     * Looks for a relationship in a set that links the first entity to the second.
     * @param relationships is the set of relationships to look in.
     * @param rowEntity is the first entity to match.
     * @param columnEntity is the second entity to match.
     * @return any matching relationship or null if none foun d.
     */
    private Relationship find(Set<Relationship> relationships, Entity rowEntity, Entity columnEntity) throws Exception{
        Relationship found = null;
        for(Relationship r  : relationships){
            if(r.start().connectsTo().equals(rowEntity) && r.finish().connectsTo().equals(columnEntity)){
                found = r;
                break;
            }
        }
        return found;
    }


    public static String getWindowName(MetaRelationship mr){
		return "Matrix:" + mr.getKey().toString();
	}
	
	/**
	 * sortMatrix sorts the matrix (both rows and columns) using
	 * the supplied sort.
	 * @param sort is the sort strategy to sort the matrix with.
	 */
	public void sortMatrix(MatrixSort sort){
	    
	    int[] rowOrder = sort.sortRows(tableModel);
	    int[] colOrder = sort.sortColumns(tableModel);
	    
	    tableModel.setRowOrder(rowOrder);
	    tableModel.setColumnOrder(colOrder);
	    
	    this.repaint();
	}

	/**
	 * sortRows sorts just the rows of the matrix using the supplied
	 * sort.
	 * @param sort is the sort strategy to sort the matrix with.
	 */
	public void sortRows(MatrixSort sort){
	    int[] rowOrder = sort.sortRows(tableModel);
	    tableModel.setRowOrder(rowOrder);
	    this.repaint();
	}

	/**
	 * sortColumns sorts just the columns of the matrix using the supplied
	 * sort.
	 * @param sort is the sort strategy to sort the matrix with.
	 */
	public void sortColumns(MatrixSort sort){
	    int[] colOrder = sort.sortColumns(tableModel);
	    tableModel.setColumnOrder(colOrder);
	    this.repaint();
	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.matrix.MatrixTableModelListener#columnOrderChanged()
     */
    public void columnOrderChanged() {
        // NOP
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.matrix.MatrixTableModelListener#rowOrderChanged()
     */
    public void rowOrderChanged() {
        // NOP
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.matrix.MatrixTableModelListener#valueChanged(int, int, boolean)
     */
    public void valueChanged(int rowIndex, int columnIndex, boolean linked) {
        scrollPane.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER).repaint();
    }


	
	/**
	 * DiagramViewerWindowFactory provides a WindowFactory for creating
	 * DiagramViewers in conjunction with the WindowCoordinator.
	 * @author Bruce.Porteous
	 *
	 */
	public static class WindowFactory implements WindowCoordinator.WindowFactory {

		private Model model;
		private MetaRelationship metaRelationship;
		private Application app;
		
		/**
		 * Creates a window factory for creating a window for the given
		 * model and meta-relationship.
		 * @param model is the model to create a viewer for.
		 * @param metaRelationship is the relationship to display.
		 */
		public WindowFactory(Model model, MetaRelationship metaRelationship, Application app) {
		    if(model == null) {
		        throw new NullPointerException("Can't create matrix for null model");
		    }
		    if(metaRelationship == null) {
		        throw new NullPointerException("Can't create matrix for null metaRelationship");
		    }
            if(app == null) {
                throw new NullPointerException("Can't create matrix for null application");
            }

		    this.model = model;
			this.metaRelationship = metaRelationship;
			this.app = app;
		}
		
		/* (non-Javadoc)
		 * @see alvahouse.eatool.gui.WindowCoordinator.WindowFactory#createFrame()
		 */
		public JInternalFrame createFrame() throws Exception {
			return new MatrixViewer(model,metaRelationship, app);
		}
    	
	}



    /**
     * Saves the contents of the viewer back into the model.
     */
    public void save() throws Exception {
        updateModel(model,metaRelationship);
    }

    /**
     * Verifies that the current matrix is valid.  Faults are returned in a list
     * of text.  An empty list indicates that the matrix is OK.  The validation 
     * consists of checking the cardinality constraints and also any restriction on
     * the MetaRelationship.
     * @return a list of String, empty if OK, never null.
     */
    public List<String> verify() {
        List<String> faults = new LinkedList<String>();

        int rowCount = tableModel.getRowCount();
        int[] rowCounts = new int[rowCount];
        for(int i = 0; i < rowCount; ++i){
            rowCounts[i] = 0;
        }
        
        int colCount = tableModel.getColumnCount();
        int[] colCounts = new int[colCount];
        for(int i=0; i < colCount; ++i){
            colCounts[i] = 0;
        }
        
        MetaRelationshipRestriction restriction = metaRelationship.getRestriction();
        MetaRelationshipRestriction.IntermediateState state = restriction.createState();
        
        for(int row = 0; row < rowCount; ++row){
            Entity rowEntity = tableModel.getRowEntity(row);
            
            for(int col = 0; col < colCount; ++col){
                Entity columnEntity = tableModel.getColumnEntity(col);
                
                boolean isLinked = tableModel.booleanValueAt(row,col);
                if(isLinked){
                    ++rowCounts[row];
                    ++colCounts[col];
                    if(!restriction.isValid(state, metaRelationship, rowEntity, columnEntity)){
                        String fault = "Restriction " + restriction.getName() + " forbids connecting " + rowEntity + " to " + columnEntity;
                        faults.add(fault);
                    }
                }
            }
        }

        faults.addAll(restriction.finishValidation(state, metaRelationship));
        
        Multiplicity start = metaRelationship.start().getMultiplicity();
        Multiplicity finish = metaRelationship.finish().getMultiplicity();

        for(int i = 0; i < rowCount; ++i){
            if(!finish.isValid(rowCounts[i])){
                Entity rowEntity = tableModel.getRowEntity(i);
                String fault = "Row " + rowEntity + " can only be connected to " + finish + " but is connected to " + rowCounts[i];
                faults.add(fault);
            }
        }
        
        for(int i=0; i < colCount; ++i){
            if(!start.isValid(colCounts[i])){
                Entity colEntity = tableModel.getColumnEntity(i);
                String fault = "Column " + colEntity + " can only be connected to " + start + " but is connected to " + colCounts[i];
                faults.add(fault);
            }
        }
        
        return faults;
    }
    
    /**
     * Closes the viewer.
     */
    public void close() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);

        dispose();
		WindowCoordinator wc = app.getWindowCoordinator();
		wc.removeFrame(MatrixViewer.getWindowName(metaRelationship));
    }


    /**
     * Determine whether the uesr has modified the model.
     * @return
     */
    public boolean isModified() {
        return tableModel.isModified();
    }


    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent event) {
        MatrixEntryEditor item = (MatrixEntryEditor)event.getItem();
        tableModel.setValueAt(item.isSelected(), item.getCurrentRow(), item.getCurrentColumn());
    }

}
