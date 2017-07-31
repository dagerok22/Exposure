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


public class LocationUtil {
    private final LocationRequest locationRequest;
    private final GoogleApiClient googleApiClient;
    UpdatedLocationHandler updatedLocationHandler;
    private Context context;

    public LocationUtil(Context context) {
        locationRequest = LocationRequest.create();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, location -> updatedLocationHandler.handleUpdatedLocation(location));
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {

                })
                .build();
    }

    public interface UpdatedLocationHandler {
        void handleUpdatedLocation(Location location);
    }

    public static LocationUtil getInstance(Context context, UpdatedLocationHandler updatedLocationHandler) {
        LocationUtil locationUtilInstance = new LocationUtil(context);
        locationUtilInstance.updatedLocationHandler = updatedLocationHandler;
        locationUtilInstance.context = context;
        return locationUtilInstance;
    }

    public void apiConnect() {
        googleApiClient.connect();
    }

    public void apiDisconnect() {
        googleApiClient.disconnect();
    }

    public static double getDistance(LatLng from, LatLng to) {
        return SphericalUtil.computeDistanceBetween(from, to);
    }
}
