 /*
 * StandardDiagramTypeEditor.java
 * Created on 15-Nov-2003
 * By bruce.porteous
 *
 */
package alvahouse.eatool.gui.graphical.standard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import alvahouse.eatool.gui.BasicDialog;
import alvahouse.eatool.gui.DiagramTypeEditor;
import alvahouse.eatool.gui.ExceptionDisplay;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramTypes;
import alvahouse.eatool.repository.graphical.standard.ConnectorType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.util.InvertedMap;
import alvahouse.eatool.util.UUID;

/**
 * StandardDiagramTypeEditor allows the user to edit a diagram type.
 * This includes selecting allowable entities, symbols, connectors etc.
 * This is a fairly complex editor - when symbols are selected then the
 * list of possible relationships changes.  The allowable types of symbols
 * and connectors are supplied by the DiagramTypes.AllowableElements class.
 * @author bruce.porteous
 *
 */
public class StandardDiagramTypeEditor extends BasicDialog implements DiagramTypeEditor {

    private static final long serialVersionUID = 1L;
    private DiagramTypes types;
	private MainPanel mainPanel;
	private StandardDiagramType dtOriginal;
	private StandardDiagramType dtEdit;
	private MetaModel metaModel;
	private SymbolsModel symbolsModel;
	private ConnectorsModel connectorsModel;
	private JTextField nameField;
	private StandardDiagramTypeEditor thisDialog = this;
	
	public StandardDiagramTypeEditor(Component parent){
	    super(parent, "Standard Diagram Type Editor");
	}
	
	public void init(DiagramType type, MetaModel metaModel, DiagramTypes types) 
	throws Exception{

		this.types = types;
		this.metaModel = metaModel;
		
		dtEdit = (StandardDiagramType)type;
		
		setResizable(true);
		getContentPane().add(getOKCancelPanel(),BorderLayout.EAST);

		mainPanel = new MainPanel();
		mainPanel.initFromDiagramType(dtEdit);
		
		getContentPane().add(mainPanel,BorderLayout.CENTER);
        
		pack();
		
		
	}
	
	
	public void dispose() {
		super.dispose();
	}


	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#onOK()
	 */
	@Override
	protected void onOK() {
		// Need to end up with 2 lists:
		// SymbolType & ConnectorType for StandardDiagramType.
		// Each SymbolType ties a MetaEntity to a Symbol Class.
		// Each ConnectorType ties a MetaRelationship to a Connector class.
		
		// Update the symbols.

	    dtEdit.clearSymbolTypes();
	    dtEdit.clearConnectorTypes();
	    
		try {
			AllowableElements allowable = AllowableElements.getAllowable();
			
			List symbolTypes = symbolsModel.getSymbolTypes();
			for(Iterator iter = symbolTypes.iterator(); iter.hasNext(); ){
				SymbolType st = (SymbolType)iter.next();
				dtEdit.add(st);
			}
//			Map symbols = symbolsModel.getSelectedItemMap();
//			Map allowableSymbols = allowable.getAllowableSymbols();
//			for(Iterator iter = symbols.keySet().iterator(); iter.hasNext();){
//				MetaEntity me = (MetaEntity)iter.next();
//				String symbolName = (String)symbols.get(me);
//				Class symbolClass = (Class)allowableSymbols.get(symbolName);
//				SymbolType st = new SymbolType(me,symbolClass,null);
//				dtNew.add(st);
//			}
			
			Map connectors = connectorsModel.getSelectedItemMap();
			Map allowableConnectors = allowable.getAllowableConnectors();
			for(Iterator iter = connectors.keySet().iterator(); iter.hasNext();){
				MetaRelationship mr = (MetaRelationship)iter.next();
				String connectorName = (String)connectors.get(mr);
				Class connectorClass = (Class)allowableConnectors.get(connectorName);
				ConnectorType ct = new ConnectorType(new UUID(),mr, connectorClass, null);
				dtEdit.add(ct);
			}
			dtEdit.setName(nameField.getText());
			
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unable to update diagram type");
		}
		
	}


	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.BasicDialog#validateInput()
	 */
	protected boolean validateInput() {
		// To be valid, we need at least 1 meta class and
		// 1 meta relationship defined.  (open for debate - could
		// argue that only 1 class is needed).
		// Also there must be a valid name.
		return symbolsModel.getSelectedMetaEntities().size() > 0
		&& nameField.getText().length() > 0;
	}
	
