/*
 * DiagramViewerWindowFactory.java
 * Project: EATool
 * Created on 21-Oct-2006
 *
 */
package alvahouse.eatool.gui;

import alvahouse.eatool.Application;
import alvahouse.eatool.gui.WindowCoordinator.WindowFactory;
import alvahouse.eatool.repository.Repository;
import alvahouse.eatool.repository.graphical.Diagram;

/**
 * DiagramViewerWindowFactory is a base interface that should be implemented by all
 * WindowFactory-s that create any sort of diagram viewer.  This allows the diagram explorer
 * to treat all diagrams identically through this (and other) polymorphism.
 * 
 * @author rbp28668
 */
public interface DiagramViewerWindowFactory extends WindowFactory {

	/**
	 * Allows the window factory to be initialised with a given diagram and model.
	 * This will then produce diagram windows that view the given diagram.
	 * @param diagram is the diagram to produce windows for.
	 * @param model is the underlying model.
	 */
	public void init(Diagram diagram, Application app, Repository repository);

}
