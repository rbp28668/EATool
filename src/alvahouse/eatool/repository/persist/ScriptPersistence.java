/**
 * 
 */
package alvahouse.eatool.repository.persist;

import java.util.Collection;

import alvahouse.eatool.repository.dto.scripting.ScriptDto;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public interface ScriptPersistence {

	/**
	 * Clears down the model.
	 */
	public void dispose() throws Exception;

	/**
	 * Looks up a script by UUID.
	 * 
	 * @param key is the key of the script to get
	 * @return the corresponding script, or null if not in the model
	 */
	public ScriptDto getScript(UUID key) throws Exception;

	/**
	 * adds a new script to the collection.
	 * 
	 * @param s is the script to add.
	 * @return the revision of the newly added script.
	 * @throws IllegalStateException if the script already exists in the model.
	 */
	public String addScript(ScriptDto s) throws Exception;

	/**
	 * Updates and existing script in the model.
	 * 
	 * @param s is the script to update.
	 * @return the new revision of the updated script.
	 * @throws IllegalStateException if the script already exists in the model.
	 */
	public String updateScript(ScriptDto s) throws Exception;

	/**
	 * Deletes an script keyed by UUID.
	 * 
	 * @param key is the key of the script to delete.
	 * @param version is the revision of the script to delete.
	 * @throws IllegalStateException if deleting a script not in the model.
	 */
	public void deleteScript(UUID key, String version) throws Exception;

	/**
	 * Gets an iterator that iterates though all the entites.
	 * 
	 * @return an iterator for the entities.
	 */
	public Collection<ScriptDto> getScripts() throws Exception;

	/**
	 * DEBUG only
	 * 
	 * @return
	 */
	public int getScriptCount() throws Exception;

}
