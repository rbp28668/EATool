/*
 * Version.java
 * Project: EATool
 * Created on 25 Nov 2007
 *
 */
package alvahouse.eatool.repository.version;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.Attributes;

import alvahouse.eatool.repository.dto.VersionDto;
import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.XMLWriter;

/**
 * Version provides a concrete implementation of Versionable which versionable
 * objects can delegate to.
 * 
 * @author rbp28668
 */
public class VersionImpl implements Version {

    private final static Date START = new Date(0);
    private final static String ANON = "";
    
    private Date createDate = START;
    private String createUser = ANON;
    private Date modifyDate = START;
    private String modifyUser = ANON;
    private String version = null;
    private String originalVersion = null;
    
    private final static String TIME_FORMAT_STR = "yyyyMMddHHmmssSSS";
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_STR);
    
    
    public VersionImpl() {
    }

    /**
	 * @param dao
	 */
	public VersionImpl(VersionDto dto) {
		fromDto(dto);
	}
	
	public VersionDto toDto() {
		VersionDto dao = new VersionDto();
		dao.setCreateDate(createDate);
		dao.setCreateUser(createUser);
		dao.setModifyDate(modifyDate);
		dao.setModifyUser(modifyUser);
		dao.setVersion(version);
		dao.setOriginalVersion(originalVersion);
		return dao;
	}

	public void fromDto(VersionDto dto) {
		createDate = dto.getCreateDate();
		createUser = dto.getCreateUser();
		modifyDate = dto.getModifyDate();
		modifyUser = dto.getModifyUser();
		version = dto.getVersion();
		originalVersion = dto.getOriginalVersion();
	}
	/* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getCreateDate()
     */
    public Date getCreateDate() {
        return createDate;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#setCreateDate(java.util.Date)
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getCreateUser()
     */
    public String getCreateUser() {
        return createUser;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#setCreateUser(java.lang.String)
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getModifyDate()
     */
    public Date getModifyDate() {
        return modifyDate;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#setModifyDate(java.util.Date)
     */
    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getModifyUser()
     */
    public String getModifyUser() {
        return modifyUser;
    }
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#setModifyUser(java.lang.String)
     */
    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#getVersion()
     */
    public String getVersion() {
        return version;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#setVersion(alvahouse.eatool.util.UUID)
     */
    public void setVersion(String version) {
        this.version = version;
    }
    
	/**
     * @return the originalVersion
     */
    public String getOriginalVersion() {
        return originalVersion;
    }

    /**
     * @param originalVersion the originalVersion to set
     */
    public void setOriginalVersion(String originalVersion) {
        this.originalVersion = originalVersion;
    }
    
    
    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#createBy(java.lang.String)
     */
    public void createBy(String user){
        createUser = modifyUser = user;
        createDate = modifyDate = new Date();
        originalVersion = null;
        version = null;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.version.Versionable#modifyBy(java.lang.String)
     */
    public void modifyBy(String user){
        modifyUser = user;
        modifyDate = new Date();
    }

    
    /* (non-Javadoc)
	 * @see alvahouse.eatool.repository.version.Version#update(java.lang.String)
	 */
	@Override
	public void update(String version) {
		if(version != null) {
			this.originalVersion = this.version;
			this.version = version;
		}
	}
	

	public void cloneTo(VersionImpl other){
        other.createUser = createUser;
        other.createDate = new Date(createDate.getTime());
        other.modifyUser = modifyUser;
        other.modifyDate = new Date(modifyDate.getTime());
        other.version = version;
        other.originalVersion = originalVersion;
    }
    
    /**
     * Writes the Version information out as XML
     * @param out is the XMLWriterDirect to write the XML to
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity("Version");
        out.addAttribute("createUser",getCreateUser());
        out.addAttribute("createDate",TIME_FORMAT.format(getCreateDate()));
        out.addAttribute("modifyUser",getModifyUser());
        out.addAttribute("modifyDate",TIME_FORMAT.format(getModifyDate()));
        out.addAttribute("version", (version == null) ? "" : version.toString());    
        out.addAttribute("originalVersion", (originalVersion == null) ? "" : originalVersion.toString());    
        out.stopEntity();
    }
    
    public static void readXML(Attributes attrs, Versionable versionable) throws InputException {
        Version version = versionable.getVersion();
        String createUser = attrs.getValue("createUser");
        if(createUser == null){
            throw new InputException("Missing createUser in version");
        }
        
        String createDateStr = attrs.getValue("createDate");
        String modifyUser = attrs.getValue("modifyUser");
        String modifyDateStr = attrs.getValue("modifyDate");
        String verStr = attrs.getValue("version");    
        String origVerStr = attrs.getValue("originalVersion");
        Date createDate;
        Date modifyDate;
        try {
            createDate = VersionImpl.TIME_FORMAT.parse(createDateStr);
            modifyDate = VersionImpl.TIME_FORMAT.parse(modifyDateStr);
        } catch (ParseException e) {
            throw new InputException("Invalid date/time in version");
        }
        
        version.setCreateDate(createDate);
        version.setCreateUser(createUser);
        version.setModifyDate(modifyDate);
        version.setModifyUser(modifyUser);
        version.setVersion(verStr);
        version.setOriginalVersion(origVerStr);
    }


}
