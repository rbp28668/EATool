/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
@XmlSeeAlso({
		DiamondSymbolDto.class,
		RectangularSymbolDto.class,
		RoundedSymbolDto.class,
		CircularSymbolDto.class
		})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CircularSymbolDto.class, name = "CircularSymbol"),
    @JsonSubTypes.Type(value = RectangularSymbolDto.class, name = "RectangularSymbol"),
    @JsonSubTypes.Type(value = RoundedSymbolDto.class, name = "RoundedSymbol"),
    @JsonSubTypes.Type(value = DiamondSymbolDto.class, name = "DiamondSymbol")
    })
public abstract class SymbolDto extends TextualObjectDto {

	private int index;
	private UUID referencedItemKey;
	private UUID symbolTypeKey;

	/**
	 * 
	 */
	public SymbolDto() {
	}

	/**
	 * @return the index
	 */
	@XmlAttribute
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the referencedItemKey
	 */
	@JsonIgnore
	public UUID getReferencedItemKey() {
		return referencedItemKey;
	}

	/**
	 * @param referencedItemKey the referencedItemKey to set
	 */
	public void setReferencedItemKey(UUID referencedItemKey) {
		this.referencedItemKey = referencedItemKey;
	}

	/**
	 * @return the referencedItemKey
	 */
	@XmlElement(name="referencedItemKey")
	@JsonProperty("referencedItemKey")
	public String getReferencedItemKeyJson() {
		return referencedItemKey.asJsonId();
	}

	/**
	 * @param referencedItemKey the referencedItemKey to set
	 */
	public void setReferencedItemKeyJson(String referencedItemKey) {
		this.referencedItemKey = UUID.fromJsonId(referencedItemKey);
	}

	/**
	 * @return the symbolTypeKey
	 */
	@JsonIgnore
	public UUID getSymbolTypeKey() {
		return symbolTypeKey;
	}

	/**
	 * @param symbolTypeKey the symbolTypeKey to set
	 */
	public void setSymbolTypeKey(UUID symbolTypeKey) {
		this.symbolTypeKey = symbolTypeKey;
	}

	/**
	 * @return the symbolTypeKey
	 */
	@XmlElement(name="symbolTypeKey")
	@JsonProperty("symbolTypeKey")
	public String getSymbolTypeKeyJson() {
		return symbolTypeKey.asJsonId();
	}

	/**
	 * @param symbolTypeKey the symbolTypeKey to set
	 */
	public void setSymbolTypeKeyJson(String symbolTypeKey) {
		this.symbolTypeKey = UUID.fromJsonId(symbolTypeKey);
	}

	
	
}
