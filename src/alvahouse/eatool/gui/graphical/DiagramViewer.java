/*
 * DiagramViewer.java
 * Project: EATool
 * Created on 24-Nov-2006
 *
 */
package alvahouse.eatool.gui.graphical;

import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JInternalFrame;

import alvahouse.eatool.gui.DiagramTypeEditor;
import alvahouse.eatool.gui.DiagramViewerWindowFactory;
import alvahouse.eatool.gui.graphical.standard.StandardDiagramTypeEditor;
import alvahouse.eatool.gui.graphical.standard.model.ModelViewer;
import alvahouse.eatool.gui.graphical.time.TimeDiagramType;
import alvahouse.eatool.gui.graphical.time.TimeDiagramTypeDialog;
import alvahouse.eatool.gui.graphical.time.TimeDiagramViewer;
import alvahouse.eatool.repository.graphical.Diagram;
import alvahouse.eatool.repository.graphical.DiagramType;
import alvahouse.eatool.repository.graphical.standard.StandardDiagramType;

/**
 * DiagramViewer is the abstract base class for all types of diagram viewer. It describes the
 * absolute minimum functionality that any diagram type may want.  Note that no assumptions are made
 * as to the content of the diagram.
 * 
 * @author rbp28668
 */
public abstract class DiagramViewer extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private Diagram diagram;

    private static final Map<Class<? extends DiagramType>, Class<? extends DiagramTypeEditor>> editorClasses = new HashMap<>();
    private static final Map<Class<? extends DiagramType>, Class<? extends DiagramViewerWindowFactory>> windowFactoryClasses = new HashMap<>();

    static {
		//TODO make extensible - currently all diagram types have to be hard wired here.
		editorClasses.put(StandardDiagramType.class, StandardDiagramTypeEditor.class);
		editorClasses.put(TimeDiagramType.class, TimeDiagramTypeDialog.class);
		
		windowFactoryClasses.put(StandardDiagramType.class, ModelViewer.WindowFactory.class);
		windowFactoryClasses.put(TimeDiagramType.class, TimeDiagramViewer.WindowFactory.class);
    }

    
    /**
     * @param title
     */
    public DiagramViewer(String title, Diagram diagram) {
        super(title);
        this.diagram = diagram;
    }

    public Diagram getDiagram() {
        return diagram;
    }
    
	public Color getBackgroundColour(){
	    return diagram.getBackgroundColour();
	}
	
	public void setBackgroundColour(Color background){
	    diagram.setBackgroundColour(background);
	    refresh();
	}

    /**
	 * 
	 */
	public abstract void refresh();

	public abstract void setZoom(float zoom);
	
	public abstract float getZoom();
	
	public abstract void fitDiagramToWindow();

	/**
	 * Given a diagram type magics up an appropriate window factory to create
	 * viewers for diagrams of that type.
	 * @param diagramType is the type you want a window factory for.
	 * @return a DiagramViewerWindowFactory.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static DiagramViewerWindowFactory getWindowFactory(DiagramType diagramType) throws InstantiationException, IllegalAccessException{
		Class<? extends DiagramViewerWindowFactory> diagramViewerWindowFactoryClass = windowFactoryClasses.get(diagramType.getClass());
		DiagramViewerWindowFactory windowFactory =  diagramViewerWindowFactoryClass.newInstance();
		return windowFactory;
	}
	
	/**
	 * Magics up a type editor for a given diagram type.
	 * @param diagramType is the diagram type we want the editor for.
	 * @param explorer is the parent component for the editor.
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static DiagramTypeEditor getTypeEditor(DiagramType diagramType, Component explorer) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
        Class<? extends DiagramTypeEditor> editorClass = editorClasses.get(diagramType.getClass());
        Class<?>[] signature = {Component.class}; 
        Constructor<? extends DiagramTypeEditor> cons = editorClass.getConstructor(signature);
        Object[] args = {explorer};
        DiagramTypeEditor editor = cons.newInstance(args);
        return editor;
	}
}
