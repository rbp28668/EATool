/**
 * 
 */
package alvahouse.eatool.repository.dto.metamodel;

import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "regexType")
@XmlAccessorType(XmlAccessType.NONE)
public class RegexpCheckedTypeDto extends ExtensibleMetaPropertyTypeDto {

	   private Pattern pattern = null;
	    private String defaultValue = "";
	    private int fieldLength;

	    /**
		 * @return the pattern
		 */
	    @JsonIgnore
		public Pattern getPattern() {
			return pattern;
		}
		/**
		 * @param pattern the pattern to set
		 */
		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}

		/**
		 * @return the pattern
		 */
		@XmlElement(name="pattern")
		@JsonProperty("pattern")
		public String getPatternJson() {
			return pattern.pattern();
		}
		/**
		 * @param pattern the pattern to set
		 */
		public void setPatternJson(String regex) {
			this.pattern = Pattern.compile(regex);
		}

		
		/**
		 * @return the defaultValue
		 */
		@XmlElement(name="defaultValue")
		public String getDefaultValue() {
			return defaultValue;
		}
		/**
		 * @param defaultValue the defaultValue to set
		 */
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		/**
		 * @return the fieldLength
		 */
		@XmlElement(name="length")
		public int getFieldLength() {
			return fieldLength;
		}
		/**
		 * @param fieldLength the fieldLength to set
		 */
		public void setFieldLength(int fieldLength) {
			this.fieldLength = fieldLength;
		}

}
