package com.example.guillaumelam34.taskrclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int DEFAULT_RADIUS = 10;
    public static final int LOCATION_REQUEST = 1;
    public static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;

    @BindView(R.id.profile_id_result) TextView profileJSON;
    @BindView(R.id.nearby_requests_result) TextView requestsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initLocationMgr();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initLocationMgr(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST
            );
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, locationListener);
    }

    public void findUserById(View view) {
        EditText editText = (EditText) findViewById(R.id.profile_id_field);
        int userId = Integer.parseInt(editText.getText().toString());

        Api.getInstance(this).getUserProfile(userId, new Api.ApiCallback<Api.Profile>() {
            @Override
            public void onSuccess(Api.Profile profile) {
                String profileString = "Name: " + profile.name + "\n" +
                        "ID: " + profile.id + "\n" +
                        "Wallet: " + profile.wallet;
                profileJSON.setText(profileString);
            }

            @Override
            public void onFailure(String message) {
                profileJSON.setText(message);
            }
        });
    }

    public void findNearbyRequests(View view){
        if(null == lastLocation) {
            for(String provider : locationManager.getAllProviders()) {
                if(null == lastLocation) {
                    try {
                        lastLocation = locationManager.getLastKnownLocation(provider);
                    } catch (SecurityException e) { // Android Studio complains otherwise :)
                        Log.e(TAG, e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        // lastLocation not promised to be nonnull, but through a NPE is as good as anything else
        // right now. Fix later
        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();
        double radius = DEFAULT_RADIUS; // TODO: store/get from settings?

        Api.getInstance(this).getNearbyRequests(latitude, longitude, radius,
                new Api.ApiCallback<ArrayList<Api.Request>>() {
            @Override
            public void onSuccess(ArrayList<Api.Request> requests) {
                String data = "";

                for(Api.Request req : requests) {
                    data += "ID: " + req.id + "\n" +
                            "Title: " + req.title + "\n" +
                            "Amount: " + req.amount + "\n" +
                            "\n";
                }

                if(data.equals("")) {
                    data = "No requests nearby";
                }

                requestsJSON.setText(data);
            }

            @Override
            public void onFailure(String message) {
                requestsJSON.setText(message);
            }
        });
    }
}