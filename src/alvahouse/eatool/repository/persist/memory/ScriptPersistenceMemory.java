/**
 * 
 */
package alvahouse.eatool.repository.persist.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alvahouse.eatool.repository.dto.scripting.ScriptDto;
import alvahouse.eatool.repository.persist.ScriptPersistence;
import alvahouse.eatool.repository.persist.StaleDataException;
import alvahouse.eatool.util.UUID;

/**
 * @author bruce_porteous
 *
 */
public class ScriptPersistenceMemory implements ScriptPersistence {

	private Map<UUID, ScriptDto> scripts = new HashMap<>();
	
	/**
	 * 
	 */
	public ScriptPersistenceMemory() {
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#dispose()
	 */
	@Override
	public void dispose() {
		scripts.clear();
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScript(alvahouse.eatool.util.UUID)
	 */
	@Override
	public ScriptDto getScript(UUID key) throws Exception {
		ScriptDto s = scripts.get(key);
		if(s == null) {
			throw new IllegalArgumentException("Script with key " + key + " not found in repository");
		}
		return s;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#addScript(alvahouse.eatool.repository.scripting.Script)
	 */
	@Override
	public String addScript(ScriptDto s) throws Exception {
	   	UUID key = s.getKey();
    	if(scripts.containsKey(key)) {
    		throw new IllegalArgumentException("Duplicate script -" + s.getName());
    	} 
    	
    	String version = s.getVersion().update(new UUID().asJsonId());
    	scripts.put(key, s);
    	return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#updateScript(alvahouse.eatool.repository.scripting.Script)
	 */
	@Override
	public String updateScript(ScriptDto s) throws Exception {
	   	UUID key = s.getKey();
	   	ScriptDto original = scripts.get(key);
	   	if(original == null) { 
    		throw new IllegalArgumentException("Can't update a script " + s.getName() + " not in the repository.");
    	} 
    	if(!s.getVersion().sameVersionAs(original.getVersion())) {
    		throw new StaleDataException("Unable to update script due to version mismatch");
    	}
    	String version = s.getVersion().update(new UUID().asJsonId());
    	scripts.put(key, s);
    	return version;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#deleteScript(alvahouse.eatool.util.UUID)
	 */
	@Override
	public void deleteScript(UUID key, String version) throws Exception {
		ScriptDto original = scripts.get(key);
    	if(original == null) {
    		throw new IllegalArgumentException("Can't delete script with key " + key + ": not in repository");
    	}
    	
    	if(!original.getVersion().getVersion().equals(version)) {
    		throw new StaleDataException("Unable to update script due to version mismatch");
    	}

    	scripts.remove(key);
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScripts()
	 */
	@Override
	public Collection<ScriptDto> getScripts() {
		List<ScriptDto> copy = new ArrayList<>(scripts.size());
		copy.addAll(scripts.values());
		return copy;
	}

	/* (non-Javadoc)
	 * @see alvahouse.eatool.repository.persist.ScriptPersistence#getScriptCount()
	 */
	@Override
	public int getScriptCount() {
		return scripts.size();
	}

}
