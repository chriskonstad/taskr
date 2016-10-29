package com.taskr.client;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

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

        //showFragment(new TestFragment(), false); //for debugging
        showFragment(new LoginFragment(), false);
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
            case R.id.action_logout:
                LoginManager.getInstance().logOut();
                showFragment(new LoginFragment(), false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

}