package com.app.loyalme.model;

import java.io.Serializable;

/**
 * Created by dharmraj on 24/1/17.
 */

public class RewardedUser implements Serializable{

   private int id, retailerId;
   private Double rewardPoint, totalPurchase;
   private UserInfo userInfo;

    public RewardedUser() {
        this.id = -1;
        this.retailerId = 0;
        this.rewardPoint = 0.0;
        this.totalPurchase = 0.0;
        this.userInfo = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(int retailerId) {
        this.retailerId = retailerId;
    }

    public Double getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(Double rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public Double getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(Double totalPurchase) {
        this.totalPurchase = totalPurchase;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
