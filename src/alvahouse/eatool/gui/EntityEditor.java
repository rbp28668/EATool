/*
* EntityEditor.java
*
* Created on 14 March 2002, 22:09
*/

package alvahouse.eatool.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import alvahouse.eatool.Main;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaProperty;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.metamodel.MetaRole;
import alvahouse.eatool.repository.metamodel.Multiplicity;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.model.Role;

/**
 * EntityEditor provides editing for a single Entity. This edits the entity "in place"
 * and doesn't update the model.  If wasEdited returns true the caller should update
 * the model and call persistRelationships to update any relationships in the model.  
 * The
 * actual editing is done within the inner classes, {@link RelationshipPanel}
 * and {@link PropertiesPanel}. In turn the panels use <code>JTable</code> which
 * handles the actual editing via further inner classes, subclasses of
 * <code>AbstractTableModel</code>: <code>PropertiesTableModel</code> and
 * <code>RelationshipsTableModel</code>
 * 
 * @author rbp28668
 */
public class EntityEditor extends BasicDialog {

	private static final long serialVersionUID = 1L;

	/** The original entity to be edited */
	private Entity originalEntity;

	/** Panel for handling property edit - presented in the properties tab */
	private PropertiesPanel propertiesPanel;

	/** Panel for handling relationship edit - presented in the relationships tab */
	private RelationshipsPanel relationshipsPanel;

	/** Repository to contain associated relationships */
	private Repository repository;

	/**
	 * Creates new form EntityEditor
	 * 
	 * @param parent is the parent Component that created this dialog
	 * @param        e, Entity is the entity to be edited
	 */
	public EntityEditor(Component parent, Entity e, Repository repository) throws Exception {
		super(parent, "Edit Entity");

		originalEntity = e;
		this.repository = repository;

		JLabel label = new JLabel("uuid: " + e.getKey().toString());
		label.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
		getContentPane().add(label, BorderLayout.NORTH);

		propertiesPanel = new PropertiesPanel(e, e.getMeta());
		relationshipsPanel = new RelationshipsPanel(e, repository.getModel(), repository.getMetaModel());

		JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP);
		tabs.addTab("Properties", propertiesPanel);
		tabs.addTab("Relationships", relationshipsPanel);
		getContentPane().add(tabs, BorderLayout.CENTER);
		getContentPane().add(getOKCancelPanel(), BorderLayout.EAST);
		pack();
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
	 */
	protected void onOK() {
		try {
			propertiesPanel.onOK(); // updates original entity.
			relationshipsPanel.onOK(); // and deal with any changes to relationships.
		} catch (Exception e) {
			new ExceptionDisplay(this, e);
		}
	}

	/**
	 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
	 */
	protected boolean validateInput() throws Exception{
		return propertiesPanel.validateInput() && relationshipsPanel.validateInput();
	}

	/**
	 * Persist all the relationships that may have been added or removed.
	 * @param is the model to persist to.
	 * @throws Exception
	 */
	public void persistRelationships(Model model) throws Exception {
		relationshipsPanel.persistRelationships(model);
	}
	
	/* ================================================================= */
	// RelationshipsPanel is the editing panel for the Entity's
	// relationships.
	/* ================================================================= */
	private class RelationshipsPanel extends JPanel {

		private final LinkedList<RelationshipsSubPanel> relationshipPanels = new LinkedList<>();

		private static final long serialVersionUID = 1L;

		RelationshipsPanel(final Entity e, final Model model, final MetaModel metaModel) throws Exception {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			MetaEntity meta = e.getMeta();
			Set<MetaRelationship> metaRelationships = metaModel.getMetaRelationshipsFor(meta);
			for (MetaRelationship mr : metaRelationships) {
				RelationshipsSubPanel sub = new RelationshipsSubPanel(e, model, mr);
				relationshipPanels.add(sub);
				add(sub);
			}

		}

		/**
		 * @see alvahouse.eatool.GUI.BasicDialog#onOK()
		 */
		protected void onOK() throws Exception {
			for (RelationshipsSubPanel sub : relationshipPanels) {
				sub.onOK();
			}
		}