	/**
	 * MainPanel
	 * Provides the main editing panel for the diagram
	 * type editor.
	 *
	 */
	private class MainPanel extends JPanel{
		
		MainPanel() throws Exception {
			setLayout(new BorderLayout());
			
			Box strip = Box.createHorizontalBox();
			strip.add(new JLabel("Name"));
			strip.add(Box.createVerticalStrut(5));
			strip.add(nameField = new JTextField());
			
			getContentPane().add(strip,BorderLayout.NORTH);
			
			JTable tableSymbols = null;
			JTable tableConnectors = null;			
			try{
				AllowableElements allowable = AllowableElements.getAllowable();
				symbolsModel = new SymbolsModel(allowable);
				tableSymbols = new JTable(symbolsModel);
			
				tableSymbols.setDefaultRenderer(
					ItemTag.class,
					new ItemTagRenderer());
					
				tableSymbols.setDefaultEditor(
					ItemTag.class,
					new DefaultCellEditor(
						new AllowableCombo(allowable.getAllowableSymbols())));
						
				tableSymbols.setDefaultRenderer(
					JButton.class,
					new ButtonRenderer());
			
				tableSymbols.setDefaultEditor(
					JButton.class,
					new ButtonEditor());
				
				connectorsModel = new ConnectorsModel();
				tableConnectors = new JTable(connectorsModel);
				
				tableConnectors.setDefaultRenderer(
					ItemTag.class,
					new ItemTagRenderer());
					
				tableConnectors.setDefaultEditor(
					ItemTag.class,
					new DefaultCellEditor(
						new AllowableCombo(
							allowable.getAllowableConnectors())));
							
				// Do some messing around with sizes for a more pleasing display.							
				JButton btn = new JButton("Edit");
				Dimension size = btn.getPreferredSize();
				int editWidth = 3*size.width/2;
				tableSymbols.getColumnModel().getColumn(2).setMaxWidth(editWidth);
				tableSymbols.getColumnModel().getColumn(2).setMinWidth(editWidth);
			
				
			} catch (ClassNotFoundException cnfe){
				new ExceptionDisplay( thisDialog, cnfe);	
			}

			// Set relative sizes of scroll panes
			JScrollPane scrollSymbols = new JScrollPane(tableSymbols);
			JScrollPane scrollConnectors = new JScrollPane(tableConnectors);
			scrollSymbols.setPreferredSize(new Dimension(500,300));
			scrollConnectors.setPreferredSize(new Dimension(500,200));
			
			add(new JSplitPane (
				JSplitPane.VERTICAL_SPLIT,
				true,
				scrollSymbols,
				scrollConnectors
				),BorderLayout.CENTER);
			
			// Add a listener to the entity table so that if the entities
			// have associated relationships, we update the relationship
			// table to include those.  Hence we can only select connectors
			// for relationships that are valid in the context of the
			// selected entities & their symbols.
			symbolsModel.addTableModelListener( new TableModelListener() {

				public void tableChanged(TableModelEvent e) {
					System.out.println("Table Changed at " + e.getColumn() + "," + e.getFirstRow());
					if(e.getColumn() == 1) {
						try {
							updateValidConnectors();
						} catch (Exception x) {
							Container c = MainPanel.this;
							while(c != null){
								if(c instanceof java.awt.Frame) {
									java.awt.Frame frame = (java.awt.Frame)c;
									new ExceptionDisplay(frame, x);
									break;
								}
								c = c.getParent();
							}
						}
					}
				}
			});
			
		}
		
		/**
		 * Initialises the main panel from the contents of the given
		 * diagram type.
		 * @param diagramType is the diagram type to use for 
		 * initialisation.
		 */
		public void initFromDiagramType(StandardDiagramType diagramType)
		throws Exception{
			nameField.setText(diagramType.getName());
			symbolsModel.initFromDiagramType(diagramType);
			updateValidConnectors();
			connectorsModel.initFromDiagramType(diagramType);
		}
		
