package com.noveogroup.evgeny.awersomeproject.ui.activity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;
import com.noveogroup.evgeny.awersomeproject.util.PhotoHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewTaskActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {


    public static final String TAGS_ARRAY = "TAGS_ARRAY";
    public static final String NEW_TASK_NAME = "NEW_TASK_NAME";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_TAGS = 2;
    private String currentPhotoPath;
    private ArrayList<String> tags;
    private String taskName;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private LocationUtil locationUtil;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationUtil.getInstance(this).addLocationUpdatesListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationUtil.getInstance(this).removeLocationUpdatesListener(this);
    }

    @OnClick(R.id.make_new_task)
    public void onCameraOpenClick() {
        dispatchTakePictureIntent();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = PhotoHelper.createImageFile(this);
                currentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                //FIXME use logback
                Log.d("SCREEN3", "File create err: ", ex);
            }
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
            Location location = LocationUtil.getLastUpdatedLocation();
            if (location != null) {
                addTaskToDatabase(location);
            }
            else {
                Toast.makeText(this,"Мы не можем определить ваше местоположение. Задание не будет добавленно."
                        ,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean handleUpdatedLocation(Location location) {
        //addTaskToDatabase(location);
        return false;
    }

    private void addTaskToDatabase(final Location location) {
        float rating = 0;
        String authorId = currentUser.getUid();
        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        Toast.makeText(getApplicationContext(), "got location", Toast.LENGTH_SHORT).show();
        dbApi.writeImageAndGetUrl(new File(currentPhotoPath), new RealTimeDBApi.HandleImageFileCallback() {
            @Override
            public void onSuccess(Uri imageRef) {
                dbApi.getUserById(authorId, data -> {
                    dbApi.writeTask(taskName, tags, imageRef.toString(), new LatLng(location.getLatitude(), location.getLongitude()), rating, authorId, data.getName(), new Date());
                });
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

