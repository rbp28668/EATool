/*
 * EventMapPanel.java
 * Project: EATool
 * Created on 21-Mar-2006
 *
 */
package alvahouse.eatool.gui.scripting;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import alvahouse.eatool.repository.scripting.EventMap;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.Scripts;
import alvahouse.eatool.util.UUID;

/**
 * EventMapPanel
 * 
 * @author rbp28668
 */
public class EventMapPanel extends JPanel implements ItemListener {

 	private static final long serialVersionUID = 1L;
	private EventMap eventMap;
    private JTable table;
    private static final Script NONE = new Script(UUID.NULL); 

    /**
     * 
     */
    public EventMapPanel(EventMap eventMap, Scripts scripts) throws Exception {
        super();
        this.eventMap = eventMap;
        NONE.setName("--NONE--");
        table = new JTable( new EventMapTableModel(scripts));
        table.setDefaultEditor(Script.class, new HandlerCellEditor(scripts,this));
        JScrollPane scroll = new JScrollPane(table);
        add(scroll,BorderLayout.CENTER);
    }

    /**
     * updates the EventMap from the table.
     */
    public void onOK() {
        TableModel model = table.getModel();
        int rows = model.getRowCount();
        for(int i=0; i<rows; ++i){
            String event = (String)model.getValueAt(i,0);
            Script handler = (Script)model.getValueAt(i,1);
            if(handler == null){
                eventMap.removeHandler(event);
            } else {
                eventMap.setHandler(event,handler);
            }
        }
        
    }

    /**
     * @return
     */
    public boolean validateInput() {
        return true;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent event) {
        System.out.println(event.getItem().getClass().getName());
        if(event.getItem() instanceof HandlerCellEditor){
	        HandlerCellEditor item = (HandlerCellEditor)event.getItem();
	        if(table.isCellEditable(item.getCurrentRow(), item.getCurrentColumn())){
	            table.getModel().setValueAt(item.getCellEditorValue(), item.getCurrentRow(), item.getCurrentColumn());
	        }
        }
    }

    private class EventMapTableModel extends AbstractTableModel{

 		private static final long serialVersionUID = 1L;
		private final String[] headers = {"Event", "Handler"};
        private  String[] events;
        private Script[] handlers; 

        EventMapTableModel(Scripts scripts) throws Exception{
            int count = eventMap.getEventCount();
            events = new String[count];
            handlers = new Script[count];
            
            int idx = 0;
            for(String event : eventMap.getEvents()){
                events[idx] = event;
                handlers[idx] = eventMap.hasHandler(event) ? null : eventMap.get(event, scripts);
                ++idx;
            }
        }
        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return eventMap.getEventCount();
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return headers.length;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt( int row, int col ) {
            if(col == 0){
                return events[row];
            } else if (col == 1){
                return handlers[row];
            }
            return null;
        }

		/**
		 * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
		 */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if(columnIndex != 1)
                throw new IllegalArgumentException("Table column not editable");
            
            if(aValue == NONE){
                aValue = null;
            }
            
            handlers[rowIndex] = (Script)aValue;
        }
        
		/**
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
        public String getColumnName(int c) {
            return headers[c];
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        public Class<?> getColumnClass(int column) {
            if(column == 0){
                return String.class;
            } else if (column == 1){
                return Script.class;
            }
            
            return super.getColumnClass(column);
        }
       
		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }
        
        
    }

    public static class HandlerCellEditor extends AbstractCellEditor implements TableCellEditor {

 		private static final long serialVersionUID = 1L;
		private JComboBox<Script> combo;
        private int currentRow;
        private int currentColumn;
        private ItemListener listener;
        
        HandlerCellEditor(Scripts scripts, ItemListener listener) throws Exception{
            this.listener = listener;
            
            combo = new JComboBox<Script>();
            combo.addItem(NONE);

            // Do a bit of translation of the ItemEvents so they end up
            // coming from the HandlerCellEditor rather than the combo box.
            combo.addItemListener(new ItemListener(){
               public void itemStateChanged(ItemEvent arg0) {
                    ItemEvent event = new ItemEvent(arg0.getItemSelectable(), arg0.getID(),HandlerCellEditor.this,arg0.getStateChange());
                    HandlerCellEditor.this.listener.itemStateChanged(event);
                }
                
            });
            
            for(Script script : scripts.getScripts()){
                combo.addItem(script);
            }
        }

        public int getCurrentColumn() {
            return currentColumn;
        }
        public int getCurrentRow() {
            return currentRow;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value == null){
                combo.setSelectedItem(NONE);
            } else {
                combo.setSelectedItem(value);
            }
            currentRow = row;
            currentColumn = column;
            return combo;
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#getCellEditorValue()
         */
        public Object getCellEditorValue() {
            Object item = combo.getSelectedItem();
            if(item == NONE){
                item = null;
            }
            return item;
        }

        
    }

}