		/**
		 * This takes the set of selected meta entities and sets the allowable meta-relationships in
		 * the connectors model to those meta-relationships that join the selected meta-entities.
		 */
		private void updateValidConnectors() throws Exception{
			Set selected = symbolsModel.getSelectedMetaEntities();
			connectorsModel.resetRelationships();
			Iterator iter = metaModel.getMetaRelationships().iterator();
			while(iter.hasNext()){
				MetaRelationship r = (MetaRelationship)iter.next();
				if(selected.contains(r.start().connectsTo()) 
				&& selected.contains(r.finish().connectsTo())){
					System.out.println("Valid relationship " + r.getName());
					connectorsModel.validRelationship(r);
				}
			}
			connectorsModel.pruneRelationships();
		}

	}
	
	
	/**
	 * SymbolsModel
	 * Provides a TableModel for selecting symbol types for
	 * the set of meta-entities.
	 *
	 */
	private class SymbolsModel extends AbstractTableModel{

		private AllowableElements allowable = null;
		
		private MetaEntity[] metaEntities = null;
		private ItemTag[] symbolTags = null;
		private SymbolType[] symbolTypes = null;
		private JButton[] buttons = null;
		private Set selectedMetaEntities = new HashSet();
				
		private String[] headers = {"Entity Type", "Symbol", "Configure"};
		private Class[] columnClasses = {String.class, ItemTag.class, JButton.class};
		
		public SymbolsModel(AllowableElements allowable)  throws Exception{
		    Collection<MetaEntity> mes = metaModel.getMetaEntities();
			metaEntities = mes.toArray(new MetaEntity[mes.size()]);
			symbolTags = new ItemTag[metaEntities.length];
			symbolTypes = new SymbolType[metaEntities.length];
			buttons = new JButton[metaEntities.length];
			

			for(int i=0; i<metaEntities.length; ++i){
				symbolTags[i] = null;	// currently unassigned.
				symbolTypes[i] = null;
				buttons[i] = null;
			}
			this.allowable = allowable;
			
		}
		
		/**
		 * Initialises the symbols table model from the diagram type.
		 * @param diagramType is the diagram type to initialise from.
		 * @throws ClassNotFoundException - if one of the allowable
		 * types for the diagram can't be instantiated.
		 */
		public void initFromDiagramType(StandardDiagramType diagramType)
		throws ClassNotFoundException {
			
			AllowableElements allowable = AllowableElements.getAllowable();
			// allowable should be a one to one mapping of friendly name to
			// a Class of Symbol or it's subtypes. Will need to do inverse mapping
			// to set up the ItemTags
			
			Map nameLookup = new InvertedMap(allowable.getAllowableSymbols());
			for(int i=0; i<metaEntities.length; ++i){
				if(diagramType.hasSymbolTypeFor(metaEntities[i])){
					SymbolType st = diagramType.getSymbolTypeFor(metaEntities[i]);
					String name = (String)nameLookup.get(st.getRenderClass());
					symbolTags[i] = new ItemTag(name);	// currently unassigned.
					symbolTypes[i] = st;
					addButton(i,st);
					selectedMetaEntities.add(metaEntities[i]);
				}
			}
		}
		
		/**
		 * As well as the standard table model, this also tracks which meta-entities have
		 * symbols assigned to them.  These are the only ones which can have relationships.
		 * (i.e. for a relationship to appear on the diagram, both ends must have symbols
		 * assingned).
		 * @return a set of MetaEntity.  May be empty.  Never null.
		 */
		public Set getSelectedMetaEntities() {
			return selectedMetaEntities;
		}
		
		/**
		 * Gets a list of all the valid symbol types for this diagram.
		 * @return List of SymbolType, may be empty, never null.
		 */
		public List getSymbolTypes(){
			List symbolTypeList = new LinkedList();
			for(int i=0; i<symbolTypes.length; ++i){
				if(symbolTypes[i] != null){
					symbolTypeList.add(symbolTypes[i]);
				}
			}
			return symbolTypeList;
		}
		/**
		 * @return
		 */
		public Map getSelectedItemMap() {
			Map result = new HashMap();
			for(int i=0; i<symbolTags.length; ++i){
				if(symbolTags[i] != null){
					result.put(metaEntities[i], symbolTags[i].getValue());
				}
			}
			return result;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return metaEntities.length;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 3;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex){
				case 0:
					return metaEntities[rowIndex];
				case 1:
					return symbolTags[rowIndex];
				case 2:
					return buttons[rowIndex];
			}
			assert(false);
			return null;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int r, int c){
			if(c == 1) {
				String val = null;
				if(value != null) {
					val = value.toString().trim();
					if(val.length() == 0) {
						val = null;
					}
				}
				
				if(symbolTags[r] == null && val != null) {
					symbolTags[r] = new ItemTag();
				}
				
				if(val != null) {
					symbolTags[r].setValue(val);
					
					// need to create a corresponding symbol type for editing.
					// val should name the symbol type.
					Map allowableSymbols = allowable.getAllowableSymbols();
					MetaEntity me = metaEntities[r];
					Class symbolClass = (Class)allowableSymbols.get(val);
					SymbolType st= new SymbolType(new UUID(), me,symbolClass,me.getName());					
					symbolTypes[r] = st;
					
					addButton(r,st);
					
					selectedMetaEntities.add(metaEntities[r]);
					
				} else {
					symbolTags[r] = null;
					symbolTypes[r] = null;
					buttons[r] =  null;
					selectedMetaEntities.remove(metaEntities[r]);
				}
				
				TableModelEvent e = new TableModelEvent(this,r,r,c);
				fireTableChanged(e);
				
			}
		}
		
