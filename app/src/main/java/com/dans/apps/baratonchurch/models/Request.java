package com.dans.apps.baratonchurch.models;

import com.dans.apps.baratonchurch.Constants;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents what the congregants will be sending to the church
 * Created by duncan on 10/8/18.
 */

public class Request {
    int requestType;
    String sender;
    String senderEmailAddress;
    String senderPhoneNumber;
    String requestTitle;
    String additionalInformation;
    String reasonForRejection;
    Object createdAt;

    @Exclude
    String requestID;
    int requestStatus = Constants.RequestStatus.WAITING_APPROVAL;

    HashMap<String,String>specificRequestFields;

    public Request(){
    }

    public Request(int requestType, String sender,
                   String senderEmailAddress,
                   String senderPhoneNumber,
                   String requestTitle,
                   String additionalInformation) {
        this.requestType = requestType;
        this.sender = sender;
        this.senderEmailAddress = senderEmailAddress;
        this.senderPhoneNumber = senderPhoneNumber;
        this.requestTitle = requestTitle;
        this.additionalInformation = additionalInformation;
        this.createdAt = ServerValue.TIMESTAMP;
        this.requestStatus = Constants.RequestStatus.WAITING_APPROVAL;
        this.reasonForRejection = null;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    @Exclude
    public String getRequestID() {
        return requestID;
    }

    @Exclude
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderEmailAddress() {
        return senderEmailAddress;
    }

    public void setSenderEmailAddress(String senderEmailAddress) {
        this.senderEmailAddress = senderEmailAddress;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public HashMap<String, String> getSpecificRequestFields() {
        return specificRequestFields;
    }

    public void setSpecificRequestFields(HashMap<String, String> specificRequestFields) {
        this.specificRequestFields = specificRequestFields;
    }

    @Exclude
    public Date getDate(){
        Timestamp tsp = new Timestamp((Long)createdAt);
        return new Date(tsp.getTime());
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", sender='" + sender + '\'' +
                ", senderEmailAddress='" + senderEmailAddress + '\'' +
                ", senderPhoneNumber='" + senderPhoneNumber + '\'' +
                ", requestTitle='" + requestTitle + '\'' +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", createdAt=" + createdAt +
                ", requestStatus=" + requestStatus +
                ", specificRequestFields=" + specificRequestFields +
                '}';
    }
}
