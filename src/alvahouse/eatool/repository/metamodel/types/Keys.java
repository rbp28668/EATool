/*
 * Keys.java
 * Project: EATool
 * Created on 07-Jul-2006
 *
 */
package alvahouse.eatool.repository.metamodel.types;

import alvahouse.eatool.util.UUID;


/**
 * Keys provides well known UUID keys for standard built-in meta property types.
 * 
 * @author rbp28668
 */
public class Keys {

    //public static final UUID TYPE_
    public static final UUID TYPE_BOOLEAN= new UUID("00000000-0000-0000-8000-000000000001");
    public static final UUID TYPE_CHAR= new UUID("00000000-0000-0000-8000-000000000002");
    public static final UUID TYPE_BYTE= new UUID("00000000-0000-0000-8000-000000000003");
    public static final UUID TYPE_SHORT= new UUID("00000000-0000-0000-8000-000000000004");
    public static final UUID TYPE_INT= new UUID("00000000-0000-0000-8000-000000000005");
    public static final UUID TYPE_LONG= new UUID("00000000-0000-0000-8000-000000000006");
    public static final UUID TYPE_FLOAT= new UUID("00000000-0000-0000-8000-000000000007");
    public static final UUID TYPE_DOUBLE= new UUID("00000000-0000-0000-8000-000000000008");
    public static final UUID TYPE_STRING= new UUID("00000000-0000-0000-8000-000000000009");
    public static final UUID TYPE_UUID= new UUID("00000000-0000-0000-8000-000000000010");
    public static final UUID TYPE_DATE= new UUID("00000000-0000-0000-8000-000000000011");
    public static final UUID TYPE_TIME= new UUID("00000000-0000-0000-8000-000000000012");
    public static final UUID TYPE_TIMESTAMP= new UUID("00000000-0000-0000-8000-000000000013");
    public static final UUID TYPE_INTERVAL= new UUID("00000000-0000-0000-8000-000000000014");
    public static final UUID TYPE_URL= new UUID("00000000-0000-0000-8000-000000000015");
    public static final UUID TYPE_TEXT= new UUID("00000000-0000-0000-8000-000000000016");

    // Old invalid UUIDs.  Kept hanging around in case of old repositories.
    public static final UUID OLD_TYPE_BOOLEAN= new UUID("00000000-0000-0000-8000-00000001");
    public static final UUID OLD_TYPE_CHAR= new UUID("00000000-0000-0000-8000-00000002");
    public static final UUID OLD_TYPE_BYTE= new UUID("00000000-0000-0000-8000-00000003");
    public static final UUID OLD_TYPE_SHORT= new UUID("00000000-0000-0000-8000-00000004");
    public static final UUID OLD_TYPE_INT= new UUID("00000000-0000-0000-8000-00000005");
    public static final UUID OLD_TYPE_LONG= new UUID("00000000-0000-0000-8000-00000006");
    public static final UUID OLD_TYPE_FLOAT= new UUID("00000000-0000-0000-8000-00000007");
    public static final UUID OLD_TYPE_DOUBLE= new UUID("00000000-0000-0000-8000-00000008");
    public static final UUID OLD_TYPE_STRING= new UUID("00000000-0000-0000-8000-00000009");
    public static final UUID OLD_TYPE_UUID= new UUID("00000000-0000-0000-8000-00000010");
    public static final UUID OLD_TYPE_DATE= new UUID("00000000-0000-0000-8000-00000011");
    public static final UUID OLD_TYPE_TIME= new UUID("00000000-0000-0000-8000-00000012");
    public static final UUID OLD_TYPE_TIMESTAMP= new UUID("00000000-0000-0000-8000-00000013");
    public static final UUID OLD_TYPE_INTERVAL= new UUID("00000000-0000-0000-8000-00000014");
    public static final UUID OLD_TYPE_URL= new UUID("00000000-0000-0000-8000-00000015");
    public static final UUID OLD_TYPE_TEXT= new UUID("00000000-0000-0000-8000-00000016");

    /**
     * 
     */
    public Keys() {
        super();
    }

    
}
