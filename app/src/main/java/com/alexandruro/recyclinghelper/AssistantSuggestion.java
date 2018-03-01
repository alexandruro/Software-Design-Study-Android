package com.alexandruro.recyclinghelper;

import android.graphics.drawable.Drawable;

public class AssistantSuggestion {

    public static final int TYPE_RECYCLABLE_REBUY = 0;
    public static final int TYPE_UNRECYCLABLE_REBUY = 1;

    private String title;
    private String subtitle;
    private String positiveButtonText;
    private Drawable image;

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Drawable getImage() {
        return image;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public AssistantSuggestion(String title, String subtitle, String positiveButtonText, Drawable image) {

        this.title = title;
        this.subtitle = subtitle;
        this.positiveButtonText = positiveButtonText;
        this.image = image;
    }
}
