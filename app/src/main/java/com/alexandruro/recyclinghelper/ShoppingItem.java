package com.alexandruro.recyclinghelper;

import android.graphics.Bitmap;

/**
 * Created by Alex on 21/02/2018.
 */

public class ShoppingItem {

    private String name;
    private Bitmap image;

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public ShoppingItem(String name, Bitmap image) {

        this.name = name;
        this.image = image;
    }
}
