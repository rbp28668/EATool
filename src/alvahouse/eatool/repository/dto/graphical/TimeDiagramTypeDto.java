/**
 * 
 */
package alvahouse.eatool.repository.dto.graphical;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * DTO for TimeDiagramType
 * @author bruce_porteous
 *
 */
@XmlRootElement(name = "timeDiagramType")
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type_name")
public class TimeDiagramTypeDto extends DiagramTypeDto {

	private List<TypeEntryDto> targets;

	/**
	 * 
	 */
	public TimeDiagramTypeDto() {
	}

	/**
	 * @return the targets
	 */
	@XmlElementWrapper(name = "targets")
	@XmlElementRef
	public List<TypeEntryDto> getTargets() {
		if (targets == null) {
			targets = new LinkedList<>();
		}
		return targets;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(List<TypeEntryDto> targets) {
		this.targets = targets;
	}

}
