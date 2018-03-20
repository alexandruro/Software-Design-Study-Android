package com.alexandruro.recyclinghelper;

import android.graphics.Bitmap;

public class ShoppingItem {

    private String name;
    private Bitmap image;
    private String imageLink;

    public ShoppingItem(String name, String imageLink) {
        this.name = name;
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageLink() {

        return imageLink;
    }

}
