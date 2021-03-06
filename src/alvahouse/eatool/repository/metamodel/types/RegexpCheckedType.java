/*
 * RegexpCheckedType.java
 * Project: EATool
 * Created on 10-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import alvahouse.eatool.repository.exception.InputException;
import alvahouse.eatool.util.ClassUtils;
import alvahouse.eatool.util.UUID;
import alvahouse.eatool.util.XMLWriter;

/** 
 * RegexpCheckedType
 * 
 * @author rbp28668
 */
public class RegexpCheckedType extends ExtensibleMetaPropertyType {

    private Pattern pattern = null;
    private String defaultValue = "";
    private int fieldLength;
    
    /**
     * 
     */
    public RegexpCheckedType() {
        super();
        fieldLength = super.getDisplayLength();
    }

    /**
     * @param key
     */
    public RegexpCheckedType(UUID key) {
        super(key);
        fieldLength = super.getDisplayLength();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.ExtensibleMetaPropertyType#writeXML(alvahouse.eatool.util.XMLWriter)
     */
    public void writeXML(XMLWriter out) throws IOException {
        out.startEntity(ClassUtils.baseClassNameOf(this));
        writeAttributesXML(out);
        out.textEntity("Pattern",pattern.pattern());
        out.textEntity("Default", defaultValue);
        out.textEntity("FieldLength",Integer.toString(fieldLength));
        out.stopEntity();
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#validate(java.lang.String)
     */
    public String validate(String value) throws IllegalArgumentException {
        Matcher matcher = pattern.matcher(value);
        if(!matcher.matches()){
            throw new IllegalArgumentException("Value does not match pattern " + pattern.pattern());
        }
        return value;
    }

    /* (non-Javadoc)
     * @see alvahouse.eatool.repository.metamodel.types.MetaPropertyType#initialise()
     */
    public String initialise() {
        return defaultValue;    
    }
    
    public String getPattern() {
        if(pattern == null){
            return ".*";
        }
        return pattern.pattern();
    }
    
    public void setPattern(String regexp) throws PatternSyntaxException {
        this.pattern = Pattern.compile(regexp);
    }

    public boolean isPatternValid(String regexp) {
        boolean isValid = true;
        try {
            Pattern.compile(regexp);
        } catch (PatternSyntaxException psx){
            isValid = false;
        }
        return isValid;
    }

    /**
     * Allow checking of a value to check if it matches a given pattern.
     * Used for dialogs to check validity of input before the pattern is set.
     * @param regexp is the regular expression pattern.
     * @param is the value to check.
     * @return
     */
    public boolean isValueValid(String regexp, String value) {
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        validate(defaultValue);
        this.defaultValue = defaultValue;
    }

    public int getDisplayLength() {
        return fieldLength;
    }
    /**
     * @param fieldLength
     */
    public void setFieldLength(int fieldLength) {
        this.fieldLength = fieldLength;
        
    }


    /* (non-Javadoc)
     * @see alvahouse.eatool.util.IXMLContentHandler#endElement(java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String local) throws InputException {
        if(local.equals("Pattern")){
            setPattern(getXMLValue());
        } else if(local.equals("Default")){
            setDefaultValue(getXMLValue());
        } else if(local.equals("FieldLength")){
            setFieldLength(Integer.parseInt(getXMLValue()));
        }
    }

 
 }
