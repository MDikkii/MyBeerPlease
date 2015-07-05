package com.example.mikoaj.mybeerplease;


public class Beer {

    public Beer(long id, String name, String desc, byte [] imageArray, String type, double price)
    {
        this.id = id;
        this.imageArray = imageArray;
        description = desc;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    private long id;
    private String name;
    private String description;
    private byte[] imageArray;
    private double price;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageArray() {
        return imageArray;
    }

    public void setImageArray(byte [] imageArray) {
        this.imageArray = imageArray;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double longitude) {
        this.price = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
