package com.noveogroup.evgeny.awersomeproject.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getFilesDir();
        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
}


