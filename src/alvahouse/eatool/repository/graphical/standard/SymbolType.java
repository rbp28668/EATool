package alvahouse.eatool.repository.graphical.standard;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import alvahouse.eatool.repository.base.FactoryBase;
import alvahouse.eatool.repository.base.KeyedItem;
import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.dto.graphical.SymbolTypeDto;
import alvahouse.eatool.repository.exception.LogicException;
import alvahouse.eatool.repository.metamodel.MetaEntity;
import alvahouse.eatool.repository.metamodel.MetaEntityProxy;
import alvahouse.eatool.repository.metamodel.MetaModel;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/**
 * SymbolType determines the symbol to use to represent a given type
 * of entity. This acts as a factory class for symbols for a given meta-entity.
 * @link {ConnectorType} is the analogue of this class for connectors.
 * @author bruce.porteous
 *
 */
public class SymbolType extends NamedRepositoryItem implements TextObjectSettings{

	
	/** The class used to display the symbol (a subclass of BasicSymbol) */
	private Class<? extends Symbol> symbolClass;
	
	/** Which MetaEntity this symbol type represents */
	private MetaEntityProxy represents = new MetaEntityProxy();

	/** Default background colour for symbols of this type */
	private Color backColour = Color.lightGray;

	/** Default text colour for symbols of this type */
	private Color textColour = Color.black;

	/** Default border colour for symbols of this type */
	private Color borderColour = Color.black;

	/** Default font for symbols of this type */
	private Font font = new Font("SansSerif", Font.PLAIN,10);


	
	
	/**
	 * Constructor for SymbolType.
	 * @param uuid is the key for this SymbolType.
	 */
	public SymbolType(UUID uuid) {
		super(uuid);
	}
	
	/**
	 * Creates a SymbolType for a given MetaEntity.
	 * @param uuid is the key for this SymbolType.
	 * @param represents is the MetaEntity this SymbolType is used to represent.  
	 * @param symbolClass is the Class of the symbols used to represent entities of the 
	 * type corresponding to the given MetaEntity.
	 * @param name is the name of the symbol type.
	 */
	public SymbolType(UUID uuid, MetaEntity represents, Class<? extends Symbol> symbolClass, String name) {
		super(uuid);

		if(represents == null) {
			throw new NullPointerException("Null value for represented meta-entity");
		}

		if(symbolClass == null) {
			throw new NullPointerException("Null value for symbol class");
		}
		
		this.represents.set(represents);
		this.symbolClass = symbolClass;
		setName(name);
	}

	/**
	 * constructor for creating a SymbolType for displaying MetaEntities
	 * rather than Entities.
	 * @param symbolClass is the Class of the Symbol to use for display.
	 * @param name is the name this SymbolType should be known by.
	 */
	 public SymbolType(Class<? extends Symbol> symbolClass, String name){
		super(new UUID());

		if(symbolClass == null) {
			throw new NullPointerException("Null value for symbol class");
		}
		
		this.represents = null;
		this.symbolClass = symbolClass;
		setName(name);
	}
	
	/**
	 * @param stdto
	 */
	@SuppressWarnings("unchecked")
	public SymbolType(SymbolTypeDto stdto) throws Exception {
		super(stdto);
		this.represents.setKey(stdto.getMetaEntityKey());
		this.symbolClass = (Class<? extends Symbol>)Class.forName(stdto.getSymbolClass());
		this.textColour = stdto.getTextColour();
		this.backColour = stdto.getBackColour();
		this.borderColour = stdto.getBorderColour();
		this.font = stdto.getFont();
	}

	public SymbolTypeDto toDto() {
		SymbolTypeDto dto = new SymbolTypeDto();
		copyTo(dto);
		return dto;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		SymbolType copy = new SymbolType(getKey());
		cloneTo(copy);
		return copy;
	}
	
	
	/**
	 * updateFromCopy updates this symbol type from a copy.  It's the inverse
	 * of clone and used when updating from an edited copy.
	 * @param copy is the SymbolType to update from.
	 */
	public void updateFromCopy(SymbolType copy){
		copy.cloneTo(this);
	}
	
	/**
	 * Method newSymbol.
	 * @param target is the Object to be associated with this symbol.
	 * @param x is the X coordinate of the symbol's initial position.
	 * @param y is the Y coordinate of the symbol's initial position.
	 * @return the new BasicSymbol
	 * @throws LogicException - if the symbol can't be created.
	 * @throws NullPointerException - if e is null.
	 */
	public AbstractSymbol newSymbol(KeyedItem target, float x, float y)
	throws LogicException, NullPointerException {
		
		if(target == null){
			throw new NullPointerException("Trying to create a symbol attached to a null entity");
		}
		
		
		try {
			AbstractSymbol symbol = (AbstractSymbol)symbolClass.newInstance();
			symbol.setType(this);
			symbol.setItem(target);
			symbol.setPosition(x,y);
			symbol.setBackColour(backColour);
			symbol.setTextColour(textColour);
			symbol.setBorderColour(borderColour);
			symbol.setFont(font);
			
			return symbol;
		} catch (Throwable t) {
			throw new LogicException("Unable to create symbol " + getName(), t);
		}
	}

