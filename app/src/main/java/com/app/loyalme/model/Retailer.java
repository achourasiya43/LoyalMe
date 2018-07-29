package com.app.loyalme.model;

import java.io.Serializable;

/**
 * Created by dharmraj on 4/1/17.
 */

public class Retailer implements Serializable{

    private int id, point;
    private boolean isPoint;
    private String businessName, email, userName, address, imageUrl, phoneNo;
    private double lat, lng;
    private double distance;

    public Retailer() {
        this.id = -1;
        this.point = 0;
        this.isPoint = false;
        this.businessName = "N/A";
        this.email = "N/A";
        this.userName = "N/A";
        this.address = "N/A";
        this.distance = 0.0d;
        this.imageUrl = "N/A";
        this.phoneNo = "N/A";
        this.lat = 0;
        this.lng = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public boolean isPoint() {
        return isPoint;
    }

    public void setPoint(boolean point) {
        isPoint = point;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
