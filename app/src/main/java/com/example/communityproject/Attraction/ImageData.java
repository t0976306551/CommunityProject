package com.example.communityproject.Attraction;

import android.graphics.Bitmap;

public class ImageData {
    private Bitmap bitmap;

    public ImageData(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
