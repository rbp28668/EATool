/*
 * MetaPropertyType.java
 *
 * Created on 12 January 2002, 22:12
 */

package alvahouse.eatool.repository.metamodel.types;
import java.awt.Component;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import alvahouse.eatool.repository.base.NamedRepositoryItem;
import alvahouse.eatool.repository.version.Version;
import alvahouse.eatool.repository.version.VersionImpl;
import alvahouse.eatool.repository.version.Versionable;
import alvahouse.eatool.util.UUID;

/* Use basic Java types
 * boolean
 * char
 * byte (-128 to 127)
 * short
 * int
 * long
 * float
 * double
 * 
 * And add some obvious ones:
 * string
 * uuid
 * date
 * time
 * timestamp
 * url
 */
/**
 * MetaProperty type models the data type of a MetaProperty (and hence provides the data
 * type of properties created from this MetaProperty).
 * @author  rbp28668
 */
public abstract class MetaPropertyType extends NamedRepositoryItem {


    /** Creates new MetaPropertyType - note this is implemented to allow
     * Class.newInstance to be used to create new types. At other times
     * the keys should be known. */
    public MetaPropertyType() {
        this(new UUID());
    }
    
    public MetaPropertyType(UUID key){
        super(key);
    }

    /**
     * Gets the name of the data type for this MetaProperty.
     * @return  String with the type name
     * */    
    public String getTypeName() {
        return getName();
    }
    
    /**
     * Sets the name of the data type.
     * @param typeName is the name of the data type
    */    
    protected void setTypeName(String typeName) {
        setName(typeName);
    }
    
    /** This allows a property to validate it's contents based on
     * it's meta-property type.
     * @param value is the value to validate
     * @return a canonical representation of the value
     * @throws IllegalArgumentException  */    
    public abstract String validate(String value) throws IllegalArgumentException;

    /**
     * Gets a comparable value from the initial string.  By default this just
     * returns the original string but can be over-ridden to allow sensible
     * numeric, date, time etc. comparisons.
     * @param value is the string value to convert.
     * @return a Comparable object. 
     * @throws IllegalArgumentException
     */
    public Comparable<?>  getComparable(String value) throws IllegalArgumentException{
        return value;
    }
    

    /**
     * initialises the value for this meta type
     * @param buff is the buffer 
     * @return an appropriately initialised string for this type
     */    
    public abstract String initialise();

    /**
     * Get an appropriate renderer for this type. Over-ride for any
     * thing different such as checkboxes for boolean, calendars,
     * etc. The default behaviour is to get the editor, but disable it.
     * @param value is the initial value to edit.
    * @return a suitable JComponent for this display or null to use a default.
    */
   public Component getRenderer(Object value){
       JLabel field = new JLabel((String)value);
       return field;
   }

     /**
      * Get an appropriate editor for this type. Over-ride for any
      * thing different such as checkboxes for boolean, calendars,
      * etc. Note that if you over-ride this you should also over-ride
      * <code>getEditValue(Component)</code>
      * @param value is the initial value to edit.
     * @return a suitable JComponent for this display or null to use a default.
     */
    public Component getEditor(Object value){
        
        JTextField field = new JTextField((String)value);
        field.setColumns(getDisplayLength());
        return field;
    }
    
    /**
     * Gets the edited value from the editor.  The component should be the same
     * as supplied by getEditor(String) which must be over-ridden if this method
     * is over-ridden.
     * @param component
     * @return
     */
    public String getEditValue(Component component){
        if(!(component instanceof JTextField)){
            throw new IllegalArgumentException("Cannot get edit value from a " + component.getClass().getName());
        }
        JTextField field = (JTextField)component;
        return field.getText();
    }
    
    /**
     * Gets the desired display length (default 40 chars).
     * @return the desired display length.
     */
    public int getDisplayLength(){
        return 40;
    }

    
    /*=================================================================*/
    // Built in type classes:
    /*=================================================================*/
    
	static class TypeBoolean extends MetaPropertyType {
        public TypeBoolean() {
            super(Keys.TYPE_BOOLEAN);
            super.setTypeName("boolean");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            if(value.compareToIgnoreCase("true") == 0)
                return new String("true");
            if(value.compareToIgnoreCase("false") == 0)
                return new String("false");
            throw new IllegalArgumentException("Invalid boolean");
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            if(value.compareToIgnoreCase("true") == 0)
                return Boolean.TRUE;
            if(value.compareToIgnoreCase("false") == 0)
                return Boolean.FALSE;
            throw new IllegalArgumentException("Invalid boolean");
        }
        
        public String initialise() {
            return new String("false");
        }
        
