package com.taskr.client;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.taskr.api.Api;

import java.security.MessageDigest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        logKeyHash();

        if(!LocationProvider.hasPermissions(this)) {
            LocationProvider.checkPermissions(this);
        } else {
            LocationProvider.onPermissionsChanged(this);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        showLogin();
    }

    public void refreshNavHeader() {
        String name = Api.getInstance(this).getName();
        String email = Api.getInstance(this).getEmail();

        View header = navigationView.getHeaderView(0);
        TextView headerName = (TextView)header.findViewById(R.id.header_name);
        TextView headerEmail = (TextView)header.findViewById(R.id.header_email);

        headerName.setText(name);
        headerEmail.setText(email);
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
                showFragment(new SettingsFragment(), true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i(TAG, "Permissions changed");
        if(LocationProvider.hasPermissions(this)) {
            Log.i(TAG, "Permissions granted");
            LocationProvider.onPermissionsChanged(this);
        } else {
            Log.e(TAG, "Permissions denied");
        }
    }

    public void showFragment(Fragment fragment, boolean addToBackstack) {
        if(null != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            if(addToBackstack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
            Log.i(TAG, "Loading fragment");
        } else {
            Log.wtf(TAG, "Trying to show a null fragment");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            int REQUEST = 0;
            int PROFILE = 1;
            int SETTINGS = 2;

            // TODO Handle profile fragment
            int itemToSelect = -1;
            if(frag instanceof TestFragment) {
                itemToSelect = REQUEST;
            } else if (frag instanceof SettingsFragment){
                itemToSelect = SETTINGS;
            } else {
                Log.wtf("onBackPressed", "Unknown fragment type!!!! Check MainActivity");
            }

            // Only if we have a new section to check should we uncheck the old selected section
            if(itemToSelect != -1) {
                navSelect(itemToSelect);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_requests) {
            showFragment(new TestFragment(), true);
        } else if (id == R.id.nav_profile) {
            // TODO change to profile fragment
            showFragment(new TestFragment(), true);
        } else if (id == R.id.nav_settings) {
            showFragment(new SettingsFragment(), true);
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            showLogin();
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void navSelect(int pos) {
        for(int i=0; i<navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        navigationView.getMenu().getItem(pos).setChecked(true);
    }

    private void showLogin() {
        navSelect(0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        showFragment(new LoginFragment(), false);
    }

    public void onLogin() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        refreshNavHeader();
        navSelect(0);
        showFragment(new TestFragment(), false);
    }

    private void logKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.taskr.client",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}