		/**
		 * @param row
		 * @param st
		 */
		private void addButton(int row, SymbolType st){
			JButton button = new JButton("Edit");
//			button.setBackground(st.getBackColour());
//			button.setForeground(st.getTextColour());
//			button.setFont(st.getFont());
			button.addActionListener( new EditActionListener(st));
			buttons[row] = button;			
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int column) {
			return columnClasses[column];
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column){
			return headers[column];
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column) { 
			return column == 1 || column == 2;
		}
	}
	
	/**
	 * ConnectorsModel
	 * Provides a TableModel for the connectors.
	 *
	 */
	private class ConnectorsModel extends AbstractTableModel {

	    /** A Set of MetaRelationship that holds all the valid meta relationships
	     * that can be represented on the diagram.  This changes depending
	     * which MetaEntities are selected in the SymbolsModel.
	     */
		private Set validRelationships = new HashSet(); // of MetaRelationship
		
		/** The column of MetaRelationships in the table */
		private Vector relationships = new Vector();	// of MetaRelationship
		
		/** The column of ItemTag in the table that holds the name of the desired connector*/
		private Vector itemTags = new Vector();			// of ItemTag holding connector type
		
		private String[] headers = {"Relationship", "Connector"};
		private Class[] columnClasses = {String.class, ItemTag.class};

		public ConnectorsModel(){
		}
		
		/**
		 * Initialises the connectors table model from the diagram type.
		 * Note that relationships should already be setup by the 
		 * initialisation process before calling this.
		 * @param diagramType is the diagram type to initialise from.
		 * @throws ClassNotFoundException - if one of the allowable types cannot be found.
		 */
		public void initFromDiagramType(StandardDiagramType diagramType)
		throws ClassNotFoundException{

			AllowableElements allowable = AllowableElements.getAllowable();
			// allowable should be a one to one mapping of friendly name to
			// a Class of Symbol or it's subtypes. Will need to do inverse mapping
			// to set up the ItemTags
			
			Map nameLookup = new InvertedMap(allowable.getAllowableConnectors());
			for(int i=0; i<relationships.size(); ++i){
				MetaRelationship mr = (MetaRelationship)relationships.get(i);
				if(diagramType.hasConnectorTypeFor(mr)){
					ConnectorType ct = diagramType.getConnectorTypeFor(mr);
					String name = (String)nameLookup.get(ct.getRenderClass());
					itemTags.add(i, new ItemTag(name));
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int column) {
			return columnClasses[column];
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int column){
			return headers[column];
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int column) { 
			return column == 1;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return relationships.size();
		}

		/**
		 * pruneRelationships - any relationships in the validRelationships
		 * set that aren't in the vector need to be added, and any in the
		 * vector, not in the set need to be removed.
		 */
		protected void pruneRelationships() {
		    Set valid = new HashSet();
		    valid.addAll(validRelationships);
			for(int i = relationships.size()-1; i>=0; --i){
				MetaRelationship r = (MetaRelationship)relationships.get(i);
				if(valid.contains(r)) {
					valid.remove(r);
				} else { // invalid - remove
					relationships.remove(i);
					itemTags.remove(i);
				}
			}
			// Any left in validRelationships are new:
			for (Iterator iter = valid.iterator(); iter.hasNext();) {
				MetaRelationship element = (MetaRelationship) iter.next();
				relationships.add(element);
				itemTags.add(null);
			}
			
			TableModelEvent e = new TableModelEvent(this);
			fireTableChanged(e);
		}

		/**
		 * Adds a relationship to the set of valid relationships.
		 * @param r is the relationship to add.
		 */
		protected void validRelationship(MetaRelationship r) {
			validRelationships.add(r);
			
		}

		/**
		 * Resets the list of valid relationships.  By then calling
		 * validRelationship(MetaRelationship) a set of valid 
		 * relationships (determined by the selected symbols) can
		 * be built up.
		 */
		protected void resetRelationships() {
			validRelationships.clear();
			
		}

		/**
		 * Gets the set of valid relationships in the connectors
		 * table model.
		 * @return Set  of MetaRelationship.
		 */
		public Set getValidRelationships() {
			return validRelationships;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex){
				case 0:
					return relationships.get(rowIndex);
				case 1:
					return itemTags.get(rowIndex);					
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int r, int c){
			if(c == 1) {
				String val = null;
				if(value != null) {
					val = value.toString().trim();
					if(val.length() == 0) {
						val = null;
					}
				}
				
				if(itemTags.get(r) == null && val != null) {
					itemTags.set(r, new ItemTag());
				}
				
				if(val != null) {
					((ItemTag)itemTags.get(r)).setValue(val);
				} else {
					itemTags.set(r,null);
				}
				
				TableModelEvent e = new TableModelEvent(this,r,r,c);
				fireTableChanged(e);
			}
		}
		
		/**
		 * This gets a map of the selected connectors.  The map is keyed by
		 * MetaRelationship and the value is the text that describes which
		 * connector to use.
		 * @return a Map of selected connectors, maybe empty, never null.
		 */
		public Map getSelectedItemMap() {
			Map result = new HashMap();
			for(int i=0; i<itemTags.size(); ++i){
				if(itemTags.get(i) != null){
					result.put(relationships.get(i), ((ItemTag)itemTags.get(i)).getValue());
				}
			}
			return result;
		}

	};
	
	/**
	 * ItemTag
	 * provides a class to hold the selected symbol  or
	 * connector when editing the tables.
	 *
	 */
	private class ItemTag {
		private String value = null;
		public ItemTag(){
		}
		public ItemTag(String value){
			this.value = value;
		}
		public void setValue(String value){
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public String toString() {
			return value;
		}
	}
	
	/**
	 * ItemTagRenderer
	 * is a TableCellRenderer for displaying the selected
	 * symbol or connector for that cell.
	 *
	 */
	private class ItemTagRenderer extends JLabel implements TableCellRenderer {

		private JLabel emptyCellLabel = new JLabel("");
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column) {
			if (value == null) {
				return emptyCellLabel;
			}

			if (value instanceof ItemTag) {
				this.setText(value.toString());
			}
			return this;
		}
	}
	/**
	 * AllowableCombo provides a combo box to display allowable values. A 
	 * convenience class that displays the keys from a map of allowable 
	 * values and adds an empty value at the start of the list for 
	 * selecting "none".
	 * @author Bruce.Porteous
	 *
	 */
	private class AllowableCombo extends JComboBox{	
		public AllowableCombo(Map values){
			addItem(""); // initial blank entry.
			for (Iterator iter = values.keySet().iterator(); iter.hasNext();) {
				addItem(iter.next());
			}
		}
	}

	/**
	 * EditActionListener is used for the edit buttons in the table to 
	 * allow editing of the symbol types.
	 * @author Bruce.Porteous
	 *
	 */
	private class EditActionListener implements ActionListener {
		private SymbolType symbolType;
			
		public EditActionListener(SymbolType symbolType){
			this.symbolType = symbolType;
		}
			
		public void actionPerformed(ActionEvent evt){
			TextObjectSettingsDialog dlg = new TextObjectSettingsDialog(symbolType, thisDialog,"Configure Symbol Type");
			dlg.setVisible(true);
		}
	}

	/**
	 * ButtonRenderer
	 * @author Bruce.Porteous
	 *
	 */
	private class ButtonRenderer implements TableCellRenderer{


		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if(value != null){
				assert(value instanceof JButton);
				JButton button = (JButton)value;
				return button;
			}
			return null;
		}
	}
	
	private class ButtonEditor extends AbstractCellEditor implements TableCellEditor{
		private Object value;
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.value = value;
			return (Component)value;
		}

		public Object getCellEditorValue(){
			return value;
		}
 		
	}
	
}
