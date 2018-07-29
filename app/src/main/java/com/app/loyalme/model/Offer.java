package com.app.loyalme.model;

/**
 * Created by dharmraj on 1/2/17.
 */

public class Offer {

    private Integer id;
    private String name, expiryDate, image, description, points;


    public Offer() {
        this.id = -1;
        this.name = "";
        this.expiryDate = "N/A";
        this.image = "";
        this.description = "";
        this.points = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