    /**
     * Creates a new symbol when much of its data is known.
     * @param key is the key for the symbol.
     * @param target is the target object the symbol represents.
     * @param x is the symbols X position in the diagram.
     * @param y is the symbol's Y position in the diagram.
     * @param width is the symbol's width.
     * @param height is the symbol's height.
     * @return the new Symbol.
     * @throws LogicException if the symbol creation fails.
     */
    public Symbol newSymbol(UUID key, KeyedItem target, float x, float y, float width, float height) throws LogicException {
		try {
			AbstractSymbol symbol = (AbstractSymbol)symbolClass.newInstance();
			symbol.setType(this);
			symbol.setKey(key);
			symbol.setItem(target);
			symbol.setPosition(x,y);
			symbol.setSize(width,height);
			symbol.setBackColour(backColour);
			symbol.setTextColour(textColour);
			symbol.setBorderColour(borderColour);
			symbol.setFont(font);
			
			return symbol;
		} catch (Throwable t) {
			throw new LogicException("Unable to create symbol " + getName(), t);
		}
    }
	
	/**
	 * Returns the name - if there is a name given to this symbol type then
	 * use it, otherwise, use the meta-entities name.
	 * @return String
	 */
	public String getName(MetaModel mm) throws Exception {
		String name = super.getName();
		if(name != null) {
			return name;
		} else {
			return represents.get(mm).getName();
		}
	}

	/**
	 * Returns the represents.
	 * @return MetaEntity
	 */
	public MetaEntity getRepresents(MetaModel mm) throws Exception {
		return represents.get(mm);
	}

	/**
	 * Gets the key of the meta entity this binds to.
	 * @return
	 */
	public UUID getRepresentsKey() {
		return represents.getKey();
	}
	
	/**
	 * Returns the Class of the SymbolClass used to render this symbol type.
	 * @return Class
	 */
	public Class<? extends Symbol> getRenderClass() {
		return symbolClass;
	}

	/**
	 * Sets the represents.
	 * @param represents The represents to set
	 */
	public void setRepresents(MetaEntity represents) {
		if(represents == null) {
			throw new NullPointerException("Null value for represented meta-entity");
		}
		this.represents.set(represents);
		String name = super.getName();
		if(name == null || name.trim().isEmpty()){
			setName(represents.getName());
		}
	}

	/**
	 * Sets the class used to render this symnbol type.
	 * @param symbolClass The symbolClass to set
	 */
	public void setRenderClass(Class<? extends Symbol> symbolClass) {
		if(symbolClass == null) {
			throw new NullPointerException("Null value for symbol class");
		}
		
		if(!AbstractSymbol.class.isAssignableFrom(symbolClass)) {
			throw new IllegalArgumentException("Symbol Types's Rendering class must be Symbol or a subclass of Symbol");
		}
		
		this.symbolClass = symbolClass;
	}

	/**
	 * Writes the symbol type out as XML
	 * @param out is the XMLWriterDirect to write the XML to
	 * @throws IOException in the event of an io error
	 */
	public void writeXML(XMLWriter out) throws IOException {
		out.startEntity("SymbolType");
		writeAttributesXML(out);
		out.addAttribute("represents", represents.getKey().toString());
		out.addAttribute("renderClass", symbolClass.getName());
		FactoryBase.writeColour(out,"DefaultTextColour",textColour);
		FactoryBase.writeColour(out,"DefaultBackColour",backColour);
		FactoryBase.writeColour(out,"DefaultBorderColour",borderColour);
		FactoryBase.writeFont(out,"DefaultFont",font);
		out.stopEntity();
	}

	
	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getBackColour()
	 */
	public Color getBackColour() {
		return backColour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getTextColour()
	 */
	public Color getTextColour() {
		return textColour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getBorderColour()
	 */
	public Color getBorderColour() {
		return borderColour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setBackColour(java.awt.Color)
	 */
	public void setBackColour(Color colour) {
		if(colour == null) {
			throw new NullPointerException("Setting null colour");
		}
		backColour = colour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setTextColour(java.awt.Color)
	 */
	public void setTextColour(Color colour) {
		if(colour == null) {
			throw new NullPointerException("Setting null colour");
		}
		textColour = colour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setBorderColour(java.awt.Color)
	 */
	public void setBorderColour(Color colour) {
		if(colour == null) {
			throw new NullPointerException("Setting null colour");
		}
		borderColour = colour;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#getFont()
	 */
	public Font getFont() {
		return font;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.gui.graphical.TextObjectSettings#setFont(java.awt.Font)
	 */
	public void setFont(Font font) {
		if(font == null) {
			throw new NullPointerException("Setting null font on symbol type");
		}
		this.font = font;
	}

	protected void cloneTo(SymbolType copy){
		super.cloneTo(copy);
		copy.symbolClass = symbolClass;
		copy.represents = represents;
		copy.backColour = cloneColour(backColour);
		copy.textColour = cloneColour(textColour);
		copy.borderColour = cloneColour(borderColour);
		copy.font = new Font(font.getName(), font.getStyle(), font.getSize());
	}
	
	/**
	 * Convenience method for cloning a colour.
	 * @param src is the colour to be cloned.
	 * @return a new copy of src.
	 */
	private Color cloneColour(Color src){
		return new Color(src.getRed(), src.getGreen(), src.getBlue(), src.getAlpha());
	}

	protected void copyTo(SymbolTypeDto dto) {
		super.copyTo(dto);
		dto.setMetaEntityKey(getKey());
		dto.setSymbolClass(symbolClass.getName());
		dto.setTextColour(textColour);
		dto.setBackColour(backColour);
		dto.setBorderColour(borderColour);
		dto.setFont(font);
	}
}
