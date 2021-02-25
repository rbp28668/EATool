/**
 * 
 */
package alvahouse.eatool.repository;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.IXMLContentHandler;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class RepositoryFactory extends FactoryBase implements IXMLContentHandler {

	private final Repository repository;
	/**
	 * 
	 */
	public RepositoryFactory(Repository repository) {
		this.repository = repository;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.util.IXMLContentHandler#startElement(java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String local, Attributes attrs) throws InputException {
        String attr = attrs.getValue("uuid");
        if(attr != null) {
        	repository.setKey(new UUID(attr));
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
