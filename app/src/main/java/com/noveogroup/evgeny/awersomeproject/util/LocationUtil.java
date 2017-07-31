package com.noveogroup.evgeny.awersomeproject.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;


public class LocationUtil {
    static private Location lastUpdatedLocation;
    static private LocationUtil locationUtil;
    private final GoogleApiClient googleApiClient;
    private ArrayList<UpdatedLocationHandler> updatedLocationHandlers;

    private LocationUtil(Context context) {
        updatedLocationHandlers = new ArrayList<>();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, getLocationRequest(), location -> updateAllListeners(location));
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {

                })
                .build();
    }

    public static Location getLastUpdatedLocation() {
        return lastUpdatedLocation;
    }

    public static LocationUtil getInstance(Context context) {
        if (locationUtil == null) {
            locationUtil = new LocationUtil(context);
        }
        return locationUtil;
    }

    public static double getDistance(LatLng from, LatLng to) {
        return SphericalUtil.computeDistanceBetween(from, to);
    }

    public void addLocationUpdatesListener(UpdatedLocationHandler handler) {
        updatedLocationHandlers.add(handler);
        if (updatedLocationHandlers.size() == 1) {
            googleApiClient.connect();
        }
    }

    public void removeLocationUpdatesListener(UpdatedLocationHandler handler) {
        updatedLocationHandlers.remove(handler);
        if (updatedLocationHandlers.isEmpty()) {
            googleApiClient.disconnect();
        }
    }

    protected void updateAllListeners(Location location) {
        lastUpdatedLocation = location;
        for (UpdatedLocationHandler handler : updatedLocationHandlers) {
            handler.handleUpdatedLocation(location);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(5000);
        locationRequest.setSmallestDisplacement(5);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public interface UpdatedLocationHandler {
        void handleUpdatedLocation(Location location);
    }
}
