// ISODate.java
/**
 * ***********************************
 * Copyright Notice
 * Copyright (c) 1999, Regents of the University of California. All rights reserved.
 *
 * DISCLAIMER
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * ************************************
 */
package net.matlux.coherence.flooder;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.io.PrintStream;

/**
 * Static class for converting between various Date Formats
 * @author $Author: dwforslund $
 * @version $Revision: 1.13 $ $Date: 2006/09/01 21:57:36 $
 */
public class ISODate {
    //   static DateFormat formatter = DateFormat.getInstance();
    private static PrintStream cat = System.out;

    /**
     * Convert ISO formated dates to Java Date
     *
     *
     * @param dateStr
     *
     * @return
     *
     * @see
     */
    static public Date ISO2Date(String dateStr) {

	if (dateStr.endsWith("Z")) dateStr = dateStr.replaceAll("Z","UTC");
        String timePattern = "";

        // select the time pattern to use:
         if (dateStr.length() == 8) {
            timePattern = "yyyyMMdd";
        } else if (dateStr.length() == 12) {
            timePattern = "yyyyMMddHHmm";
        } else if (dateStr.length() == 13) {
            timePattern = "yyyyMMdd'T'HHmm";
        } else if (dateStr.length() == 14) {
            timePattern = "yyyyMMddHHmmss";
        } else if (dateStr.length() == 15) {
            timePattern = "yyyyMMdd'T'HHmmss";
        } else if (dateStr.length() > 8 && dateStr.charAt(8) == 'T') {
            timePattern = "yyyyMMdd'T'HHmmssz";
        } else {
            timePattern = "yyyyMMddHHmmssz";
        }
        // alternative ISO format with hyphens
        if (dateStr.indexOf('-') > -1) {
            timePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            if (dateStr.length() <= 10)
            timePattern = timePattern.substring(0,dateStr.length() - 1);
            else if (dateStr.length() > 11)
            timePattern = timePattern.substring(0,Math.min(timePattern.length()-1,dateStr.length() + 1));
        }
        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(timePattern);

        Date d = null;

        try {
            d = formatter.parse(dateStr, new ParsePosition(0));
        } catch (NullPointerException e) {
            cat.print("constructor failed for" + dateStr);
        }

        return d;
    }

    /**
     * Convert UTC formatted time to Java Date
     *
     *
     * @param dateStr
     *
     * @return
     *
     * @see
     */
    static public Date UTC2Date(String dateStr) {
        String timePattern = "";

        // select the time pattern to use:
        if (dateStr.length() == 6) {
            timePattern = "yyMMdd";
        }
        if (dateStr.length() == 8) {
            timePattern = "yyyyMMdd";
        } else if (dateStr.length() == 12) {
            timePattern = "yyyyMMddHHmm";
        } else if (dateStr.length() == 13) {
            timePattern = "yyyyMMdd'T'HHmm";
        } else if (dateStr.length() == 14) {
            timePattern = "yyyyMMddHHmmss";
        } else if (dateStr.length() == 15) {
            timePattern = "yyyyMMdd'T'HHmmss";
        } else {
            timePattern = "yyyyMMdd'T'HHmmssZ";
        }
        // alternative UTC format with hyphens
        if (dateStr.indexOf('-') > -1) {
            timePattern = "yyyy-MM-dd'T'HH:mm:ssZ";
            if (dateStr.length() <= 10)
            timePattern = timePattern.substring(0,dateStr.length() - 1);
            else if (dateStr.length() > 11)
            timePattern = timePattern.substring(0,Math.min(timePattern.length()-1,dateStr.length() + 1));

        }
      //  System.out.println("timePattern="+timePattern);
        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(timePattern);


   //     formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date d = null;

        try {
            d = formatter.parse(dateStr, new ParsePosition(0));
        } catch (NullPointerException e) {
        	cat.print("constructor failed for" + dateStr);
        }

        return d;

    }

