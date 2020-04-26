/**
 * 
 */
package alvahouse.eatool.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


/**
 *  If adding buttons to a table make sure they're visible as buttons using this
 *  class. e.g. 
 * 		ButtonEditor buttonEditor = new ButtonEditor();
 *		table.setDefaultRenderer(JButton.class, buttonEditor);
 *		table.setDefaultEditor(JButton.class, buttonEditor);
 *
 * @author bruce_porteous
 *
 */
public class ButtonEditor implements TableCellEditor,TableCellRenderer {


	/**
	 * 
	 */
	ButtonEditor() {
	}

	private JButton theButton;

	public Component getTableCellRendererComponent(
		     JTable table, Object value,
		     boolean isSelected, boolean hasFocus,
		     int row, int column) {
			
			JButton button = (JButton)value;
			return button;
		}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return theButton;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing() {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	@Override
	public void cancelCellEditing() {
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void addCellEditorListener(CellEditorListener l) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void removeCellEditorListener(CellEditorListener l) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
		theButton = (JButton)value;
		return theButton;
	}
	
}