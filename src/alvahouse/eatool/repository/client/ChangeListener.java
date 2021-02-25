/**
 * 
 */
package alvahouse.eatool.repository.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramChangeEvent;
import alvahouse.eatool.repository.graphical.DiagramChangeListener;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.DiagramsChangeEvent;
import alvahouse.eatool.repository.graphical.DiagramsChangeListener;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.images.Image;
import alvahouse.eatool.repository.images.ImageChangeEvent;
import alvahouse.eatool.repository.images.ImagesChangeListener;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.metamodel.MetaModelChangeEvent;
import alvahouse.eatool.repository.metamodel.MetaModelChangeListener;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.repository.model.ModelChangeEvent;
import alvahouse.eatool.repository.model.ModelChangeListener;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.repository.scripting.Script;
import alvahouse.eatool.repository.scripting.ScriptChangeEvent;
import alvahouse.eatool.repository.scripting.ScriptsChangeListener;
import alvahouse.eatool.util.XMLWriter;
import alvahouse.eatool.util.XMLWriterDirect;

/**
 * @author bruce_porteous
 *
 */
public class ChangeListener implements ModelChangeListener, MetaModelChangeListener, DiagramChangeListener,
		DiagramsChangeListener, ImagesChangeListener, ScriptsChangeListener {

	private final XMLWriter out;
	/**
	 * @throws IOException 
	 * 
	 */
	public ChangeListener() throws IOException {
		File file = File.createTempFile("EATool", "log");
		Writer writer = new FileWriter(file);
		out = new XMLWriterDirect(writer);
	}

	
	private void createItem(String type, KeyedItem item) throws Exception {
		out.startEntity("create");
		out.addAttribute("type", type);
		out.addAttribute("key", item.getKey().toString());
		output(item);
		out.stopEntity();
	}

	private void updateItem(String type, KeyedItem item) throws Exception {
		out.startEntity("update");
		out.addAttribute("type", type);
		out.addAttribute("key", item.getKey().toString());
		output(item);
		out.stopEntity();
	}
	
	private void removeItem(String type, KeyedItem item) throws IOException {
		out.startEntity("remove");
		out.addAttribute("type", type);
		out.addAttribute("key", item.getKey().toString());
		out.stopEntity();
	}
	
	private void output(KeyedItem item) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method writeXML = item.getClass().getDeclaredMethod("writeXML", XMLWriter.class);
		writeXML.invoke(item, out);
	}
	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.images.ImagesChangeListener#imageAdded(alvahouse.eatool.repository.images.ImageChangeEvent)
	 */
	@Override
	public void imageAdded(ImageChangeEvent event) throws Exception {
		Image source = (Image) event.getSource();
		createItem("Image",source);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.images.ImagesChangeListener#imageRemoved(alvahouse.eatool.repository.images.ImageChangeEvent)
	 */
	@Override
	public void imageRemoved(ImageChangeEvent event) throws Exception{
		Image source = (Image) event.getSource();
		removeItem("Image",source);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.images.ImagesChangeListener#imageEdited(alvahouse.eatool.repository.images.ImageChangeEvent)
	 */
	@Override
	public void imageEdited(ImageChangeEvent event) throws Exception{
		Image source = (Image) event.getSource();
		updateItem("Image",source);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#typesUpdated(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void typesUpdated(DiagramsChangeEvent e) {
		// Note e.getSource() will be null
		assert(e.getSource() == null);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramTypeAdded(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramTypeAdded(DiagramsChangeEvent e) throws Exception {
		DiagramType dt = (DiagramType) e.getSource();
		createItem("DiagramType",dt);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramTypeChanged(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramTypeChanged(DiagramsChangeEvent e) throws Exception {
		DiagramType dt = (DiagramType) e.getSource();
		updateItem("DiagramType",dt);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramTypeDeleted(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramTypeDeleted(DiagramsChangeEvent e) throws Exception {
		DiagramType dt = (DiagramType) e.getSource();
		removeItem("DiagramType", dt);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramsUpdated(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramsUpdated(DiagramsChangeEvent e) throws Exception {
		// Note, e.getSource() will be null
		assert(e.getSource() == null);

	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramAdded(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramAdded(DiagramsChangeEvent e) throws Exception {
		Diagram diag = (Diagram)e.getSource();
		createItem("Diagram",diag);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramChanged(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramChanged(DiagramsChangeEvent e) throws Exception  {
		Diagram diag = (Diagram)e.getSource();
		updateItem("Diagram",diag);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramsChangeListener#diagramDeleted(alvahouse.eatool.repository.graphical.DiagramsChangeEvent)
	 */
	@Override
	public void diagramDeleted(DiagramsChangeEvent e) throws Exception  {
		Diagram diag = (Diagram)e.getSource();
		removeItem("Diagram",diag);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#symbolAdded(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void symbolAdded(DiagramChangeEvent e) throws Exception  {
		Symbol symbol = (Symbol)e.getSource();
		createItem("Symbol",symbol);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#symbolDeleted(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void symbolDeleted(DiagramChangeEvent e) throws Exception  {
		Symbol symbol = (Symbol)e.getSource();
		removeItem("Symbol",symbol);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#symbolMoved(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void symbolMoved(DiagramChangeEvent e) throws Exception  {
		Symbol symbol = (Symbol)e.getSource();
		updateItem("Symbol",symbol);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#connectorAdded(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void connectorAdded(DiagramChangeEvent e) throws Exception  {
		Connector connector = (Connector)e.getSource();
		createItem("Connector",connector);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#connectorDeleted(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void connectorDeleted(DiagramChangeEvent e) throws Exception  {
		Connector connector = (Connector)e.getSource();
		removeItem("Connector",connector);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#connectorMoved(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void connectorMoved(DiagramChangeEvent e) throws Exception  {
		Connector connector = (Connector)e.getSource();
		updateItem("Connector",connector);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.graphical.DiagramChangeListener#majorDiagramChange(alvahouse.eatool.repository.graphical.DiagramChangeEvent)
	 */
	@Override
	public void majorDiagramChange(DiagramChangeEvent e) throws Exception  {
		Diagram diag = (Diagram)e.getSource();
		updateItem("Diagram",diag);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#modelUpdated(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void modelUpdated(MetaModelChangeEvent e) throws Exception  {
		MetaModel meta = (MetaModel)e.getSource();
		updateItem("MetaModel",meta);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityAdded(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void metaEntityAdded(MetaModelChangeEvent e) throws Exception  {
		MetaEntity me = (MetaEntity)e.getSource();
		createItem("MetaEntity",me);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void metaEntityChanged(MetaModelChangeEvent e) throws Exception  {
		MetaEntity me = (MetaEntity)e.getSource();
		updateItem("MetaEntity",me);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaEntityDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void metaEntityDeleted(MetaModelChangeEvent e) throws Exception  {
		MetaEntity me = (MetaEntity)e.getSource();
		createItem("MetaEntity",me);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipAdded(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void metaRelationshipAdded(MetaModelChangeEvent e) throws Exception  {
		MetaRelationship mr = (MetaRelationship)e.getSource();
		createItem("MetaRelationship",mr);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipChanged(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void metaRelationshipChanged(MetaModelChangeEvent e) throws Exception  {
		MetaRelationship mr = (MetaRelationship)e.getSource();
		updateItem("MetaRelationship",mr);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.metamodel.MetaModelChangeListener#metaRelationshipDeleted(alvahouse.eatool.repository.metamodel.MetaModelChangeEvent)
	 */
	@Override
	public void metaRelationshipDeleted(MetaModelChangeEvent e) throws Exception  {
		MetaRelationship mr = (MetaRelationship)e.getSource();
		removeItem("MetaRelationship",mr);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#modelUpdated(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void modelUpdated(ModelChangeEvent e) throws Exception  {
		Model model = (Model)e.getSource();
		updateItem("MOdel",model);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityAdded(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void EntityAdded(ModelChangeEvent e) throws Exception  {
		Entity entity = (Entity)e.getSource();
		createItem("Entity",entity);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void EntityChanged(ModelChangeEvent e) throws Exception  {
		Entity entity = (Entity)e.getSource();
		updateItem("Entity",entity);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#EntityDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void EntityDeleted(ModelChangeEvent e) throws Exception  {
		Entity entity = (Entity)e.getSource();
		removeItem("Entity",entity);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipAdded(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void RelationshipAdded(ModelChangeEvent e) throws Exception  {
		Relationship relationship = (Relationship)e.getSource();
		createItem("Relationship",relationship);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipChanged(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void RelationshipChanged(ModelChangeEvent e) throws Exception  {
		Relationship relationship = (Relationship)e.getSource();
		updateItem("Relationship",relationship);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.model.ModelChangeListener#RelationshipDeleted(alvahouse.eatool.repository.model.ModelChangeEvent)
	 */
	@Override
	public void RelationshipDeleted(ModelChangeEvent e) throws Exception  {
		Relationship relationship = (Relationship)e.getSource();
		removeItem("Relationship",relationship);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.scripting.ScriptsChangeListener#updated(alvahouse.eatool.repository.scripting.ScriptChangeEvent)
	 */
	@Override
	public void updated(ScriptChangeEvent e) throws Exception  {
		Script script = (Script)e.getSource();
		updateItem("Script",script);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.scripting.ScriptsChangeListener#scriptAdded(alvahouse.eatool.repository.scripting.ScriptChangeEvent)
	 */
	@Override
	public void scriptAdded(ScriptChangeEvent e) throws Exception  {
		Script script = (Script)e.getSource();
		createItem("Script",script);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.scripting.ScriptsChangeListener#scriptChanged(alvahouse.eatool.repository.scripting.ScriptChangeEvent)
	 */
	@Override
	public void scriptChanged(ScriptChangeEvent e) throws Exception  {
		Script script = (Script)e.getSource();
		updateItem("Script",script);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.scripting.ScriptsChangeListener#scriptDeleted(alvahouse.eatool.repository.scripting.ScriptChangeEvent)
	 */
	@Override
	public void scriptDeleted(ScriptChangeEvent e) throws Exception  {
		Script script = (Script)e.getSource();
		removeItem("Script",script);
	}

}