    /**
     * Convert HL7 formatted date to Java Date
     *  Assumes 4 digit year representation in incoming string
     *
     * @param dateStr
     *
     * @return Java Date object
     *
     * @see   java.text.DateFormat
     */
    static public Date HL72Date(String dateStr) {
        String timePattern = "yyyyMMddHHmmssz";
        if (dateStr.length() <= 14)
            timePattern = timePattern.substring(0, dateStr.length() - 1);

        else if (dateStr.length() > 17) timePattern = "yyyyMMddHHmmsszzzz";

        // select the time pattern to use:
        /**
         if (dateStr.length() == 8) {
         timePattern = "yyyyMMdd";
         } else if (dateStr.length() == 10) {
         timePattern = "yyyyMMddHH";
         } else if (dateStr.length() == 12) {
         timePattern = "yyyyMMddHHmm";
         } else if (dateStr.length() == 14) {
         timePattern = "yyyyMMddHHmmss";
         } else {
         timePattern = "yyyyMMddHHmmssz";
         }
         **/
        cat.print("dateStr: "+ dateStr + " length="+dateStr.length());

        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat(timePattern);

        formatter.setLenient(true);

        Date d = null;

        try {
            d = formatter.parse(dateStr, new ParsePosition(0));
        } catch (NullPointerException e) {
        	cat.print("constructor failed for" + dateStr);
        }

        return d;


        // return formatter.parse(dateStr, new ParsePosition(0));

    }

    /**
     * convert HL7 formatted to ISO Date
     *
     *
     * @param dateStr
     *
     * @return
     *
     * @see
     */
    static public String HL72ISO(String dateStr) {
        try {
            if (dateStr.indexOf("T")> -1 || dateStr.indexOf("-") > -1 || dateStr.indexOf(":") > -1 )
                return Date2ISO(ISO2Date(dateStr));
            else
               return Date2ISO(HL72Date(dateStr));
        } catch (Exception e) {
        	cat.print(e);
            return dateStr;
        }
    }

    /**
     * Convert HL7 formatted date to UTC format
     *
     *
     * @param dateStr
     *
     * @return
     *
     * @see
     */
    static public String HL72UTC(String dateStr) {
        try {
            // allow for embedded "T" in HL7.
            if (dateStr.indexOf('T') < 10 || dateStr.indexOf('-') > 0 || dateStr.indexOf(':') > 0 )
                 return Date2UTC(UTC2Date(dateStr));
            else
                 return Date2UTC(HL72Date(dateStr));
        } catch (Exception e) {
        	cat.print(e);
            return dateStr;
        }
    }

    /**
     * Convert Java Date to ISO Date string
     *
     *
     * @param date
     *
     * @return
     *
     * @see
     */
    /*static public String Date2ISO(Date date) {
        String timePattern = "yyyyMMdd'T'HHmmssz";
        SimpleDateFormat formatter = new SimpleDateFormat(timePattern);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");

        formatter.setTimeZone(timeZone);
        String f =  formatter.format(date);
        return f.replaceAll("UTC","Z");

    }*/
	public static String Date2ISO(Date date) {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd'T'H:mm:ss.SSS Z");
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		//formatter.setTimeZone(timeZone);
        String time = formatter.format(date);
        return time;
	}
    

    /**
     * Convert Java Date to HL7 Date string
     *
     *
     * @param date
     *
     * @return
     *
     * @see
     */
    static public String Date2HL7(Date date) {

        String timePattern = "yyyyMMddHHmmssz";

        SimpleDateFormat formatter = new SimpleDateFormat(timePattern);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        //timeZone.setID("UTC");
        formatter.setTimeZone(timeZone);

        String f = formatter.format(date);
        f.replaceAll("UTC","Z");
        return f;
    }

    /**
     * Convert ISO Date string  to UTC Date string
     *
     *
     * @param dateStr
     *
     * @return
     *
     * @see
     */
    static public String ISO2UTC(String dateStr) {
        return Date2UTC(ISO2Date(dateStr));
    }

    /**
     * Convert UTC Date to HL7 Date string
     *
     *
     * @param dateStr
     *
     * @return String
     *
     * @see
     */
    static public String UTC2HL7(String dateStr) {
        return Date2HL7(UTC2Date(dateStr));
    }

    /**
     * Convert ISO Date to HL7 Date String
     * @param dateStr
     * @return String
     */
    static public String ISO2HL7(String dateStr) {
        return Date2HL7(ISO2Date(dateStr));
    }
    /**
     * Convert Java Date to Universal Time (UTC)
     *
     * @param date
     * @return  String
     * @see
     */
    static public String Date2UTC(Date date) {

        String timePattern = "yyyyMMdd'T'HHmmssz";

        SimpleDateFormat formatter = new SimpleDateFormat(timePattern);

        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String f = formatter.format(date);
        return f.replaceAll("UTC","Z");


    }

	public static Date timeMillis2Date(long timeMillis) {
		return new Date(timeMillis);
	}

	
	public static long Date2TimeMillis(Date date)
    {
        return (long)date.getTime();
    }
    

}