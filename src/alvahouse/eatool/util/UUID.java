/* UUID.java
*/

package alvahouse.eatool.util;

import java.math.BigInteger;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * class for generating "Universally Unique Identifiers" or UUIDs.  This
 * implements the Leach type 1 algorithm.
 * Note that objects of this class are immutable.
 * @author  rbp28668
 * 
 */
public class UUID implements Cloneable, Comparable<UUID> {

    public static final UUID NULL = new UUID("00000000-0000-0000-8000-000000000000");
    private static String translate = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789$_";
    private static byte[] invertTranslate = new byte[256];
    
    static {
    	for(int i=0; i<invertTranslate.length; ++i) {
    		invertTranslate[i] = -1;
    	}
    	for(int i=0; i<translate.length(); ++i) {
    		invertTranslate[ translate.codePointAt(i)] = (byte)i;
    	}
    	
    }
    
    /** Creates new UUID  */
    public UUID() {
        m_value = createUUID();
    }
    
    /** Creates a new UUID from the given input string
     * @param value is the string representation of the UUID
     */
    public UUID(String value) {
        m_value = new String(value.toLowerCase());
    }
    
	/**
	 * @see java.lang.Object#clone()
	 */
    @Override
    public Object clone() {
        return new UUID(m_value);
    }
    
	/**
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return m_value;
    }
    
	/**
	 * @see java.lang.Object#hashCode()
	 */
    @Override
    public int hashCode() {
        return m_value.hashCode();
    }
    
	/**
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
    public boolean equals(Object rhs) {
        if(rhs instanceof UUID)
            return m_value.compareTo(((UUID)rhs).m_value) == 0;
        else
            return false;
    }

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
    @Override
    public int compareTo(UUID rhs) {
        return m_value.compareTo(rhs.m_value);
    }
    
    /**
     * Presents the UUID as a compressed string that is also a valid
     * JSON identifier.  Effectively the dashes are removed, it's 
     * treated as a hex number which is then generated as a base64 number
     * but with the guarantee that the leading character will always
     * be a lower case letter.  So one base 26 character followed by
     * 21 base 64 ones.
     * @return compressed version of the UUID.
     */
    public String asJsonId() {
    	
    	// Remove the - chars
    	StringBuilder builder = new StringBuilder(32);
    	for(int i=0; i<m_value.length(); ++i) {
    		char ch = m_value.charAt(i);
    		if(ch != '-') builder.append(ch);
    	}
    	// Now parse it as a hex number
    	BigInteger val = new BigInteger(builder.toString(), 16);
    	int bits = 128;				  // with this number of bits 
    	BigInteger sixtyFour = BigInteger.valueOf(64);// 
    	char result[] = new char[22]; // Need this number of chars (2 + 6*21)
    	
    	int idx = 0;

    	// Convert to what are effectively base64 digits, least significant first.
    	// Make sure the first value is a lower case letter
		BigInteger[] acc = val.divideAndRemainder(BigInteger.valueOf(26));
		int digit = acc[1].intValue();
		result[idx++] = translate.charAt(digit);
		val = acc[0];
		bits -= 4; // slightly more actually.
		
		// Now do the rest
    	while(bits > 0) {
    		acc = val.divideAndRemainder(sixtyFour);
    		digit = acc[1].intValue();
    		result[idx++] = translate.charAt(digit);
    		val = acc[0];
    		bits -= 6;  // as divided by 64
    	}
    	return new String(result);
    }

