package com.dans.apps.baratonchurch.utils;

/**
 *
 * Created by duncan on 12/21/17.
 */

public class UrlUtils {

    /**
     * @deprecated
     * @param requestTitle
     * @param requestMessage
     * @param userID
     * @param requestType
     * @return
     */
    public static String formulateRequestsUrl(String requestTitle,
                                              String requestMessage,String userID,String requestType){
        if(requestType == null || requestMessage == null || userID == null){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("http://admin.church.ueab.ac.ke/php/index.php?command=add&request_initiator=");
        builder.append(userID);
        builder.append("&request_message=");

        String message=requestMessage.trim();//remove beginning and trailing spaces
        message=message.replaceAll(" ","+");
        builder.append(message);
        builder.append("&request_title=");
        if(requestTitle!=null && requestTitle.trim().length()>0){
            String title =requestTitle.trim();
            title = title.replaceAll(" ","+");
            builder.append(title);
        }
        builder.append("&request_type=");
        builder.append(requestType);
        builder.append("&table=requests");

        return builder.toString();
    }

    /**
     * @deprecated
     * @param title
     * @param body
     * @param userId
     * @return
     */
    public static String formulatePostUrl(String title, String body, String userId) {
        if(body == null || userId == null){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("http://admin.church.ueab.ac.ke/db?announcement_message=");

        String postBody=body.trim();
        postBody = postBody.replaceAll(" ","+");
        builder.append(postBody);
        builder.append("&announcement_sender=");
        builder.append(userId);
        builder.append("&announcement_title=");

        if(title!=null && title.trim().length()>0){
            String postTitle=title.trim();
            postTitle = postTitle.replaceAll(" ","+");
            builder.append(postTitle);
        }
        builder.append("&command=add&table=announcements");

        return builder.toString();
    }
}
