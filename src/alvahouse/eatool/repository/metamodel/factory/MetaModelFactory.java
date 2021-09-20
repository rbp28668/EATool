/**
 * 
 */
package alvahouse.eatool.repository.metamodel.factory;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class MetaModelFactory extends FactoryBase implements IXMLContentHandler {

	private final MetaModel metaModel;
	/**
	 * 
	 */
	public MetaModelFactory(MetaModel metaModel) {
		this.metaModel = metaModel;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String local, Attributes attrs) throws InputException {
        String attr = attrs.getValue("uuid");
        if(attr != null) {
        	try {
				metaModel.setKey(new UUID(attr));
			} catch (Exception e) {
				throw new InputException("Unable to set meta-model key",e);
			}
        }
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String local) throws InputException {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#characters(java.lang.String)
	 */
	@Override
	public void characters(String str) throws InputException {
	}

}