        public Component getRenderer(Object value){
            JCheckBox check = new JCheckBox();
            check.setSelected(value.equals("true"));
            check.setEnabled(false);
            return check;
        }
        
        public Component getEditor(Object value){
            JCheckBox check = new JCheckBox();
            check.setSelected(value.equals("true"));
            return check;
        }
        
        public String getEditValue(Component component){
            if(!(component instanceof JCheckBox)){
                throw new IllegalArgumentException("Cannot get boolean edit value from a " + component.getClass().getName());
            }
            JCheckBox field = (JCheckBox)component;
            boolean isSelected = field.isSelected();
            return isSelected ? "true" : "false";
        }

        public Component getRenderer(String value){
            JCheckBox field = new JCheckBox();
            field.setSelected(value.compareToIgnoreCase("true") == 0);
            field.setEnabled(false);
            return field;
        }
        
    }
    
    static class TypeChar extends MetaPropertyType {
        public TypeChar() {
            super(Keys.TYPE_CHAR);
            super.setTypeName("char");
        }
        public String validate(String value) throws IllegalArgumentException {
            if(value.length()<1)
                throw new IllegalArgumentException("Invalid char");
            return value.substring(1,1);
        }
        public String initialise() {
            return new String("?");
        }
        public int getDisplayLength(){
            return 1;
        }
    }
    static class TypeByte extends MetaPropertyType {
        public TypeByte() {
            super(Keys.TYPE_BYTE);
            super.setTypeName("byte");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                return Byte.decode(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal byte");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                return Byte.decode(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal byte");
            }
        }

