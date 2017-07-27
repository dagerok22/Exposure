package com.noveogroup.evgeny.awersomeproject.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoHelper {
    public static Bitmap compressPhotoFile(String currentPhotoPath, int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        if (bitmap == null) {
            throw new NullPointerException("Can't get bitmap from photo path");
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(new File(currentPhotoPath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap resizeBitmaap(Bitmap bitmap, float dstWidth) {
        int nh = (int) (bitmap.getHeight() * (dstWidth / bitmap.getWidth()));
        return Bitmap.createScaledBitmap(bitmap, (int) dstWidth, nh, true);
    }
}


