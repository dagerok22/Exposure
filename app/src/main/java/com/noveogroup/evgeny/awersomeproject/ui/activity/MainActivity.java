package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;
import com.noveogroup.evgeny.awersomeproject.db.model.Task;
import com.noveogroup.evgeny.awersomeproject.util.LocationUtil;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity implements LocationUtil.UpdatedLocationHandler {

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }
    }


    @OnClick(R.id.on_screen3_button)
    public void onScreen3() {
        Intent intent = new Intent(MainActivity.this, AddNewTaskActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.get_random_task_button)
    public void onRandomClick() {
        RealTimeDBApi.getInstance().getAllTasks(data -> {
            Location currentLocation = LocationUtil.getLastUpdatedLocation();
            if (currentLocation != null) {
                ArrayList<Task> dataSet = new ArrayList<>();
                for (Task task : data) {
                    if ((LocationUtil.getDistance(
                            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            new LatLng(task.getLat(), task.getLng()))) < 10000)
                        dataSet.add(task);
                }
                Task randomTask = dataSet.get(new Random().nextInt(dataSet.size()));
               // TaskExecutionActivity.newIntent(context,)
            }


//        Intent intent = new Intent(MainActivity.this, AddNewTaskActivity.class);
//        startActivity(intent);
        });
    }


    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Loading tasks..");
        return progressDialog;
    }

    @OnClick(R.id.button)
    public void onButtonClick() {
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }


    @Override
    public void handleUpdatedLocation(Location location) {

    }
}
