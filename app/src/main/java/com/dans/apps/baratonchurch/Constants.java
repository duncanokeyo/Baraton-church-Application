package com.dans.apps.baratonchurch;

/**
 * Created by duncan on 11/9/17.
 */

public class Constants {
    public static final String MAPS_API_KEY ="AIzaSyDm1ToZrsM8JfUKtsRkegrvERvAE4s_Vtw";
    public static final String CHURCH_LOCATION =
            "https://maps.googleapis.com/maps/api/staticmap?" +
                    "center=0.2550967,35.079235&markers=color:red%7Clabel:C%7C0.2550967,35.079235" +
                    "&zoom=10&size=300x400&key="+MAPS_API_KEY;
    public static final String YOUTUBE_API_KEY = "AIzaSyDYCNp6kl4LWaup99zalixmzgndZp_jMkA";

    public static final int LOGIN_REQUEST_CODE = 3;
    public static final String MESSAGE_PENDING = "message_pending";
    public static final String DefaultLanguage = "en";
    public static final String ANNOUNCEMENT_FILE_NAME="announcement.json";
    public static final String SERMONS_FILE_NAME="video_list.json";
    public static final String YOUTUBE_URL_SEGMENT = "https://youtu.be/";
    public static final String BUC_CHURCH_BLOG = "http://ueab.ac.ke/church/?cat=3?&json=1";

    public static String HTML = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "\t<title> <h1></h1></title>\n" +
            "</head>\n" +
            "<body>\n" +"content"+
            "\n" +
            "</body>\n" +
            "</html>";

    public static String CHANNEL_VIDEO_LIST =
            "https://www.googleapis.com/youtube/v3/search?channelId=UCU7Jqywi5a90DV0WM8XVcuQ&key=AIzaSyCzbkU47Qijb1mQf_y9X6jNsfVxMTOu4Dg&maxResults=50&order=date&part=id,snippet&q=&type=video";

    public interface USER_TYPE{
        int Chaplain=1;
        int Congregant = 2;
        int Unkown = 3;
    }

    public interface BUC_API {
        String AnnouncementsUrl = "http://admin.church.ueab.ac.ke/announcements/";
        String LoginVerification = "http://admin.church.ueab.ac.ke/auth/verify";
        String Register = "http://admin.church.ueab.ac.ke/auth/register";
        String DailyVerses = "http://www.ourmanna.com/verses/api/get/?format=json";
    }

    //new adventech api requires that append add /index.json to the url
    public interface SABBATH_SCHOOL_API_URLS {
        String Languages="https://sabbath-school-stage.adventech.io/api/v1/languages/index.json";
        String ListQuaterlies="https://sabbath-school-stage.adventech.io/api/v1/{lang}/quarterlies/index.json";
        String Quartalies="https://sabbath-school-stage.adventech.io/api/v1/{lang}/quarterlies/{quarterly_id}/index.json";
        String Lessons="https://sabbath-school-stage.adventech.io/api/v1/{lang}/quarterlies/{quarterly_id}/lessons/{lesson_id}/index.json";
        String Days="https://sabbath-school-stage.adventech.io/api/v1/{lang}/quarterlies/{quarterly_id}/lessons/{lesson_id}/days/index.json";
        String Read="https://sabbath-school-stage.adventech.io/api/v1/{lang}/quarterlies/{quarterly_id}/lessons/{lesson_id}/days/{day_id}/read/index.json";
    }

    public interface Store {
        String chaplain_path="/BaratonChurch/chaplain/";
        String announcements_path="/BaratonChurch/announcements";
        String requests_path="/BaratonChurch/request";
        String request_collection_path = "/BaratonChurch/request/requests";
    }


    public interface Chaplain {
        String pin = "pin";
        String phone_number="phone_number";
        String email_address = "email_address";
        String official_name="official_name";
    }

    public interface RequestStatus{
        int WAITING_APPROVAL=1;
        int APPROVED = 2;
        int REJECTED = 3;
    }

    public interface RequestTypes{
        int PRAYER = 0;
        int ANNOUNCEMENT = 1;
        int MEMBERSHIP_TRANSFER = 2;
        int MARRIAGE_SOLEMNIZATION = 3;
        int CHILD_DEDICATION =4;
        int BAPTISM =5;
        int OTHER =6;
    }

    public interface Request{
        /////////////////////////
        //membership transfer///
        String transferType="transfer_type";
        String previousChurch="Previous church";
        String newChurch="New church";
        String reasonForTansfer="Reason for transfer";
        ///////////////////////
        //marriage solemnization//
        String nameOfBride="Name of bride";
        String nameOfBrideGroom="Name of bridegroom";
        String weddingDate="Date planned to be wedded";
        String venue="Venue";
        String facilitiesNeeded="Facilities needed";
        //child dedication//////
        String nameOfChild="Name of child";
        String dateOfBirth="Date of birth";
        String dateOfDedication="Date on which you wish the child to be dedicated";
        String fatherName="Name of the father";
        String motherName="Name of the mother";
        //baptism
        String dateOfBaptism="Date on which you wished to be baptised";
        //other
    }
}
