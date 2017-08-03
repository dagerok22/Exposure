package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.DateTransformerUtil;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;
import com.noveogroup.evgeny.awersomeproject.util.PhotoHelper;
import com.noveogroup.evgeny.awersomeproject.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailsActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {

    static final int REQUEST_TAKE_PHOTO = 3;
    static final int REQUEST_DO_TASK = 4;
    private static final String KEY_TASK_ITEM = "TASK_ITEM";
    private static final String USER_MARKER_TAG = "You";

    @BindView(R.id.progress_bar_map)
    ProgressBar progressBarMap;
    @BindView(R.id.map_container)
    FrameLayout mapContainer;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tags)
    TextView tags;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Task currentTask;
    String currentPhotoPath;
    private GoogleMap map;
    private Marker userMarker;
    private LatLng taskPosition;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    public static Intent getIntent(Context context, Task task) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TASK_ITEM, task);
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
        //TODO: initialize in BaseActivity class
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentTask = (Task) getIntent().getSerializableExtra(KEY_TASK_ITEM);
        Location currentLocation = LocationUtil.getLastUpdatedLocation();
        taskPosition = new LatLng(currentTask.getLat(), currentTask.getLng());
        title.setText(currentTask.getName() + (currentTask.isUserDone(currentUser.getUid()) ? "[Done]" : ""));
        tags.setText(StringUtil.getTagsString(currentTask.getTags()));
        author.setText(currentTask.getAuthorName());
        rating.setText(String.valueOf(currentTask.getRating()));

        if (currentLocation != null) {
            distance.setText(String.format(Locale.ENGLISH, "%.1f km", (LocationUtil.getDistance(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    taskPosition)
            ) / 1000));
        } else {
            distance.setText(R.string.find_user_coords);
        }
        age.setText(DateTransformerUtil.getAgeOfTask(currentTask.getDate(), getApplicationContext()));
        initializeMap();
    }

    private void updateDistance() {
        Location currentLocation = LocationUtil.getLastUpdatedLocation();
        distance.setText(String.format(Locale.ENGLISH, "%.1f km", (LocationUtil.getDistance(
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                taskPosition)
                ) / 1000)
        );
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

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;

            LatLng taskPos = new LatLng(currentTask.getLat(), currentTask.getLng());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(taskPos, 16.0f));
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            addTaskCircle(taskPos);
        });
    }

    private void addTaskCircle(LatLng taskPos) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(taskPos)
                .fillColor(ContextCompat.getColor(getApplicationContext(), R.color.marker_circle_color))
                .radius(getResources().getInteger(R.integer.marker_circle_radius))
                .strokeWidth(getResources().getInteger(R.integer.marker_circle_stroke_width));
        map.addCircle(circleOptions);
    }

    private void addUserMarker(Location userLocation) {
        userMarker = map.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())).title(getString(R.string.task_marker_sub)));
        userMarker.setTag(USER_MARKER_TAG);
        userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private void updateUserMarker(Location userLocation) {
        userMarker.setPosition(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
    }

    @OnClick(R.id.fab)
    void onTMPClick() {
        if (currentTask.getAuthorId().equals(currentUser.getUid())){
            Toast.makeText(this, R.string.author_try_to_do_his_task, Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentTask.isUserDone(currentUser.getUid())){
            Toast.makeText(this, R.string.already_done_task, Toast.LENGTH_SHORT).show();
            return;
        }
        Location lastUpdatedLocation = LocationUtil.getLastUpdatedLocation();
        if (lastUpdatedLocation == null) {
            Toast.makeText(this, R.string.cant_get_your_location, Toast.LENGTH_SHORT).show();
            return;
        }
        if (LocationUtil.getDistance(
                new LatLng(lastUpdatedLocation.getLatitude(), lastUpdatedLocation.getLongitude()),
                taskPosition) > getResources().getInteger(R.integer.marker_circle_radius)){
            Toast.makeText(this, "Вы слишком далеко от места задания", Toast.LENGTH_SHORT).show();
            return;
        }
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
                Log.d("TaskDetailsActivity", "File create err: ", ex);
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
            startActivityForResult(TaskExecutionActivity.newIntent(this, currentPhotoPath,
                    currentTask.getName(), new ArrayList<>(currentTask.getTags()),
                    currentTask.getTaskId()), REQUEST_DO_TASK);
        }
        if (requestCode == REQUEST_DO_TASK && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public boolean handleUpdatedLocation(Location location) {
        if (map != null) {
            if (userMarker != null) {
                updateUserMarker(location);
            } else {
                addUserMarker(location);
            }
        }
        updateDistance();
        progressBarMap.setVisibility(View.GONE);
        return false;
    }

}