    /**
     * Creates a UUID from the compressed version.
     * @param id is the ID created by asJsonId()
     * @return a normal UUID.
     */
    public static UUID fromJsonId(String id) {
    	if(id.length() != 22) {
    		throw new IllegalArgumentException("Invalid length for UUID identifier");
    	}
    	
    	BigInteger sixtyFour = BigInteger.valueOf(64);
    	BigInteger acc = BigInteger.valueOf(0);
    	
    	int idx = 22;
    	BigInteger mult = BigInteger.valueOf(26);
    	while(--idx > 0 ) {
    		int ch = id.codePointAt(idx);
    		if(ch >= invertTranslate.length) {
    			throw new IllegalArgumentException("Invalid character in UUID identifier");
    		}
    		byte digit = invertTranslate[ch]; // Convert char to byte in range 0..63
    		acc = acc.multiply(mult);
    		acc = acc.add(BigInteger.valueOf(digit));
    		mult = sixtyFour;
    	}
		int ch = id.codePointAt(idx);
		if(ch < 'a' || ch > 'z') {
			throw new IllegalArgumentException("Invalid character in UUID identifier");
		}
		byte digit = invertTranslate[ch]; // Convert char to byte in range 0..63
		acc = acc.multiply(BigInteger.valueOf(26));
		acc = acc.add(BigInteger.valueOf(digit));
		
		// Convert to 32 digit hex number ensuring leading zeros are included.
    	String value = "00000000000000000000000000000000" + acc.toString(16).toLowerCase();
    	value = value.substring(value.length() - 32);
    	
    	value = value.substring(0,8) + "-" + 
    			value.substring(8,12) + "-" +
    			value.substring(12,16) + "-" + 
    			value.substring(16,20) + "-" +
    			value.substring(20);
    	return new UUID(value);
    }
	/**
	 * Method initialise initialises the state of the UUID generator.
	 * @param hostMAC is the 48 bit host adapter MAC address in the format 00-00-00-00-00-00,
     * i.e. 6 pairs of hex digits separated by hyphens.
     * @param savedState is the serialized state as produced by UUID.getState().
     * 
     */
    public static void initialise(String hostMAC, String savedState) {

       // get persistent state of generator
       PersistentState state = new PersistentState();
       state.load(savedState);

		// check validity and work out what we're going to use for a MAC address
		// Supplied MAC address over-rides saved one, but error if invalid format
		// or neither are given.    	
		if(hostMAC != null) {
			if(checkNodeValid(hostMAC))
				mac = hostMAC;
			else
	            throw new IllegalArgumentException("Invalid MAC address for UUID generation");
		} else { // null HostMAC
			if(state.isStateAvailable())
				mac = state.getMAC();
			else
		   		throw new IllegalArgumentException("UUID initialisation needs valid MAC or saved state");;
		}
			  		

        // now check clock and clock sequence
       Date now = new Date();
       long ticks = now.getTime() - start;   // number of mS since start of 15 Oct 1582
       ticks *= TICKS_PER_CLOCK;             // in intervals of 100nS


       if(!state.isStateAvailable() || !state.getMAC().equals(mac)) {
           java.util.Random rnd = new java.util.Random();
           clock_seq = (short)rnd.nextInt(16384); // 14 bit unsigned
       }
       
       if(state.isStateAvailable() && ticks < state.getTimeStamp()) { // clock backwards
            clock_seq = state.getClockSequence();
            ++clock_seq;
       }

        nodeStr = new String();
        
        // Figure out the right separate char to use when parsing mac address.
        String splitChar = "-";
        if(mac.contains(":")){
        	splitChar = ":";
        }
        // Now scrunch up the mac address.
        StringTokenizer toks = new StringTokenizer(mac,splitChar);
        for(int i=0; i<6; ++i) {
            String tok = toks.nextToken();
            nodeStr = nodeStr + tok.toLowerCase();
        }
      
    }

	/**
	 * Method getState gets the current state of the UUID generator as
	 * a string
	 * @return String containing the current state that can be used in the
	 * next call to initialise.
	 */
    public static String getState() {
        PersistentState state = new PersistentState();
        state.setState(mac, lastTick, clock_seq);
        return state.toString();
    }
    
	/**
	 * Method checkNodeValid is a utility method to check a MAC address
	 * for validity
	 * @param mac is the mac address in the format 00-00-00-00-00-00 or
	 * 00:00:00:00:00:00 on Linux.
	 * @return boolean, true only if the format is correct and it is non-zero.
	 */
    public static boolean checkNodeValid(String mac) {
        int sum = 0;
        String sep = "-";
        if(mac.indexOf(":") == 2) { // position 2 should contain first instance of seperator.
            sep = ":";
        }
        StringTokenizer toks = new StringTokenizer(mac,sep);
        if(toks.countTokens() != 6)
            return false;
        try {
            for(int i=0; i<6; ++i) {
                String tok = toks.nextToken();
                sum += Integer.parseInt(tok,16);    // allows check for 00-00-00-00-00-00
            }
        }
        catch(NumberFormatException x) {
            return false;
        }
        return sum > 0; 
    }

    
	/**
	 * Method findMACAddress - utility to find the MAC address of the
	 * host machine
	 * @return String with the 48 bit ethernet MAC address 
	 * in the format 00-00-00-00-00-00
	 */
    public static String findMACAddress() {
        Runtime rt = Runtime.getRuntime();
        String macAddress = null;
        
        String commands[] = new String[4];
        commands[0] = "ipconfig /all";	// NT/Win2k
      	commands[1] = "/sbin/ifconfig eth0"; // Linux (RH 6.2 at least)
      	commands[2] = "/sbin/ifconfig wlp2s0"; // Ubuntu on Del xps15
      	commands[3] = "/sbin/ifconfig"; // Linux - just pick first adapter
      	
      	for(int i=0; i < commands.length && macAddress == null; ++i) {
	        try {
	            StringBuffer buff = new StringBuffer();
	            Process p = rt.exec(commands[i]);
	            java.io.InputStream is = p.getInputStream();
	            int ich = 0;
	            while((ich = is.read()) != -1) {
	                buff.append((char)ich);
	            }
	            //System.out.println(buff);
	            
	            StringTokenizer toks = new StringTokenizer(buff.toString());
	            while(toks.hasMoreTokens()) {
	                String tok = toks.nextToken();
	                if((tok.length() == 17) && checkNodeValid(tok)) {
	                    //System.out.println("Found MAC address " + tok);
	                    tok.replace(":","-");
	                    macAddress = tok;
	                    break;
	                }
	            }
	        }
	        catch(Exception x) {
	            // fail quietly - just return a null string
	        }
      	}
        return macAddress;
    }
    
