package com.dans.apps.baratonchurch.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by duncan on 11/20/17.
 */

public class DateUtils {

    /**
     * -1 error
     * 0 both are the same
     * 1 first date > second date
     * 2 second date > first date
     * @param firstDate
     * @param secondDate
     * @return
     */
    public static int compareDates(String firstDate,String secondDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date first = sdf.parse(firstDate);
            Date second =sdf.parse(secondDate);

            if(first.equals(second)){
                return 0;
            }
            if(first.after(second)){
                return 1;
            }
            if(first.before(second)){
                return 2;
            }
        } catch (ParseException e) {
            return -1;
        }

        return -1;
    }
    public static String shortenDate(String date){
        if(date == null){
            return null;
        }

         if(date.indexOf(".") == -1){

         }
         return null;
    }
    //date parameter value should be in the format of 07/10/2017
    //returns 07 October
    public static String getHumanFriendlyFormat(String date) {
        if (date.indexOf("/") == -1) {
            return null;
        }

        String[] s = date.split("/");
        int month;
        try {
            month =Integer.valueOf(s[1].trim());
        }catch (Exception e){
            return null;
        }
        String monthString = null;
        switch (month){
            case 1:
                monthString="January";
                break;
            case 2:
                monthString="February";
                break;
            case 3:
                monthString="March";
                break;
            case 4:
                monthString="April";
                break;
            case 5:
                monthString="May";
                break;
            case 6:
                monthString="June";
                break;
            case 7:
                monthString="July";
                break;
            case 8:
                monthString="August";
                break;
            case 9:
                monthString="September";
                break;
            case 10:
                monthString="October";
                break;
            case 11:
                monthString="November";
                break;
            case 12:
                monthString="December";
                break;

        }
        if(monthString == null){
            return null;
        }

        return s[0].trim()+" "+monthString;
    }

    //date parameter value should be in the format of 07/10/2017
    //returns saturday 07 October
    public static String getHumanFriendlyDateWithDay(String dateValue){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dayOfTheWeek = null;
        try {
            Date date = format.parse(dateValue);
            dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String simpleDate = DateUtils.getHumanFriendlyFormat(dateValue);
        return dayOfTheWeek+" "+simpleDate;
    }

    public static String getHumanFriendlyDateTimeFromISODateTime(String isoString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = format.parse(isoString);
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String year         = (String) DateFormat.format("yyyy", date); // 2013
        String hours        = (String)DateFormat.format("HH",date);
        String minutes      = (String)DateFormat.format("mm",date);
        return dayOfTheWeek+" "+monthString+" "+year+"  "+hours+":"+minutes;
    }


}
