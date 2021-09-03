/**
 * 
 */
package alvahouse.eatool.repository.dto;

/**
 * Interface to allow generic revision handling in DTOs.
 * @author bruce_porteous
 *
 */
public interface VersionedDto {

	public VersionDto getVersion();
	
	
	/**
	 * Revision as set/returned by persistence mechanism.
	 * @return
	 */
	public String getRev();
	
	public void setRev(String rev);
}
