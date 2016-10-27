package com.example.guillaumelam34.taskrclient;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_RADIUS = 10;
    public static final int LOCATION_REQUEST = 1;
    public static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;

    private TextView profileJSON;
    private TextView requestsJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profileJSON = (TextView) findViewById(R.id.profile_id_result);
        requestsJSON = (TextView) findViewById(R.id.nearby_requests_result);

        initLocationMgr();
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

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = new APIStringBuilder().buildProfileString(userId);
            QueryHelper q = new QueryHelper();
            q.setResultContainer(profileJSON);
            q.execute(url);
        } else {
            profileJSON.setText("NO CONNECTION");
        }
    }

    public void findNearbyRequests(View view){
        double latitude = lastLocation.getLatitude();
        double longitude = lastLocation.getLongitude();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String url = new APIStringBuilder().buildNearbyRequestsString(latitude, longitude, DEFAULT_RADIUS);
            QueryHelper q = new QueryHelper();
            q.setResultContainer(requestsJSON);
            q.execute(url);
        } else {
            requestsJSON.setText("NO CONNECTION");
        }
    }

    public class QueryHelper extends AsyncTask<String, Void, String> {
        private TextView resultContainer;

        public void setResultContainer(TextView v){
            resultContainer = v;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                APIRequestHelper a = new APIRequestHelper();
                return a.sendGETRequest(new URL(urls[0]));
            } catch (IOException e) {
                return "Unable to complete API request";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultContainer.setText(result);
        }
    }
}