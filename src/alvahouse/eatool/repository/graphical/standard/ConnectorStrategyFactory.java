/**
 * 
 */
package alvahouse.eatool.repository.graphical.standard;

import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.dto.graphical.ConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.CubicConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.QuadraticConnectorStrategyDto;
import alvahouse.eatool.repository.dto.graphical.StraightConnectorStrategyDto;

/**
 * Map ConnectorStrategy DTOs to "normal" classes.
 * @author bruce_porteous
 *
 */
public class  ConnectorStrategyFactory {

	static Map<String, Factory> lookup = new HashMap<>();
	
	static {
		add(new StraightConnectorStrategyFactory());
		add(new QuadraticConnectorStrategyFactory());
		add(new CubicConnectorStrategyFactory());
	}
	
	static void add(Factory factory) {
		lookup.put(factory.dtoClass().getCanonicalName(), factory);
	}
	
	public static ConnectorStrategy fromDto(ConnectorStrategyDto dto) {
		Factory f = lookup.get(dto.getClass().getCanonicalName());
		if(f == null) {
			throw new IllegalArgumentException("Dto "  + dto.getClass().getCanonicalName() + " has no corresponding symbol");
		}
		return f.create(dto);
	}
	
	/**
	 *  Can't instantiate. 
	 */
	private ConnectorStrategyFactory() {
	}

	private interface Factory{
		Class<? extends ConnectorStrategyDto> dtoClass();
		ConnectorStrategy create(ConnectorStrategyDto dto);
	}
	
	private static class StraightConnectorStrategyFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public ConnectorStrategy create(ConnectorStrategyDto dto) {
			return new StraightConnectorStrategy();
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends ConnectorStrategyDto> dtoClass() {
			return StraightConnectorStrategyDto.class;
		}
	}

	private static class QuadraticConnectorStrategyFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public ConnectorStrategy create(ConnectorStrategyDto dto) {
			return new QuadraticConnectorStrategy();
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends ConnectorStrategyDto> dtoClass() {
			return QuadraticConnectorStrategyDto.class;
		}
	}

	private static class CubicConnectorStrategyFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public ConnectorStrategy create(ConnectorStrategyDto dto) {
			return new CubicConnectorStrategy();
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends ConnectorStrategyDto> dtoClass() {
			return CubicConnectorStrategyDto.class;
		}
	}

	
}
