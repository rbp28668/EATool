/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import alvahouse.eatool.repository.graphical.standard.DimensionFloat;
import alvahouse.eatool.util.UUID;

/**
 * NOT USED - time bars are created as needed - TimeDiagramEntryDto manages the state of each entry in the time diagram.
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "timeBar")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "propertyKeyJson","type","x","y","width","height","datesJson"})
public class TimeBarDto {

	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
    private UUID propertyKey;
    private TypeEntryDto type;
    private float x;
    private float y;
	private DimensionFloat size = new DimensionFloat(50,20);
    private Date[] dates;
	/**
	 * 
	 */
	public TimeBarDto() {
	}
	
	/**
	 * @return the propertyKey
	 */
	@JsonIgnore
	public UUID getPropertyKey() {
		return propertyKey;
	}
	/**
	 * @param propertyKey the propertyKey to set
	 */
	public void setPropertyKey(UUID propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * @return the propertyKey
	 */
	@XmlElement(name="propertyKey")
	@JsonProperty("propertyKey")
	public String getPropertyKeyJson() {
		return propertyKey.asJsonId();
	}
	
	/**
	 * @param propertyKey the propertyKey to set
	 */
	public void setPropertyKeyJson(String propertyKey) {
		this.propertyKey = UUID.fromJsonId(propertyKey);
	}

	/**
	 * @return the type
	 */
	@XmlElement
	public TypeEntryDto getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(TypeEntryDto type) {
		this.type = type;
	}
	/**
	 * @return the x
	 */
	@XmlElement
	public float getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	@XmlElement
	public float getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * @return the size
	 */
	@XmlElement
	public float getWidth() {
		return size.getWidth();
	}
	/**
	 * @param size the size to set
	 */
	public void setWidth(float width) {
		this.size.setWidth(width);
	}

	/**
	 * @return the size
	 */
	@XmlElement
	public float getHeight() {
		return size.getHeight();
	}
	/**
	 * @param size the size to set
	 */
	public void setHeight(float width) {
		this.size.setHeight(width);
	}

	/**
	 * @return the dates
	 */
	@JsonIgnore
	public Date[] getDates() {
		return dates;
	}
	/**
	 * @param dates the dates to set
	 */
	public void setDates(Date[] dates) {
		this.dates = dates;
	}
	
	/**
	 * @return the dates
	 */
	@XmlElementWrapper(name="dates")
	@XmlElement(name="date")
	public List<String> getDatesJson() {
		if(dates == null) {
			return new ArrayList<String>(0);
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
		fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
		List<String> datesAsString = new ArrayList<>(dates.length);
		for(Date d : dates) {
			String asString = fmt.format(d);
			datesAsString.add(asString);
		}
		return datesAsString;
	}
	
	/**
	 * @param dates the dates to set
	 * @throws Exception 
	 */
	public void setDatesJson(List<String> dates) throws Exception {
		if(dates == null) {
			this.dates = new Date[0];
		}
		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
		
		this.dates = new Date[dates.size()];
		int idx = 0;
		for(String date : dates) {
			fmt.setTimeZone(TimeZone.getTimeZone("UTC")); // in loop as parsing can reset
			Date d = fmt.parse(date);
			this.dates[idx++] = d;
		}
	}
	

}
