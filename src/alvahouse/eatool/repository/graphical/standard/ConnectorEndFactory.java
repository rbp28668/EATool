/**
 * 
 */
package alvahouse.eatool.repository.graphical.standard;

import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.dto.graphical.ConnectorEndArrowHeadDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndDto;
import alvahouse.eatool.repository.dto.graphical.ConnectorEndNullDto;

/**
 * Map ConnectorEnd DTOs to "normal" classes.
 * @author bruce_porteous
 *
 */
public class  ConnectorEndFactory {

	static Map<String, Factory> lookup = new HashMap<>();
	
	static {
		add(new NullConnectorEndFactory());
		add(new ConnectorEndArrowHeadFactory());
	}
	
	static void add(Factory factory) {
		lookup.put(factory.dtoClass().getCanonicalName(), factory);
	}
	
	public static ConnectorEnd fromDto(ConnectorEndDto dto) {
		Factory f = lookup.get(dto.getClass().getCanonicalName());
		if(f == null) {
			throw new IllegalArgumentException("Dto "  + dto.getClass().getCanonicalName() + " has no corresponding symbol");
		}
		return f.create(dto);
	}
	
	/**
	 *  Can't instantiate. 
	 */
	private ConnectorEndFactory() {
	}

	private interface Factory{
		Class<? extends ConnectorEndDto> dtoClass();
		ConnectorEnd create(ConnectorEndDto dto);
	}
	
	private static class NullConnectorEndFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public ConnectorEnd create(ConnectorEndDto dto) {
			return ConnectorEnd.getNullObject();
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends ConnectorEndDto> dtoClass() {
			return ConnectorEndNullDto.class;
		}
	}

	private static class ConnectorEndArrowHeadFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public ConnectorEnd create(ConnectorEndDto dto) {
			return new ConnectorEndArrowHead();
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends ConnectorEndDto> dtoClass() {
			return ConnectorEndArrowHeadDto.class;
		}
	}

	
}