		/**
		 * @see alvahouse.eatool.GUI.BasicDialog#validateInput()
		 */
		protected boolean validateInput() throws Exception {
			for (RelationshipsSubPanel sub : relationshipPanels) {
				if (!sub.validateInput()) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Persist all the relationships which may have been added or removed.
		 * @throws Exception
		 */
		void persistRelationships(Model model) throws Exception {
			for (RelationshipsSubPanel sub : relationshipPanels) {
				sub.persistRelationships(model);
			}
		}

	}

	/* ================================================================= */
	// RelationshipsSubPanel is the editing panel for the Entity's
	// relationships.
	/* ================================================================= */
	private class RelationshipsSubPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private final JTable table;
		private final List<Relationship> relationshipsToAdd = new LinkedList<Relationship>();
		private final List<Relationship> relationshipsToDelete = new LinkedList<Relationship>();
		private final RelationshipsTableModel tableModel;
		private final MetaRelationship ofType;

		/**
		 * Method RelationshipsPanel.
		 * 
		 * @param e
		 * @param model is the containing model or at least, the model the entity will
		 *              be added to.
		 */
		RelationshipsSubPanel(final Entity e, final Model model, MetaRelationship ofType) throws Exception{
			this.ofType = ofType;

			setBorder(new StandardBorder());
			setLayout(new BorderLayout());

			add(new JLabel(ofType.getName()), BorderLayout.NORTH);

			tableModel = new RelationshipsTableModel(e, model, this, ofType);
			table = new RelationshipsTable(tableModel);
			table.setRowSelectionAllowed(true);
			table.setColumnSelectionAllowed(false);
			ButtonEditor buttonEditor = new ButtonEditor();
			table.setDefaultRenderer(JButton.class, buttonEditor);
			table.setDefaultEditor(JButton.class, buttonEditor);

			JScrollPane scroll = new JScrollPane(table);
			add(scroll, BorderLayout.CENTER);

			JButton addNewButton = new JButton("Add New Relationship");
			JButton deleteSelectedButton = new JButton("Delete Selected Relationship");

			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(addNewButton);
			box.add(Box.createHorizontalStrut(20));
			box.add(deleteSelectedButton);
			box.add(Box.createHorizontalGlue());

			add(box, BorderLayout.SOUTH);

			addNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						addNewRelationship();
					} catch (Exception e) {
						new ExceptionDisplay(EntityEditor.this, e);
					}
				}
			});
			deleteSelectedButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					deleteSelectedRelationships();
				}
			});
			pack();
		}

		/**
		 * Adds a new relationship to this entity.
		 */
		private void addNewRelationship() throws Exception {
			// For this type of entity, need to get list of MetaRelationships that
			// describe the relationships that can connect to this.
			MetaEntity me = originalEntity.getMeta();
			// Now we need to pick up the allowable Entities at the "other end" of
			// the relationship.
			MetaEntity otherMetaEntity = null;
			MetaRole thisMetaRole = null;
			MetaRole otherMetaRole = null;
			boolean existingIsStart = true;

			// Order of these 2 if statements - if either end is valid then default to start
			// end.
			if (ofType.finish().connectsTo().equals(me)) {
				otherMetaEntity = ofType.start().connectsTo();
				thisMetaRole = ofType.finish();
				otherMetaRole = ofType.start();
				existingIsStart = false;
			}
			if (ofType.start().connectsTo().equals(me)) {
				otherMetaEntity = ofType.finish().connectsTo();
				thisMetaRole = ofType.start();
				otherMetaRole = ofType.finish();
				existingIsStart = true;
			}

			if (otherMetaEntity == null) {
				throw new IllegalStateException("Can't find other entity type when adding new relationship");
			}

			Entity other = Dialogs.selectEntityOf(otherMetaEntity, this, repository);
			if (other != null) {
				Relationship r = new Relationship(ofType);

				Role thisEnd = new Role(thisMetaRole);
				thisEnd.setConnection(originalEntity);
				thisEnd.setRelationship(r);

				Role otherEnd = new Role(otherMetaRole);
				otherEnd.setConnection(other);
				otherEnd.setRelationship(r);

				if (existingIsStart) {
					r.setStart(thisEnd);
					r.setFinish(otherEnd);
				} else {
					r.setStart(otherEnd);
					r.setFinish(thisEnd);
				}

				// System.out.println("New Relationship for " + editCopy + " is " + r);

				relationshipsToAdd.add(r);
				tableModel.add(r);
			}
		}

		/**
		 * Removes any selected relationships from the grid and puts them in a delete
		 * list.
		 */
		private void deleteSelectedRelationships() {
			if (table.getSelectedRowCount() > 0) {
				int[] selected = table.getSelectedRows();
				Relationship[] toRemove = new Relationship[selected.length];
				for (int i = 0; i < selected.length; ++i) {
					Relationship r = tableModel.getRow(selected[i]);
					relationshipsToDelete.add(r);
					toRemove[i] = r;
				}
				for (int i = 0; i < toRemove.length; ++i) {
					tableModel.remove(toRemove[i]);
				}
			}
		}

		/**
		 * Check that the relationships in the model will still be valid when the
		 * changes are applied.
		 * 
		 * @return true if valid, false if not.
		 */
		protected boolean validateInput() throws Exception {
			boolean valid = true;
			normaliseRelationships();

			Map<RelationshipKey, Count> track = new HashMap<RelationshipKey, Count>();
			// Check each relationship we're going to add
			for (Relationship r : relationshipsToAdd) {
				// Entity startEntity = r.start().connectsTo();
				Count count = getCount(track, r, true);
				count.inc(); // another relationship of this type for the entity.

				// Entity finishEntity = r.start().connectsTo();
				count = getCount(track, r, false);
				count.inc(); // another relationship of this type for the entity.
			}

			// Check each relationship we're going to delete
			for (Relationship r : relationshipsToDelete) {
				// Entity startEntity = r.start().connectsTo();
				Count count = getCount(track, r, true);
				count.dec(); // one less relationship of this type for the entity.

				// Entity finishEntity = r.start().connectsTo();
				count = getCount(track, r, false);
				count.dec(); // one less relationship of this type for the entity.
			}

			List<String> faults = new LinkedList<String>();
			for (Map.Entry<RelationshipKey, Count> entry : track.entrySet()) {
				RelationshipKey key = (RelationshipKey) entry.getKey();
				Count count = (Count) entry.getValue();

				if (!key.getMultiplicity().isValid(count.getCount())) {
					String fault = "Entity " + key.getEntity() + " has " + count.getCount() + " of "
							+ key.getMetaRelationship().getName() + " but should be "
							+ key.getMultiplicity().toString();
					faults.add(fault);
					valid = false;
				}
			}
			if (faults.size() > 0) {
				JList<String> list = new JList<String>();
				list.setListData(faults.toArray(new String[faults.size()]));
				list.setEnabled(false); // no point selecting items.
				JScrollPane scroll = new JScrollPane(list);
				JOptionPane.showMessageDialog(this, scroll, "Invalid Relationship(s)", JOptionPane.WARNING_MESSAGE);
			}
			return valid;
		}

		/**
		 * Helper method to set up a count for this given entity, relationship type and
		 * end (identified by MetaRole).
		 * 
		 * @param track       is the Map to track all the counts.
		 * @param r           is the relationship to check.
		 * @param startEntity is true if the entity is at the start end of the
		 *                    relationship, otherwise it should be false.
		 * @return A Count for this relationship type, entity and direction.
		 */
		private Count getCount(Map<RelationshipKey, Count> track, Relationship r, boolean isStart) throws Exception {
			Entity e = (isStart) ? r.start().connectsTo() : r.finish().connectsTo();
			MetaRole end = (isStart) ? r.finish().getMeta() : r.start().getMeta();

			RelationshipKey key = new RelationshipKey(e, r.getMeta(), end);
			Count count = (Count) track.get(key);
			if (count == null) {
				if (e.getModel() == null) {
					count = new Count();
				} else {
					Collection<Relationship> startRelationships = e.getConnectedRelationshipsOf(r.getMeta());
					count = new Count(startRelationships.size());
				}
				// System.out.println("Created new tracking record " + key + " with count " +
				// count.getCount());
				track.put(key, count);
			}
			return count;
		}

		/**
		 * Method onOK. Normalises the relationships to remove any that are created then deleted. 
		 * Note that this assumes that the optionality/cardinality constraints won't be
		 * invalidated. This should have been checked with validateInput().
		 * 
		 */
		void onOK() throws Exception {

			// First make sure any relationships in both add and delete lists
			// are removed from both (i.e. user changed his mind).
			normaliseRelationships();
		}
		
		/**
		 * PersistRelationships writes any relationship changes to the model. Not part of OnOk as
		 * we don't want to update the model in the editor but leave that to the control of the
		 * caller as the caller knows whether this is an existing entity or a new one. 
		 * @throws Exception
		 */
		void persistRelationships(Model model) throws Exception {
			for (Relationship toAdd : relationshipsToAdd) {
				model.addRelationship(toAdd);
			}
			for (Relationship toDelete : relationshipsToDelete) {
				model.deleteRelationship(toDelete.getKey());
			}
		}

		/**
		 * This removes any relationships that are in both the "to add" and in the "to
		 * delete" lists. No point adding and immediately deleting a relationship!
		 */
		private void normaliseRelationships() {
			List<Relationship> tempDelete = new LinkedList<Relationship>();
			for (Relationship toDelete : relationshipsToDelete) {
				if (relationshipsToAdd.contains(toDelete)) {
					relationshipsToAdd.remove(toDelete);
				} else {
					tempDelete.add(toDelete);
				}
			}
			relationshipsToDelete.clear();
			relationshipsToDelete.addAll(tempDelete);
		}
	}

	/* ================================================================= */
	// RelationshipsTable provides a JTable that asks its viewport
	// to only display 6 rows.
	/* ================================================================= */
	private static class RelationshipsTable extends JTable {

		private static final long serialVersionUID = 1L;
		private final RelationshipsTableModel tableModel;

		/**
		 * @param tableModel
		 */
		public RelationshipsTable(RelationshipsTableModel tableModel) {
			super(tableModel);
			this.tableModel = tableModel;
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			Dimension d = super.getPreferredScrollableViewportSize();
			int rows = tableModel.getRowCount();
			int maxRows = (rows > 6) ? 6 : rows; // max six rows
			d.height = maxRows * getRowHeight();
			return d;
		}
	}

	/* ================================================================= */
	// RelationshipsTableModel provides a display of the relationships
	// between this entity and other entities.
	/* ================================================================= */
	private static class RelationshipsTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private final Entity entity;
		private final Model model;
		private Vector<Relationship> relationships;
		private Vector<JButton> editButtons;
		private static String[] defaultHeaders = { "This Role", "Relationship", "Other Role", "To" };
		private final Vector<String> headers;
		private final Vector<Class<?>> columnTypes;
		private int editColumn;

		private final Component parentComponent;

		/**
		 * Method RelationshipsTableModel.
		 * 
		 * @param e               is the entity we're editing
		 * @param model           is the model it's going into or belongs into
		 * @param parentComponent in the UI.
		 * @toDisplay is either null, in which case all the relationships are used, or
		 *            to filter the relationships to those that correspond to this type.
		 */
		RelationshipsTableModel(Entity e, Model model, Component parentComponent, MetaRelationship toDisplay)
				throws Exception {
			this.entity = e;
			this.model = model;
			this.parentComponent = parentComponent;

			// Set up the headers & column types
			editColumn = 0;
			Collection<MetaProperty> mpc = toDisplay.getMetaProperties();
			int columnCount = defaultHeaders.length + mpc.size() + 1;// +1 for edit on the end
			this.headers = new Vector<String>(columnCount);
			this.columnTypes = new Vector<Class<?>>(columnCount);
			for (String header : defaultHeaders) {
				headers.add(header);
				columnTypes.add(String.class);
				++editColumn;
			}
			for (MetaProperty mp : mpc) {
				headers.add(mp.getName());
				columnTypes.add(String.class);
				++editColumn;
			}
			headers.add("Edit");
			columnTypes.add(JButton.class);

			if (e.getModel() == null) {
				relationships = new Vector<Relationship>(); // empty
				editButtons = new Vector<JButton>();
			} else {
				Set<Relationship> rs = e.getConnectedRelationships();
				relationships = new Vector<Relationship>(rs.size());
				editButtons = new Vector<JButton>(rs.size());

				for (Relationship r : rs) {
					if (toDisplay == null || r.getMeta().equals(toDisplay)) {
						relationships.add(r);
						editButtons.add(null);
					}
				}
			}
		}

		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return relationships.size();
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return headers.size();
		}

		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int col) {
			if (row < 0 || row >= relationships.size()) {
				throw new IllegalArgumentException("Row out of range in relationship table model");
			}

			Object val = "";

			try {
				final Relationship r = (Relationship) relationships.get(row);
				Role near;
				Role far;
				if (r.start().connectsTo().equals(entity)) {
					near = r.start();
					far = r.finish();
				} else {
					near = r.finish();
					far = r.start();
				}

				switch (col) {
				case 0:
					val = near.getMeta().getName();
					break;
				case 1:
					val = r.getMeta().getName();
					break;
				case 2:
					val = far.getMeta().getName();
					break;
				case 3:
					val = far.connectsTo().toString();
					break;
				default:
					if (col == editColumn) {
						JButton button = editButtons.get(row);
						if (button == null) {
							button = new JButton("Edit");
							button.setToolTipText("Edit this relationship");
							editButtons.setElementAt(button, row);

							button.setAction(new AbstractAction() {

								private static final long serialVersionUID = 1L;

								@Override
								public void actionPerformed(ActionEvent e) {
									try {
										RelationshipEditor editor = new RelationshipEditor(parentComponent, r, model);
										editor.setVisible(true);
									} catch (Exception e1) {
										new ExceptionDisplay(Main.getAppFrame(), e1);
									}
								}

							});
						}
						val = button;
					} else { // it's a property of the relationship
						int idx = col - defaultHeaders.length; // properties start just after default columns
						val = r.getPropertiesAsArray()[idx].getValue();
					}
					break;
				}
			} catch (Exception e) {
				new ExceptionDisplay(Main.getAppFrame(), e);
			}
			return val;
		}

		/**
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		public String getColumnName(int columnIndex) {
			return headers.get(columnIndex);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnTypes.get(columnIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return getColumnClass(columnIndex) == JButton.class;
		}

		/**
		 * Adds a new relationship to the table
		 * 
		 * @param r is the relationship to add.
		 */
		void add(Relationship r) {
			int idx = relationships.size();
			relationships.add(r);
			editButtons.add(null); // will be created if needed
			fireTableRowsInserted(idx, idx);
		}

		/**
		 * Removes a relationship from the table.
		 * 
		 * @param r is the relationship to remove.
		 */
		void remove(Relationship r) {
			int idx = relationships.indexOf(r);
			if (idx >= 0) {
				relationships.remove(idx);
				editButtons.remove(idx);
				fireTableRowsDeleted(idx, idx);
			}
		}

		/**
		 * Get the Relationship for a given row.
		 * 
		 * @param idx is the row to get the relationship for.
		 * @return the Relationship for the idx-th row.
		 */
		Relationship getRow(int idx) {
			return (Relationship) relationships.get(idx);
		}
	}

	/**
	 * RelationshipKey is a class used to form a composite key for a map based on an
	 * Entity, a MetaRelationship and a MetaRole. Note that the MetaRole should be
	 * at the far end of the relationship from the entity so that it can be used to
	 * check the multiplicity.
	 * 
	 * @author rbp28668
	 */
	private static class RelationshipKey {
		private Entity e;
		private MetaRelationship mr;
		private MetaRole end;

		RelationshipKey(Entity e, MetaRelationship mr, MetaRole end) {
			this.e = e;
			this.mr = mr;
			this.end = end;
		}

		public Multiplicity getMultiplicity() {
			return end.getMultiplicity();
		}

		public MetaRelationship getMetaRelationship() {
			return mr;
		}

		public Entity getEntity() {
			return e;
		}

		public int hashCode() {
			return 53 * e.hashCode() + 27 * mr.hashCode() + end.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof RelationshipKey) {
				RelationshipKey other = (RelationshipKey) o;
				return e.equals(other.e) && mr.equals(other.mr) && end.equals(other.end);
			}
			return false;
		}

		public String toString() {
			return e.toString() + "-" + mr.toString() + "-" + end.toString();
		}
	}

	/**
	 * Count is a simple integer counter, similar to Integer, but mutable.
	 * 
	 * @author rbp28668
	 */
	private static class Count {
		private int count;

		public Count() {
			this(0);
		}

		public Count(int initialValue) {
			count = initialValue;
			// System.out.println("Initialising count to " + count);
		}

		public void inc() {
			++count;
		}

		public void dec() {
			--count;
		}

		/**
		 * @return Returns the count.
		 */
		public int getCount() {
			return count;
		}
//        /**
//         * @param count The count to set.
//         */
//        public void setCount(int count) {
//            this.count = count;
//        }
	}
}
