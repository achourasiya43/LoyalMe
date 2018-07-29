package com.app.loyalme.model;

/**
 * Created by dharmraj on 1/2/17.
 */

public class SettingDto {

    private String itemName;
    private int imageId;

    public SettingDto(String itemName, int imageId) {
        this.itemName = itemName;
        this.imageId = imageId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
