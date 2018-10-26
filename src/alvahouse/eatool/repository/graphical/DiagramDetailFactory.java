/*
 * DiagramDetailFactory.java
 * Project: EATool
 * Created on 01-Oct-2006
 *
 */
package alvahouse.eatool.repository.graphical;

import alvahouse.eatool.repository.images.Images;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.repository.model.Model;
import alvahouse.eatool.util.IXMLContentHandler;

/**
 * DiagramDetailFactory is a content handler for diagram type specific detail.
 * 
 * @author rbp28668
 */
public interface DiagramDetailFactory extends IXMLContentHandler {

	/**
	 * Initialises the detail factory.  Kept seperate from the constructor to 
	 * allow re-initialisation which allows these to be cached and re-used to
	 * de-serialise many diagrams.
	 * @param diagram is the diagram being de-serialised.
	 * @param type is the type of the diagram.
	 * @param metaModel is the parent meta-model for possible user objects.
	 * @param model is the parent model for possible user objects.
	 */
	public void init(Diagram diagram, DiagramType type,  MetaModel metaModel, Model model, Images images );

}
