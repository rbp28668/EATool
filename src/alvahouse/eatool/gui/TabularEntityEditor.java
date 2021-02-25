/*
 * TabularEntityEditor.java
 * Project: EATool
 * Created on 04-Apr-2006
 *
 */
package alvahouse.eatool.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import alvahouse.eatool.Application;
import alvahouse.eatool.Main;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.types.MetaPropertyType;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Property;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.SettingsManager.Element;

/**
 * TabularEntityEditor allows editing of a set of entities correspondinging to
 * a single type as a grid - somewhat like a spreadsheet grid.
 * 
 * @author rbp28668
 */
public class TabularEntityEditor extends JInternalFrame {

    private static final long serialVersionUID = 1L;
    private final static String WINDOW_SETTINGS = "/Windows/TabularEntityEditor";
    private javax.swing.JScrollPane scrollPane;
    private TabularEntityTable table;
    private TabularEntityTableModel tableModel;
    private Application app;

    /**
     * 
     */
    public TabularEntityEditor(Model model, MetaEntity meta, Application app) {
        super();
        this.app = app;
        
        setTitle("Edit " + meta.getName());
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setClosable(true);
        
        GUIBuilder.loadBounds(this,WINDOW_SETTINGS,app);

        SettingsManager settings = app.getConfig();
        Element cfg = settings.getElement("/TabularEntityEditor/menus");
        JMenuBar menuBar = new JMenuBar();
        ActionSet actions = new TabularEntityEditorActionSet(this);
        GUIBuilder.buildMenuBar(menuBar, actions, cfg);
        setJMenuBar(menuBar);

        tableModel = new TabularEntityTableModel(model,meta);
        TableSorter sorter = new TableSorter(tableModel);
        table = new TabularEntityTable(sorter,meta);
        sorter.setTableHeader(table.getTableHeader());
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setViewportView(table);
        
        TableRowHeader rowHeader = new TableRowHeader(table,tableModel);
        table.getSelectionModel().addListSelectionListener(rowHeader);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		scrollPane.setRowHeaderView(rowHeader);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        
    }

    /**
     * @param count
     */
    public void appendNew(int count){
        tableModel.appendEntities(count);
    }

    public void insertBefore(){
        table.cancelEdit();
        ListSelectionModel selection = table.getSelectionModel();
        int min = selection.getMinSelectionIndex();
        int max = selection.getMaxSelectionIndex();
        if(min != -1 && max != -1){
            tableModel.insertEntities(min, max - min + 1);
        }
        
    }
    
    public void insertAfter(){
        table.cancelEdit();
        ListSelectionModel selection = table.getSelectionModel();
        int min = selection.getMinSelectionIndex();
        int max = selection.getMaxSelectionIndex();
        if(min != -1 && max != -1){
            tableModel.insertEntities(max+1, max - min + 1);
        }
        
    }
    
    public void deleteSelected(){
        table.cancelEdit();
        ListSelectionModel selection = table.getSelectionModel();
        int min = selection.getMinSelectionIndex();
        int max = selection.getMaxSelectionIndex();
        if(min != -1 && max != -1){
            tableModel.deleteRange(min,max);
        }
    }
    
    public boolean hasSelection(){
        ListSelectionModel selection = table.getSelectionModel();
        return !selection.isSelectionEmpty();
    }
    /**
     * Updates the underlying model to include the changes made in the UI.
     */
    public void updateModel() throws Exception{
        tableModel.updateModel();
    }
    
    public void dispose() {
        GUIBuilder.saveBounds(this,WINDOW_SETTINGS,app);
        app.getWindowCoordinator().removeFrame(this);
        super.dispose();
    }

 
    /**
     * TabularEntityTable
     * 
     * @author rbp28668
     */
    private class TabularEntityTable extends JTable {
        
        private Vector columnTypes;
        
