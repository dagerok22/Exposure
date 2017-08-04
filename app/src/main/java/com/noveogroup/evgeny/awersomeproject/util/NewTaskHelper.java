package com.noveogroup.evgeny.awersomeproject.util;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 04.08.2017.
 */

public class NewTaskHelper {

    public static final String TAGS_ARRAY = "TAGS_ARRAY";
    public static final String NEW_TASK_NAME = "NEW_TASK_NAME";
    private final Activity activity;
    private String currentPhotoPath;
    private ArrayList<String> tags;
    private String taskName;
    public NewTaskHelper(Activity activity) {
        this.activity = activity;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public Intent dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = PhotoHelper.createImageFile(activity);
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                //FIXME use logback
                Log.d("SCREEN3", "File create err: ", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, "com.noveogroup.evgeny.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                return takePictureIntent;
            }

        }
        return null;
    }

    public void onActivityResult(Intent data) {
        tags = data.getStringArrayListExtra(TAGS_ARRAY);
        taskName = data.getStringExtra(NEW_TASK_NAME);
        Location location = LocationUtil.getLastUpdatedLocation();
        if (location != null) {
            addTaskToDatabase(location);
        } else {
            Toast.makeText(activity, R.string.location_error_cant_create_task
                    , Toast.LENGTH_LONG).show();
        }
    }

    public void addTaskToDatabase(final Location location) {
        float rating = 0;
        String authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        dbApi.writeImageAndGetUrl(new File(currentPhotoPath), new RealTimeDBApi.HandleImageFileCallback() {
            @Override
            public void onSuccess(Uri imageRef) {
                dbApi.getUserById(authorId, data -> {
                    dbApi.writeTask(taskName, tags, imageRef.toString(), new LatLng(location.getLatitude(), location.getLongitude()), rating, authorId, data.getName(), new Date());
                    Toast.makeText(activity, R.string.task_created, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(activity, "Failure send", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}
