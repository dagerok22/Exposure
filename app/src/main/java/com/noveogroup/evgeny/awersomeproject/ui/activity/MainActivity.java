package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {
    private static final int RC_SIGN_IN = 1;
    Context context;
    @BindView(R.id.get_random_task_button)
    FancyButton rndTaskButton;
    private GoogleApiClient googleApiClient;
    private FirebaseUser currentUser;
    private List<Task> tasks;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        requestLocationPermission();

        // Configure Google Sign In

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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
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
    }

    private void updateUI(FirebaseUser user) {
    }

    private void requestLocationPermission() {
        if (hasLocationPermision()) {
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
        else {
            LocationUtil.getInstance(this).addLocationUpdatesListener(this);
        }
    }


    private boolean hasLocationPermision() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }


    @OnClick(R.id.on_screen3_button)
    public void onScreen3() {
        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.get_random_task_button)
    public void onRandomClick() {
        rndTaskButton.setEnabled(false);
        RealTimeDBApi.getInstance().getAllTasks(data -> {
            tasks = data;
            Location currentLocation = LocationUtil.getLastUpdatedLocation();
            if (currentLocation != null) {
                startRandomTask(currentLocation);
            } else {
                Toast.makeText(this, "Can't get your location", Toast.LENGTH_SHORT).show();
                //LocationUtil.getInstance(this).addLocationUpdatesListener(this);
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
    void logOutClicked(){
        auth.signOut();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean handleUpdatedLocation(Location location) {
        return true;
    }
    private void startRandomTask(Location currentLocation) {
        ArrayList<Task> dataSet = new ArrayList<>();
        for (Task task : tasks) {
            double dist = LocationUtil.getDistance(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    new LatLng(task.getLat(), task.getLng()));
            if (dist < 20000)
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

}
