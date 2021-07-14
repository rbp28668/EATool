/**
 * 
 */
package alvahouse.eatool.repository.graphical.symbols;

import java.util.HashMap;
import java.util.Map;

import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.dto.graphical.CircularSymbolDto;
import alvahouse.eatool.repository.dto.graphical.DiamondSymbolDto;
import alvahouse.eatool.repository.dto.graphical.RectangularSymbolDto;
import alvahouse.eatool.repository.dto.graphical.RoundedSymbolDto;
import alvahouse.eatool.repository.dto.graphical.SymbolDto;
import alvahouse.eatool.repository.graphical.standard.Symbol;
import alvahouse.eatool.repository.graphical.standard.SymbolType;

/**
 * @author bruce_porteous
 *
 */
public class SymbolFactory {

	static Map<String, Factory> lookup = new HashMap<>();
	
	static {
		add(new CircularSymbolFactory());
		add(new DiamondSymbolFactory());
		add(new RectangularSymbolFactory());
		add(new RoundedSymbolFactory());
	}
	
	static void add(Factory factory) {
		lookup.put(factory.dtoClass().getCanonicalName(), factory);
	}
	
	public static Symbol fromDto(KeyedItem item, SymbolType type, SymbolDto dto) {
		Factory f = lookup.get(dto.getClass().getCanonicalName());
		if(f == null) {
			throw new IllegalArgumentException("Dto "  + dto.getClass().getCanonicalName() + " has no corresponding symbol");
		}
		return f.create(item, type, dto);
	}
	
	/**
	 *  Can't instantiate. 
	 */
	private SymbolFactory() {
	}

	private interface Factory{
		Class<? extends SymbolDto> dtoClass();
		Symbol create(KeyedItem item, SymbolType type, SymbolDto dto);
	}
	
	private static class CircularSymbolFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public Symbol create(KeyedItem item, SymbolType type, SymbolDto dto) {
			CircularSymbolDto csd = (CircularSymbolDto)dto;
			return new CircularSymbol(item, type, csd);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends SymbolDto> dtoClass() {
			return CircularSymbolDto.class;
		}
	}

	private static class DiamondSymbolFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public Symbol create(KeyedItem item, SymbolType type, SymbolDto dto) {
			DiamondSymbolDto dsd = (DiamondSymbolDto)dto;
			return new DiamondSymbol(item, type, dsd);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends SymbolDto> dtoClass() {
			return DiamondSymbolDto.class;
		}
	}

	private static class RectangularSymbolFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public Symbol create(KeyedItem item, SymbolType type, SymbolDto dto) {
			RectangularSymbolDto rsd = (RectangularSymbolDto)dto;
			return new RectangularSymbol(item, type, rsd);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends SymbolDto> dtoClass() {
			return RectangularSymbolDto.class;
		}
	}

	private static class RoundedSymbolFactory implements Factory {

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#create(alvahouse.eatool.repository.dto.graphical.SymbolDto)
		 */
		@Override
		public Symbol create(KeyedItem item, SymbolType type, SymbolDto dto) {
			RoundedSymbolDto csd = (RoundedSymbolDto)dto;
			return new RoundedSymbol(item, type, csd);
		}

		/* (non-Javadoc)
		 * @see alvahouse.eatool.repository.graphical.symbols.SymbolFactory.Factory#dtoClass()
		 */
		@Override
		public Class<? extends SymbolDto> dtoClass() {
			return RoundedSymbolDto.class;
		}
	}

	
}
