package com.noveogroup.evgeny.awersomeproject.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by sergey on 29.07.17.
 */

public class LocationUtil {
    private LocationManager locationManager;
    private Context context;
    private boolean isGPSEnabled;
    private LocationListener locationListener;
    private static LocationUtil singletoneLocationUtil;


    public static LocationUtil getInstance(Context context) {
        if (singletoneLocationUtil != null){
            return singletoneLocationUtil;
        }
        singletoneLocationUtil = new LocationUtil();
        singletoneLocationUtil.context = context;
        singletoneLocationUtil.locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        singletoneLocationUtil.isGPSEnabled = singletoneLocationUtil.locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return singletoneLocationUtil;
    }

    public void requestLocationUpdates(long minTimeBetweenUpdates, float minDistanceChangeForUpdates, LocationListener locationListener) {
        this.locationListener = locationListener;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTimeBetweenUpdates,
                minDistanceChangeForUpdates,
                locationListener);
    }

    public void removeLocationUpdates(){
        locationManager.removeUpdates(locationListener);
    }

    private String getProviderName() {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);

        return locationManager.getBestProvider(criteria, true);
    }
}
