/*
 * ModelViewerItemHandler.java
 * Project: EATool
 * Created on 02-Feb-2006
 *
 */
package alvahouse.eatool.gui.graphical.standard.model;

import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.ActionSet;
import alvahouse.eatool.gui.Dialogs;
import alvahouse.eatool.gui.EntityEditor;
import alvahouse.eatool.gui.GUIBuilder;
import alvahouse.eatool.gui.PositionalPopup;
import alvahouse.eatool.gui.graphical.EntitySelectionDialog;
import alvahouse.eatool.gui.graphical.standard.ItemHandler;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramViewer;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.graphical.standard.Connector;
import alvahouse.eatool.repository.graphical.standard.ConnectorType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagram;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.graphical.standard.SymbolType;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaRelationship;
import alvahouse.eatool.repository.model.Entity;
import alvahouse.eatool.repository.model.Relationship;
import alvahouse.eatool.util.SettingsManager;
import alvahouse.eatool.util.UUID;

/**
 * ModelViewerItemHandler is the ItemHandler for the ModelViewer.  All model
 * specific code e.g. handling Entities and Relationships as distinct from
 * Symbols and Connectors should be held in this class.
 * 
 * @author rbp28668
 */
public class ModelViewerItemHandler implements ItemHandler {

    private StandardDiagramType diagramType;
    private SettingsManager.Element cfg;
    private Application app;
    private Repository repository;
    /**
     * 
     */
    public ModelViewerItemHandler(StandardDiagramType diagramType, Application app, Repository repository) {
        super();
        this.diagramType = diagramType;
        this.app = app;
        this.repository = repository;
        
        cfg = app.getConfig().getElement("/ModelViewer/popups");
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#editSymbolItem(java.lang.Object)
     */
    public boolean editSymbolItem(Component parent, Object item) throws Exception {
		Entity entity = (Entity)item;
		EntityEditor editor = new EntityEditor(parent, entity, repository);
		editor.setVisible(true);
        boolean edited = editor.wasEdited();
        editor.dispose();
        return edited;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#addSymbolAt(int, int)
     */
    public Symbol[] addSymbolsAt(Component parent, int x, int y) throws LogicException {
    
        Symbol[] symbols = null;
        
    	List<MetaEntity> allowedMeta = new LinkedList<>();
		
		try {
			Collection<SymbolType> symbolTypes = diagramType.getSymbolTypes();
			for(SymbolType symbolType : symbolTypes){
				MetaEntity me = symbolType.getRepresents();
				allowedMeta.add(me);
			}

		
			EntitySelectionDialog dlg = new EntitySelectionDialog(parent, allowedMeta, repository.getModel());
			dlg.setVisible(true);
			
			if(dlg.isEntitySelected()){
			    Entity[] entities = dlg.getAllSelected();
			    
			    symbols = new Symbol[entities.length];
			    int boxSize = (int)(Math.sqrt(entities.length));
			    int inRow = 0;
			    int startX = x;
			    for(int i=0; i<entities.length; ++i){
					Entity entity = entities[i];
					
					// Need to get the correct symbol type for this entity.
					SymbolType st = diagramType.getSymbolTypeFor(entity.getMeta());
					Symbol symbol = st.newSymbol(entity , x, y);
					symbols[i] = symbol;
					// Arbitrarily arrange multiple.
					++inRow;
					x += 50;
					if(inRow > boxSize){
					    inRow = 0;
					    x = startX;
					    y += 20;
					}
			        
			    }
			}
			return symbols;
		} catch (Exception e) {
			throw new LogicException("Unable to add symbols to diagram",e);
		}
    }

	/**
	 * Attempts to join 2 symbols on the diagram.
	 * @param first is the first Symbol to be connected.
	 * @param second is the second Symbol to be connected.
	 * 
	 * @return LogicException - if the connector can't be created.
	 */
	public Connector addConnector(Component parent, Symbol first, Symbol second) 
	throws Exception{
		// Need to figure out what the options are for
		// this diagram
		
		if(first == second) {
			return null;
		}
		
		StandardDiagram diagram = ((StandardDiagramViewer.ViewerPane)parent).getDiagram();

		Set<Relationship> connectingRelationships = getConnectingRelationships(first,second);
		Set<ConnectorType> possibleConnectors = getPossibleConnectorsBetween(diagram, first, second);
		connectingRelationships = pruneRelationships(connectingRelationships,possibleConnectors);
		// possibleConnectors has a set of all the possible connectors to use if the user wants to
		// create a new relationship between the 2 symnbols.
		// connectingRelationships has a set of all the relationships that exist between the 2
		// symbols (Entities) that can be expressed using connectors allowed in this diagram
		
		Object selected = null;
		String newOption = "--New--";
		
		if(connectingRelationships.isEmpty()){
			int sel = JOptionPane.showConfirmDialog(parent,"Create a new Relationship?", "EATool", JOptionPane.YES_NO_OPTION);
			if(sel == JOptionPane.YES_OPTION){
				selected = newOption;
			}
		} else {
			Object[] values = new Object[connectingRelationships.size() + 1];
			int idx = 0;
			values[idx++] = newOption;
			//StandardDiagramType dt = diagram.getType();
			
			for(Iterator<Relationship> iter = connectingRelationships.iterator(); iter.hasNext();){
				Relationship r = (Relationship)iter.next();
				values[idx++] = new RelationshipProxy(r, diagramType.getConnectorTypeFor(r.getMeta()));
			}
			if(values.length == 1){
			    selected = values[0];
			} else if (values.length > 1){
				selected = JOptionPane.showInputDialog(parent,
				"Select the Relationship to use", "EATool", JOptionPane.QUESTION_MESSAGE,
				null,values,newOption);
			}
		}
		
		// If the user hasn't selected anything then bail out.
		if(selected == null) {
			return null;
		}
		
		//System.out.println("Selected " + selected);
		
		// If the user has selected a new relationship then ask them which type
		// and magic up a relationship of the appropriate type.
		if(selected  == newOption) { 
		    ConnectorType ct = null;
		    int count = possibleConnectors.size();
		    if(count == 1){
		        ct = (ConnectorType)possibleConnectors.iterator().next();
		    } else if (count > 1){
				ct = (ConnectorType)JOptionPane.showInputDialog(parent,
				"Select the Relationship Type", "EATool", JOptionPane.QUESTION_MESSAGE,
				null,possibleConnectors.toArray(),null);
				//System.out.println("Selected connector type " + ct);
		    }
			
			if(ct == null){ // user has abandoned or impossible....
				return null;
			}
			
			Relationship r = new Relationship( ct.getRepresents());
			r.start().setConnection((Entity)first.getItem());
			r.finish().setConnection((Entity)second.getItem());
			
			selected = new RelationshipProxy(r,ct);
			
			try {
				repository.getModel().addRelationship(r);
			} catch (Exception e) {
				throw new LogicException("Unable to add relationship to model",e);
			}
		} 
		
		assert(selected != null);
		assert(selected instanceof RelationshipProxy);
		
		RelationshipProxy proxy = (RelationshipProxy)selected;
		
		Connector con = proxy.getConnectorType().newConnector(new UUID());
		con.setItem(proxy.getRelationship());
		return con;
	}

	/**
	 * Find the possible existing relationships that link the 2 
	 * symbols.  If we get the sets of relationships that link to
	 * the first symbol, the set of relationships that link to 
	 * the second symbol and take the intersection, then 
	 * any relationships remaining in relFirst connect the
	 * 2 symbol's entities.
	 * @param first is the first Symbol to be connected.
	 * @param second is the second Symbol to be connected.
	 * @return a Set of Relationship. Maybe empty, never null.
	 */
	private Set<Relationship> getConnectingRelationships(Symbol first, Symbol second) throws Exception{
		Entity entityFirst = (Entity)first.getItem();
		Entity entitySecond = (Entity)second.getItem();
		Set<Relationship> relFirst = entityFirst.getConnectedRelationships();
		Set<Relationship> relSecond = entitySecond.getConnectedRelationships();
		relFirst.retainAll(relSecond); // intersection
		
		return relFirst;
	}
	
	/**
	 * Gets a set of connectors that could be used to join 2 symbols.  The
	 * set is produced by only accepting connector types on the current diagram
	 * type which have a meta relationship which connect the meta entities of the
	 * entities of the symbols. (i.e. figure out the connectivity via the 
	 * base meta-model).
	 * @param first is the first Symbol to be connected.
	 * @param second is the second Symbol to be connected.
	 * @return A Set of ConnectorType.  Maybe empty, not null.
	 */
	private Set<ConnectorType> getPossibleConnectorsBetween(StandardDiagram diagram, Symbol first, Symbol second) throws Exception{
		Entity entityFirst = (Entity)first.getItem();
		Entity entitySecond = (Entity)second.getItem();
		
		StandardDiagramType diagramType = (StandardDiagramType)diagram.getType();
		Collection<?> connectorTypes = diagramType.getConnectorTypes();
		Set<ConnectorType> validConnectorTypes = new HashSet<ConnectorType>();
		for(Iterator<?> iter = connectorTypes.iterator(); iter.hasNext();){
			ConnectorType ct = (ConnectorType)iter.next();
			MetaRelationship mr = ct.getRepresents();
			MetaEntity metaStart = mr.start().connectsTo();
			MetaEntity metaFinish = mr.finish().connectsTo();
			
			MetaEntity metaFirst = entityFirst.getMeta();
			MetaEntity metaSecond = entitySecond.getMeta();
			
			// Need to run up the inheritance hierarchy for both ends: if the first 
			// metaEntity (or it's super-types) equals the start MetaEntity AND
			// the second MetaEntity (or it's super-types) equals the finish MetaEntity
			// then we win.
			boolean startOK = false;
			while(metaFirst != null && !startOK){
			    startOK = metaFirst.equals(metaStart);
			    metaFirst = metaFirst.getBase();
			}
			
			boolean finishOK = false;
			while(metaSecond != null && !finishOK){
			    finishOK = metaSecond.equals(metaFinish);
			    metaSecond = metaSecond.getBase();
			}
			
			// Note - only one way test as direction is important.			
			if(startOK && finishOK) {
				validConnectorTypes.add(ct);
			}
		}
		return validConnectorTypes;
	}
	
	/**
	 * This prunes the set of relationships discarding those that cannot be
	 * represented by the given set of connectorTypes.  This is to allow for
	 * existing relationships that cannot be represented on the current 
	 * diagram.
	 * @param relationships is the set of relationships to prune.
	 * @param connectorTypes is the set of connectorTypes to prune by.
	 * @return a pruned Set of Relationship.
	 */
	private Set<Relationship> pruneRelationships(Set<Relationship> relationships, Set<ConnectorType> connectorTypes){
		// Create set of allowable MetaRelationships from the connectorTypes.
		Set<MetaRelationship> allowableMeta = new HashSet<MetaRelationship>();
		for(Iterator<ConnectorType> iter = connectorTypes.iterator(); iter.hasNext();){
			ConnectorType ct = (ConnectorType)iter.next();
			allowableMeta.add(ct.getRepresents());
		}
		
		Set<Relationship> pruned = new HashSet<Relationship>();
		for(Iterator<Relationship> iter = relationships.iterator(); iter.hasNext();){
			Relationship r = (Relationship)iter.next();
			if(allowableMeta.contains(r.getMeta())){
				pruned.add(r);
			}
		}
		return pruned;
	}

   	/**
	 * RelationshipProxy used to proxy for relationships in pick lists.
	 * @author Bruce.Porteous
	 *
	 */
	private class RelationshipProxy{
   		private Relationship r;
   		private ConnectorType ct;
   		RelationshipProxy(Relationship relationship, ConnectorType connectorType){
   			r = relationship;
   			ct = connectorType;
   		}
   		public Relationship getRelationship(){
   			return r;
   		}
   		public ConnectorType getConnectorType(){
   			return ct;
   		}
   		public String toString(){
   			return r.getMeta().getName();
   		}
   	}

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#getPopupFor(java.lang.Class)
     */
    public PositionalPopup getPopupFor(StandardDiagramViewer viewer, Class<?> targetClass) {
        ActionSet actions = new ModelViewerActionSet(viewer, app, repository);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,targetClass);
       return popup;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#getBackgroundPopup()
     */
    public PositionalPopup getBackgroundPopup(StandardDiagramViewer viewer) {
        ActionSet actions = new ModelViewerActionSet(viewer, app, repository);
        PositionalPopup popup = GUIBuilder.buildPopup(actions,cfg,"Background");
       return popup;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.gui.graphical.ItemHandler#addSymbolNewItem(java.awt.Component, int, int)
     */
    public Symbol addSymbolNewItem(Component parent, int x, int y) throws Exception {
        
       	List<MetaEntity> allowedMeta = new LinkedList<MetaEntity>();
		
		Collection<?> symbolTypes = diagramType.getSymbolTypes();
		for(Iterator<?> iter = symbolTypes.iterator(); iter.hasNext();){
			SymbolType symbolType = (SymbolType)iter.next();
			MetaEntity me = symbolType.getRepresents();
			allowedMeta.add(me);
		}
		
		Symbol symbol = null;
		
		MetaEntity selected = (MetaEntity)Dialogs.selectNRIFrom(allowedMeta, "Select type of Entity", parent);
		if(selected != null){
		    Entity entity = new Entity(selected);
		    
		    boolean edited = true;
		    if(!selected.getMetaProperties().isEmpty()){
				EntityEditor editor = new EntityEditor(parent, entity,repository);
				editor.setVisible(true);
		        edited = editor.wasEdited();
		        editor.dispose();
		    }	        
	        if(edited){
				SymbolType st = diagramType.getSymbolTypeFor(entity.getMeta());
				symbol = st.newSymbol(entity , x, y);
				try {
					repository.getModel().addEntity(entity);
				} catch (Exception e) {
					throw new LogicException("Unable to add entity to model",e);
				}
	        }

		}

        return symbol;
    }

}
