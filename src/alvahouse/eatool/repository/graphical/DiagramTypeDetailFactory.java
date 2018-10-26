/*
 * DiagramTypeDetailFactory.java
 * Project: EATool
 * Created on 02-Oct-2006
 *
 */
package alvahouse.eatool.repository.graphical;

import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.IXMLContentHandler;

/**
 * DiagramTypeDetailFactory is for factory classes to implement to deserialise
 * polymorphic versions of DiagramType.
 * 
 * @author rbp28668
 */
public interface DiagramTypeDetailFactory extends IXMLContentHandler {
    public void init(DiagramType diagramType, MetaModel metaModel);
}