	/**
	 * Method createUUID creates a UUID string using the leach UUID algorithm.
	 * Used by the constructor to create new UUIDs 
	 * @return String containing the UUID
	 */
    private static String createUUID() {
       // These variables as per Leach spec.
        int time_low;
        short time_mid;
        short time_hi_and_version; // version 4 most significant bits.
        byte clock_seq_hi_and_reserved;
        byte clock_seq_low;

        long ticks;
        do {
           Date now = new Date();
           ticks = now.getTime() - start;   // number of mS since start of 15 Oct 1582
           ticks *= TICKS_PER_CLOCK;        // in intervals of 100nS
           if(ticks == lastTick) {          
               if(ticksExtra < TICKS_PER_CLOCK)
                    ++ticksExtra;
           } else {  // a new tick so can reset the sub-division
               ticksExtra = 0;
           }
        } while(ticksExtra == TICKS_PER_CLOCK);
        
        lastTick = ticks;   
        if(ticksExtra == TICKS_PER_CLOCK)
            ticksExtra = 0; // as have just waited for ticks to roll-over
        ticks += ticksExtra;
        
        // ticks should now hold the nominal time in units of 100nS since 15 Oct 1582
        // with the least significant decimal digit being a counter to provide artificial
        // resolution (as per spec)
        
        time_low =  (int)(ticks & 0x00000000ffffffffl);
        time_mid =  (short)((ticks & 0x0000ffff00000000l)>>32);
        time_hi_and_version = (short)((ticks & 0x0fff000000000000l)>>48);
        time_hi_and_version |= (VERSION)<<4;
        clock_seq_low = (byte)(clock_seq & 0x00ff);
        clock_seq_hi_and_reserved = (byte)((clock_seq & 0x3f00) >> 8);
        clock_seq_hi_and_reserved |= 0x80; // variant field.
        
        String value = toHex(time_low,8) + 
                        "-" +
                        toHex(time_mid,4) +
                        "-" +
                        toHex(time_hi_and_version,4) +
                        "-" +
                        toHex(clock_seq_hi_and_reserved,2) +
                        toHex(clock_seq_low,2) +
                        "-" +
                        nodeStr;
        return value;
    }
    
    /** converts an integer to a hex string with enough leading zeros
     * (if needed) to pad to the required number of digits
     * @param the number to convert
     * @param digits gives the number of digits to return
     * @return the hex representation of the given number
     */
    private static String toHex(int val, int digits) {
        String rep = "00000000" + Integer.toHexString(val);
        return rep.substring(rep.length() - digits);
    }
    
    private String m_value; // of each UUID
    
    
    private static long lastTick = 0;
    private static int ticksExtra  = 0;
    private static short clock_seq = 0;
    private static String nodeStr;                  // mac address without hyphens
    private static String mac;                      // mac address with hyphens

    /** cache for start of timer epoch as is constant */
    private static long start = (new java.util.GregorianCalendar()).getGregorianChange().getTime(); // 15 Oct 1582
    
    private static final int VERSION = 1;
    private static final long TICKS_PER_CLOCK = 10000;  // 1mS timer resolution, 100nS GUID resolution.
    
    /** class to hold the persistent state of a guid
     */
    private static class PersistentState {
		/**
		 * Method isStateAvailable.
		 * @return boolean if, and only if, the persistent state is valid
		 */
        public boolean isStateAvailable() {
            return stateIsValid;
        }
        
		/**
		 * Method getMAC.
		 * @return String
		 */
        public String getMAC() {
            return mac;
        }
        
		/**
		 * Method getTimeStamp.
		 * @return long
		 */
        public long getTimeStamp() {
            return timeStamp;
        }
        
		/**
		 * Method getClockSequence.
		 * @return short
		 */
        public short getClockSequence() {
            return clockSequence;
        }
        
		/**
		 * Method setState.
		 * @param m is the system MAC address
		 * @param ts is the timestamp
		 * @param cs is the clock sequence number.
		 */
        public void setState(String m, long ts, short cs) {
        	mac = m;
        	timeStamp = ts;
        	clockSequence = cs;
        	stateIsValid = true;
        }

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return mac + "*" + timeStamp + "*" + clockSequence;
		}        
		
		/**
		 * Method load sets up the persistent state from a string produced
		 * by toString.
		 * @param state is the string containing the persistent state.
		 */
        public void load(String state) {
            try {
                StringTokenizer toks = new StringTokenizer(state,"*");
                mac = toks.nextToken();
                timeStamp = Long.valueOf(toks.nextToken()).longValue();
                clockSequence = Short.valueOf(toks.nextToken()).shortValue();
                if(UUID.checkNodeValid(mac))
                    stateIsValid = true;
            }
            catch(Exception x) {
                // fail quietly - but won't set stateIsValid either!
                // likely problems are a null state or incorrect format
            }
        }
        
        private String mac;
        private long timeStamp; // normalised to 100nS intervals
        private short clockSequence;
        private boolean stateIsValid = false;
    }
    
}       

