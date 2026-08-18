package com.example.communityproject.Acyivity;

import android.graphics.Bitmap;

public class ActivityImageData {
    private Bitmap bitmap;

    public ActivityImageData(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
