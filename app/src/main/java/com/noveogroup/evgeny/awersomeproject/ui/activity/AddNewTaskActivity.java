package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewTaskActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {


    public static final String TAGS_ARRAY = "TAGS_ARRAY";
    public static final String NEW_TASK_NAME = "NEW_TASK_NAME";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_TAGS = 2;
    //FIXME Why package-private?
    String currentPhotoPath;
    ArrayList<String> tags;
    String taskName;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private LocationUtil locationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button2)
    public void onCameraOpenClick() {
        dispatchTakePictureIntent();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //FIXME use logback
                Log.d("SCREEN3", "File create err: ", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.noveogroup.evgeny.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            startActivityForResult(NewPhotoActivity.newIntent(this, currentPhotoPath), REQUEST_CHOOSE_TAGS);
        }
        if (requestCode == REQUEST_CHOOSE_TAGS && resultCode == RESULT_OK) {
            tags = data.getStringArrayListExtra(TAGS_ARRAY);
            taskName = data.getStringExtra(NEW_TASK_NAME);
            LocationUtil.getInstance(this).addLocationUpdatesListener(this);
        }
    }

    //FIXME !!! CODE DUPLICATION. The same as in the TaskDetailsActivity
    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getFilesDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void handleUpdatedLocation(Location location) {
        LocationUtil.getInstance(this).removeLocationUpdatesListener(this);
        float rating = 0;
        String authorName = "Evgen";
        int authorId = 5;
        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        Toast.makeText(getApplicationContext(), "got location", Toast.LENGTH_SHORT).show();
        dbApi.writeImageAndGetUrl(new File(currentPhotoPath), new RealTimeDBApi.HandleImageFileCallback() {
            @Override
            public void onSuccess(Uri imageRef) {
                dbApi.writeTask(taskName, tags, imageRef.toString(), new LatLng(location.getLatitude(), location.getLongitude()), rating, authorId, authorName, new Date());
                Toast.makeText(getApplicationContext(), "Success send", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Failure send", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}