        public String initialise() {
            return new String("0");
        }
        public int getDisplayLength(){
            return 4;
        }

    }
    
    static class TypeShort extends MetaPropertyType {
        public TypeShort() {
            super(Keys.TYPE_SHORT);
            super.setTypeName("short");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                return Short.decode(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal short");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                return Short.decode(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal short");
            }
        }
        
        public String initialise() {
            return new String("0");
        }

        public int getDisplayLength(){
            return 6;
        }
    }
    
    static class TypeInt extends MetaPropertyType {
        public TypeInt() {
            super(Keys.TYPE_INT);
            super.setTypeName("int");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                return Integer.decode(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal int");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                return Integer.decode(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal int");
            }
        }
        
        
        public String initialise() {
            return new String("0");
        }
        
        public int getDisplayLength(){
            return 11;
        }

    }
    
    static class TypeLong extends MetaPropertyType {
        public TypeLong() {
            super(Keys.TYPE_LONG);
            super.setTypeName("long");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                return Long.decode(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal long");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                return Long.decode(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal long");
            }
        }
        
        
        public String initialise() {
            return new String("0");
        }
        
        public int getDisplayLength(){
            return 22;
        }

    }
    
    static class TypeFloat extends MetaPropertyType {
        public TypeFloat() {
            super(Keys.TYPE_FLOAT);
            super.setTypeName("float");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                return Float.valueOf(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal float");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                return Float.valueOf(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal float");
            }
        }
        
        
        public String initialise() {
            return new String("0.0");
        }
        
        public int getDisplayLength(){
            return 24;
        }

    }
    
    static class TypeDouble extends MetaPropertyType {
        public TypeDouble() {
            super(Keys.TYPE_DOUBLE);
            super.setTypeName("double");
        }
        public String validate(String value) throws IllegalArgumentException {
            try {
                return Double.valueOf(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal double");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                return Double.valueOf(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal double");
            }
        }
        
        
        public String initialise() {
            return new String("0.0");
        }
        
        public int getDisplayLength(){
            return 26;
        }

    }
    static class TypeString extends MetaPropertyType {
        public TypeString() {
            super(Keys.TYPE_STRING);
            super.setTypeName("string");
        }
        public String validate(String value) throws IllegalArgumentException {
            return value;
        }
        public String initialise() {
            return new String("");
        }
    }
    static class TypeUUID extends MetaPropertyType {
        public TypeUUID() {
            super(Keys.TYPE_UUID);
            super.setTypeName("uuid");
        }
        public String validate(String value) throws IllegalArgumentException {
            UUID uuid = new UUID(value);
            return uuid.toString();
        }
        public String initialise() {
            UUID uuid = new UUID();
            return uuid.toString();
        }
        public int getDisplayLength(){
            return 32;
        }

    }
    static class TypeDate extends MetaPropertyType {
        
        private DateFormat dfo = DateFormat.getDateInstance();
        
        public TypeDate() {
            super(Keys.TYPE_DATE);
            super.setTypeName("date");
        }

        public String validate(String value) throws IllegalArgumentException {
            try {
                Date d = dfo.parse(value);
                if(d == null)
                    throw new Exception();
                return dfo.format(d);
            }
            catch(Exception e) {
                throw new IllegalArgumentException("Invalid date");
            }
        }
        
        public String initialise() {
            Date d = new Date(); // now
            return dfo.format(d);
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                Date d = dfo.parse(value);
                if(d == null)
                    throw new Exception();
                return d;
            }
            catch(Exception e) {
                throw new IllegalArgumentException("Invalid date");
            }
        }
        
    }
    
    static class TypeTime extends MetaPropertyType {

        private DateFormat dfo = DateFormat.getTimeInstance();
        
        public TypeTime() {
            super(Keys.TYPE_TIME);
            super.setTypeName("time");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                Date d = dfo.parse(value);
                if(d == null)
                    throw new Exception();
                return dfo.format(d);
            }
            catch(Exception e) {
                throw new IllegalArgumentException("Invalid time");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                Date d = dfo.parse(value);
                if(d == null)
                    throw new Exception();
                return d;
            }
            catch(Exception e) {
                throw new IllegalArgumentException("Invalid time");
            }
        }
        
        public String initialise() {
            Date d = new Date(); // now
            return dfo.format(d);
        }
    }

    static class TypeTimeStamp extends MetaPropertyType {

        private DateFormat dfo = DateFormat.getDateTimeInstance();

        public TypeTimeStamp() {
            super(Keys.TYPE_TIMESTAMP);
            super.setTypeName("timestamp");
        }
        
        public String validate(String value) throws IllegalArgumentException {
            try {
                Date d = dfo.parse(value);
                if(d == null)
                    throw new Exception();
                return dfo.format(d);
            }
            catch(Exception e) {
                throw new IllegalArgumentException("Invalid timestamp");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                Date d = dfo.parse(value);
                if(d == null)
                    throw new Exception();
                return d;
            }
            catch(Exception e) {
                throw new IllegalArgumentException("Invalid timestamp");
            }
        }
        
        public String initialise() {
            Date d = new Date(); // now
            return dfo.format(d);
        }
    }
    
    static class TypeInterval extends MetaPropertyType {
        public TypeInterval() {
            super(Keys.TYPE_INTERVAL);
            super.setTypeName("interval");
        }
        public String validate(String value) throws IllegalArgumentException {
            try {
                // interval in mS
                return Long.decode(value).toString();
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal interval");
            }
        }
        
        public Comparable<?> getComparable(String value) throws IllegalArgumentException {
            try {
                // interval in mS
                return Long.decode(value);
            }
            catch(Exception e){
                throw new IllegalArgumentException("Illegal interval");
            }
        }
        
        public String initialise() {
            return new String("0");
        }
    }

    static class TypeURL extends MetaPropertyType {
        public TypeURL() {
            super(Keys.TYPE_URL);
            super.setTypeName("url");
        }
        public String validate(String value) throws IllegalArgumentException {
            try {
                URL url = new URL(value);
                return url.toString();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Illegal URL");
            }
        }
        public String initialise() {
            return new String("");
        }
    }
    
    static class TypeText extends MetaPropertyType {
        public TypeText() {
            super(Keys.TYPE_TEXT);
            super.setTypeName("text");
        }
        public String validate(String value) throws IllegalArgumentException {
            return value;
        }
        public String initialise() {
            return new String("");
        }
        
        /**
         * Get an appropriate Renderer for this type. Over-ride for any
         * thing different such as checkboxes for boolean, calendars,
         * etc. 
         * @param value is the initial value to display.
        * @return a suitable JComponent for this display or null to use a default.
        */
       public Component getRenderer(Object value){
           Component editor = getEditor(value);
           editor.setEnabled(false);
           return editor;
       }

        /**
         * Get an appropriate editor for this type. Over-ride for any
         * thing different such as checkboxes for boolean, calendars,
         * etc. Note that if you over-ride this you should also over-ride
         * <code>getEditValue(Component)</code>
         * @param value is the initial value to edit.
        * @return a suitable JComponent for this display or null to use a default.
        */
       public Component getEditor(Object value){
           
           TextArea field = new TextArea((String)value);
           field.setColumns(getDisplayLength());
           field.setRows(5);
           return field;
       }
       
       /**
        * Gets the edited value from the editor.  The component should be the same
        * as supplied by getEditor(String) which must be over-ridden if this method
        * is over-ridden.
        * @param component
        * @return
        */
       public String getEditValue(Component component){
           if(!(component instanceof TextArea)){
               throw new IllegalArgumentException("Cannot get edit value from a " + component.getClass().getName());
           }
           TextArea field = (TextArea)component;
           return field.getText();
       }

    }

    
}
