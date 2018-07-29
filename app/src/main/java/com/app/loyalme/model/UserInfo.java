package com.app.loyalme.model;

import java.io.Serializable;

/**
 * Created by dharmraj on 14/5/16.
 */
public class UserInfo implements Serializable {

    private String userName, fullName, Email, userImage, userImageThumb, userStatus, userType, authToken, contact, address, businessName, qrCodeImage;
    private int userId, isApproved;

    public UserInfo() {
        this.fullName = "";
        this.userName = "";
        this.Email = "";
        this.userImage = "";
        this.userImageThumb = "";
        this.userStatus = "";
        this.userType = "";
        this.authToken = "";
        this.contact = "N/A";
        this.address = "";
        this.businessName = "";
        this.userId = -1;
        this.isApproved = -1;
        this.qrCodeImage = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public String getUserImageThumb() {
        return userImageThumb;
    }

    public void setUserImageThumb(String userImageThumb) {
        this.userImageThumb = userImageThumb;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }
}