        TabularEntityTable(TableModel tableModel, MetaEntity me){
            super(tableModel);
            
            setDefaultRenderer(Property.class, new PropertyCellRenderer());
            setDefaultEditor(Property.class, new PropertyCellEditor());
            
            int idx = 0;
            Collection metaProperties = me.getMetaProperties();
            columnTypes = new Vector(metaProperties.size());
            for(Iterator iter = me.getMetaProperties().iterator(); iter.hasNext();){
                MetaProperty mp = (MetaProperty)iter.next();
                MetaPropertyType type = mp.getMetaPropertyType();
                columnTypes.add(idx++, type);
            }
            setRowSelectionAllowed(true);
        }
        
        MetaPropertyType getTypeForColumn(int column){
            column = convertColumnIndexToModel(column);
            return (MetaPropertyType)columnTypes.get(column);
        }
        
        //ement table header tool tips.
        protected JTableHeader createDefaultTableHeader() {
            return new TabularEntityTableHeader(columnModel);
        }
    
        void cancelEdit(){
            cellEditor.cancelCellEditing();
        }

    }
 
    /**
     * PropertyCellRenderer gets components to display property values.
     * 
     * @author rbp28668
     */
    private class PropertyCellRenderer extends DefaultTableCellRenderer{

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            MetaPropertyType type = ((TabularEntityTable)table).getTypeForColumn(column);
            Component renderer = type.getRenderer(value); // table editable so user editors for all cells.
            return renderer;

         }
    }
    
    /**
     * PropertyCellEditor uses the meta-property type for the column to
     * set up an appropriate editor. 
     * 
     * @author rbp28668
     */
    private class PropertyCellEditor implements TableCellEditor{

        private transient List listeners = new LinkedList();
        private transient String originalValue;
        private transient Component editor;
        private transient MetaPropertyType type;
        
        /* (non-Javadoc)
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            type = ((TabularEntityTable)table).getTypeForColumn(column);
            this.originalValue = (String)value;
            editor = type.getEditor(value);
            return editor;
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#getCellEditorValue()
         */
        public Object getCellEditorValue() {
            Object value = type.getEditValue(editor); 
            return value;
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
        
        private void fireEditingStopped(){
            ChangeEvent ce = new ChangeEvent(this);
            CellEditorListener[] copy = (CellEditorListener[])listeners.toArray(new CellEditorListener[listeners.size()]);
            for(int i=0; i<copy.length; ++i){
                copy[i].editingStopped(ce);
            }
        }
        
        private void fireEditingCancelled(){
            //value = new String(originalValue);
            ChangeEvent ce = new ChangeEvent(this);
            CellEditorListener[] copy = (CellEditorListener[])listeners.toArray(new CellEditorListener[listeners.size()]);
            for(int i=0; i<copy.length; ++i){
                copy[i].editingCanceled(ce);
            }
        }
        
    }
    /**
     * TabularEntityTableHeader
     * 
     * @author rbp28668
     */
    private class TabularEntityTableHeader extends JTableHeader {
        
        public TabularEntityTableHeader(){
            super();
        }
        
        public TabularEntityTableHeader(TableColumnModel columnModel){
            super(columnModel);
        }
        
        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            //TabularEntityTableModel model = (TabularEntityTableModel)dataModel;
            MetaProperty mp = tableModel.getMetaForColumn(realIndex);
            String tooltip = mp.getDescription();
            if(tooltip.trim().length() == 0) {
                tooltip = null;
            }
            return tooltip;
        }
    }
    
    /**
     * TabularEntityTableModel provides the underlying table representation
     * if a set of entities.
     * 
     * @author rbp28668
     */
    private class TabularEntityTableModel extends AbstractTableModel{

        private Vector metaProperties;
        private Vector rowState;
        private MetaEntity metaEntity;
        private Model model;
        private List deleted = new LinkedList();
        
        /**
         * @param model
         * @param metaEntity
         */
        TabularEntityTableModel(Model model, MetaEntity metaEntity){
            this.model = model;
            this.metaEntity = metaEntity;
            
            List entityList = model.getEntitiesOfType(metaEntity);
            metaProperties = new Vector(metaEntity.getMetaProperties());
            rowState = new Vector(entityList.size());
            Iterator iter = entityList.iterator();
            for(int i=0; i<entityList.size(); ++i){
                Entity e = (Entity)iter.next();
                rowState.add(i,new RowState(e,metaProperties));
            }
        }


        /**
         * Updates the underlying model from the stored states.
         */
        public void updateModel() throws Exception {
            for(int i=0; i<rowState.size(); ++i){
                RowState row = (RowState)rowState.get(i);
                if(row.isEdited()){
                    Entity e;
                    if(row.isNew()){
                        e = new Entity(metaEntity);
                        row.updateEntity(e);
                        model.addEntity(e);
                    } else {
                        row.updateEntity(row.getEntity());
                    }
                }
            }
            
            // Now handle any deleted rows...
            for(Iterator iter = deleted.iterator(); iter.hasNext();){
                RowState row = (RowState)iter.next();
                Entity e = row.getEntity();
                if(e != null){
                    model.deleteEntity(e.getKey());
                }
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return rowState.size();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return metaProperties.size();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnClass(int)
         * This allows the table to pick an appropriate editor/renderer
         * for each property type.
         */
        public Class getColumnClass(int columnIndex) {
            return Property.class;
        }
        
         /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            RowState row =((RowState)rowState.get(rowIndex));
            return row.getValue(columnIndex);
        }
        
        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                RowState row =((RowState)rowState.get(rowIndex));
                row.setValue(columnIndex,aValue);
            } catch (RuntimeException e) {
                // NOP.
            }
        }
        
        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int columnIndex) {
            MetaProperty mp = (MetaProperty) metaProperties.get(columnIndex);
            return mp.getName();
        }
        
        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int rowIndex, int columnIndex){
            MetaProperty mp = (MetaProperty) metaProperties.get(columnIndex);
            return !mp.isReadOnly();
        }
        
        /**
         * Gets the MetaProperty for a given column.
         * @param columnIndex is the desired column.
         * @return the MetaProperty that corresponds to columnIndex.
         */
        MetaProperty getMetaForColumn(int columnIndex){
            return (MetaProperty) metaProperties.get(columnIndex);
        }
        
        /**
         * Appends a number of new entities at the end of the table.
         * @param count is the number of entities to add.
         */
        void appendEntities(int count){
            if(count <= 0){
                throw new IllegalArgumentException("Can't add 0 or fewer entities to table");
            }
            int originalSize = rowState.size();
            rowState.ensureCapacity(originalSize + count);
            for(int i=0; i<count; ++i){
                rowState.add(new RowState(metaProperties)); // new and not edited.
            }
            
            fireTableRowsInserted(originalSize, originalSize + count -1);
        }
        
        /**
         * Inserts a number of new entities at the end of the table.
         * @param count is the number of entities to add.
         */
        void insertEntities(int position, int count){
            if(count <= 0){
                throw new IllegalArgumentException("Can't add 0 or fewer entities to table");
            }
            rowState.ensureCapacity(rowState.size() + count);
            for(int i=0; i<count; ++i){
                rowState.add(position + i, new RowState(metaProperties)); // new and not edited.
            }
            fireTableRowsInserted(position, position + count -1);
        }
        
        /**
         * Deletes entries from the table.
         * @param min is the minimum inclusive index of the things to be removed.
         * @param max is the maximum inclusive index of the things to be removed.
         */
        public void deleteRange(int min, int max) {
            for(int i=min; i<=max; ++i){
                Object removed = rowState.remove(min); // note - min, not i as index!
                deleted.add(removed);
            }
            fireTableRowsDeleted(min,max);
        }


    }
    
	/**
	 * TableRowHeader is the component that implements the table header.
	 * This is responsible for painting all the rows
	 * @author rbp28668
	 */
	private static class TableRowHeader extends JComponent implements ListSelectionListener {
		
 		//private Vector rowEntities;
	    private TabularEntityTableModel tableModel;
		private JTable table;
		private int width;
		
		TableRowHeader(JTable table, TabularEntityTableModel tableModel){
			assert(table != null);
			assert(tableModel != null);
			assert(table.getRowCount() == tableModel.getRowCount());
			
			this.table = table;
			this.tableModel = tableModel;
			
			int rows = tableModel.getRowCount();
			width = 0;
			JLabel label = new JLabel();
			FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
			width = 2 * fontMetrics.charWidth('m');
		}
		
		public Dimension getPreferredSize(){
			return new Dimension(width, tableModel.getRowCount() * table.getRowHeight());
		}
		
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D)g;
			int iy=0;
			
			for(int i=0; i<tableModel.getRowCount(); ++i){
				int height = table.getRowHeight(i);
				
				if(table.isRowSelected(i)){
				    g2d.setBackground(table.getSelectionBackground());
				} else {
				    g2d.setBackground(table.getBackground());
				}
				g2d.clearRect(0,iy,width,height);
				
				g2d.setColor(table.getGridColor());
				g2d.drawRect(0,iy,width-1,height-1);
				
				
				iy += height;
			}
		}

        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent arg0) {
            repaint();
        }
		
	}

    /**
     * RowState is used to track the state (new and/or edited) of a row in the table.
     * 
     * @author rbp28668
     */
    class RowState {
        boolean isNew = false;
        boolean isEdited = false;
        Vector values = null;
        Vector metaProperties = null;
        Entity entity = null;
        

        RowState(Vector metaProperties){
            this.metaProperties = metaProperties;
            this.isNew = true;
            this.isEdited = false;
            values = new Vector(metaProperties.size());
            for(int i=0; i<metaProperties.size(); ++i){
                MetaProperty mp = (MetaProperty)metaProperties.get(i);
                values.add(i,mp.getDefaultValue());
            }
        }
        
        RowState(Entity entity, Vector metaProperties){
            this.entity = entity;
            this.metaProperties = metaProperties;
            this.isNew = false;
            this.isEdited = false;
        }
        
        Object getValue(int column){
            if(values != null){
                return values.get(column);
            } else {
                MetaProperty mp = (MetaProperty)metaProperties.get(column);
                return entity.getPropertyByMeta(mp.getKey()).getValue();
            }
        }
        
        void setValue(int column, Object obj){
            if(values == null){
                
                // Initialise a copy of the data for the underlying entity.
                values = new Vector(metaProperties.size());
                for(int i=0; i<metaProperties.size(); ++i){
                    MetaProperty mp = (MetaProperty)metaProperties.get(i);
                    Property p = entity.getPropertyByMeta(mp.getKey());
                    values.add(i,p.getValue());
                 }
            }
            MetaProperty mp = (MetaProperty)metaProperties.get(column);
            mp.getMetaPropertyType().validate((String)obj);
            values.set(column,obj);
            isEdited = true;
        }
        
        public boolean isEdited() {
            return isEdited;
        }
        
        public boolean isNew() {
            return isNew;
        }
            /**
         * @return Returns the entity.
         */
        public Entity getEntity() {
            return entity;
        }
        
        /**
         * Updates the underlying entity from the stored values.
         * @param e
         */
        public void updateEntity(Entity e) {
            for(int i=0; i<values.size(); ++i){
                MetaProperty mp = (MetaProperty)metaProperties.get(i);
                Property p = e.getPropertyByMeta(mp.getKey());
                p.setValue((String) values.get(i));
            }
        }
        
}
    
    /**
     * WindowFactory
     * 
     * @author rbp28668
     */
    public static class WindowFactory implements WindowCoordinator.WindowFactory {

        private Model model;
        private MetaEntity metaEntity;
        private Application app;
        
        /**
         * 
         */
        public WindowFactory(Model model, MetaEntity metaEntity, Application app) {
            super();
            this.model = model;
            this.metaEntity = metaEntity;
            this.app = app;
        }

        /* (non-Javadoc)
         * @see alvahouse.eatool.gui.WindowCoordinator.WindowFactory#createFrame()
         */
        public JInternalFrame createFrame() {
            return new TabularEntityEditor(model,metaEntity,app);
        }

    }

    /**
     * @param meta
     * @return
     */
    public static String getWindowName(MetaEntity meta) {
        return "Table Edit " + meta.getName();
    }

}
