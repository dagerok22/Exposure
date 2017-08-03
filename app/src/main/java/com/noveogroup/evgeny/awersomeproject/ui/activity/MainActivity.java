package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;
import com.noveogroup.evgeny.awersomeproject.util.PhotoHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {

    public static final String TAGS_ARRAY = "TAGS_ARRAY";
    public static final String NEW_TASK_NAME = "NEW_TASK_NAME";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_TAGS = 2;
    Context context;
    @BindView(R.id.get_random_task_button)
    FancyButton rndTaskButton;
    private GoogleApiClient googleApiClient;
    private FirebaseUser currentUser;
    private List<Task> tasks;
    private FirebaseAuth auth;
    private String currentPhotoPath;
    private ArrayList<String> tags;
    private String taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> {
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Важное сообщение!")
                        .setMessage("Чтобы выполнять или добавлять задания мы должны понять где вы находитесь")
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                (dialog, id) -> {
                                    dialog.cancel();
                                    requestLocationPermission();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                requestLocationPermission();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationUtil.getInstance(this).removeLocationUpdatesListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (hasLocationPermision()) {
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        } else {
            LocationUtil.getInstance(this).addLocationUpdatesListener(this);
        }
    }


    private boolean hasLocationPermision() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }


    @OnClick(R.id.add_new_task_btn)
    public void onNewTaskClick() {
        dispatchTakePictureIntent();
    }

    @OnClick(R.id.get_random_task_button)
    public void onRandomClick() {
        rndTaskButton.setEnabled(false);
        RealTimeDBApi.getInstance().getAllTasksSingle(data -> {
            tasks = data;
            Location currentLocation = LocationUtil.getLastUpdatedLocation();
            if (currentLocation != null) {
                startRandomTask(currentLocation);
            } else {
                Toast.makeText(this, R.string.cant_get_your_location, Toast.LENGTH_SHORT).show();
                rndTaskButton.setEnabled(true);
            }
        });
    }

    @OnClick(R.id.open_user_list)
    public void onUserListBtnClick() {
        startActivity(UserListActivity.newIntent(this));
    }

    @OnClick(R.id.goToListButton)
    public void onButtonClick() {
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.log_out_button)
    void logOutClicked() {
        auth.signOut();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean handleUpdatedLocation(Location location) {
        return false;
    }

    private void startRandomTask(Location currentLocation) {
        ArrayList<Task> dataSet = new ArrayList<>();
        for (Task task : tasks) {
            double dist = LocationUtil.getDistance(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(task.getLat(), task.getLng()));
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (!task.isUserDone(currentUser) && dist < 20000)
                dataSet.add(task);
        }
        if (dataSet.size() > 0) {
            Task randomTask = dataSet.get(new Random().nextInt(dataSet.size()));
            Intent intent = TaskDetailsActivity.getIntent(this, randomTask);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Нет заданий расположенных недалеко от вас", Toast.LENGTH_SHORT).show();
        }
        rndTaskButton.setEnabled(true);
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
               // finish();
            } else {
                Toast.makeText(this, "Мы не можем определить ваше местоположение. Задание не будет добавленно."
                        , Toast.LENGTH_LONG).show();
            }
        }
    }

    private void addTaskToDatabase(final Location location) {
        float rating = 0;
        String authorId = currentUser.getUid();
        RealTimeDBApi dbApi = RealTimeDBApi.getInstance();
        //Toast.makeText(getApplicationContext(), "got location", Toast.LENGTH_SHORT).show();
        dbApi.writeImageAndGetUrl(new File(currentPhotoPath), new RealTimeDBApi.HandleImageFileCallback() {
            @Override
            public void onSuccess(Uri imageRef) {
                dbApi.getUserById(authorId, data -> {
                    dbApi.writeTask(taskName, tags, imageRef.toString(), new LatLng(location.getLatitude(), location.getLongitude()), rating, authorId, data.getName(), new Date());
                });
                Toast.makeText(getApplicationContext(), "Task was add", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "Failure send", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}
