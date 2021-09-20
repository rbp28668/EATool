/**
 * 
 */
package alvahouse.eatool.repository.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Static formatting utilities 
 * @author bruce_porteous
 *
 */
public class Formatting {

    private final static String TIME_FORMAT_STR = "yyyyMMddHHmmssSSS";
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_STR);

	public static  String fromDate(Date date) {
		return TIME_FORMAT.format(date);
	}
	
	public static Date toDate(String date) throws ParseException {
		return TIME_FORMAT.parse(date);
	}
}
