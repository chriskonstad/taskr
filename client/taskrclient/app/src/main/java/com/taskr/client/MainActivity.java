package com.taskr.client;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.taskr.api.Api;
import com.taskr.api.LoginResult;
import com.taskr.api.Profile;
import com.taskr.api.ServerApi;
import com.taskr.api.TestApi;

import java.security.MessageDigest;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String UNDER_TEST = "under_test";
    private String TAG;
    private NotificationHandler notificationHandler;
    private Api mApi = null;

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

        TAG = getString(R.string.main_activity_tag);

        // Init the API
        String serverHostname = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_hostname), getString(R.string.default_hostname));
        mApi = new ServerApi(this, serverHostname);
        Intent intent = getIntent();
        if(null != intent) {
            // If this app is running in unit tests, UNDER_TEST will be set
            if(intent.hasExtra(UNDER_TEST)) {
                mApi = new TestApi(this);
            }
        }

        if(!LocationProvider.hasPermissions(this)) {
            LocationProvider.checkPermissions(this);
        } else {
            LocationProvider.onPermissionsChanged(this);
        }

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                } else {
                    // Show arrow
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        try {
            if(mApi.checkReady()) {
                handleInitialRouting();
            }
        } catch (Api.AuthenticationException e) {
            if(mApi instanceof TestApi) {
                // "Mock" login by ignoring login screen
                TestApi tApi = (TestApi) mApi;
                mApi.login(tApi.profile.name, tApi.email, tApi.profile.fbid,
                        new Api.ApiCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult returnValue) {
                                onLogin();
                            }

                            @Override
                            public void onFailure(String message) {
                                Log.wtf(TAG, message);
                                assert false;
                            }
                        });
            } else {
                showLogin();
            }
        }
    }

    public Api api() {
        return mApi;
    }

    @Override
    protected void onNewIntent(Intent intent){
        this.setIntent(intent);
    }

    /**
     * Refresh the navigation drawer header with any new data
     */
    public void refreshNavHeader() {
        String name = mApi.getName();
        String email = mApi.getEmail();

        View header = navigationView.getHeaderView(0);
        TextView headerName = (TextView)header.findViewById(R.id.header_name);
        TextView headerEmail = (TextView)header.findViewById(R.id.header_email);
        ImageView headerProfilePicture = (ImageView)header.findViewById(R.id.nav_profile_picture);

        headerName.setText(name);
        headerEmail.setText(email);
        Ion.with(headerProfilePicture)
                .placeholder(R.drawable.loadingpng)
                .load(Profile.buildProfilePictureUrl(mApi.getFbid()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Uncomment this code to reenable the overflow menu... Not sure if we need/want it
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                showFragment(new SettingsFragment(), true, new TransitionParams(getApplicationContext().getString(R.string.settings_fragment_tag), ""));
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

    /**
     * Show a fragment
     * @param fragment fragment to show
     * @param addToBackstack if the fragment should be added to the backstack
     * @param transitionParams transition parameters, animations, etc.
     */
    public void showFragment(Fragment fragment, boolean addToBackstack, TransitionParams transitionParams) {
        if(null != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            new TransitionFactory(getApplicationContext()).setCustomAnimations(transaction, transitionParams);
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

    public void showFragmentAsDialog(DialogFragment fragment){
        if(null != fragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(null);

            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.show_as_dialog), true);
            fragment.setArguments(bundle);
            fragment.show(ft, "dialog");

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
            int MINE = 1;
            int PROFILE = 2;
            int SETTINGS = 3;

            int itemToSelect = -1;
            if(frag instanceof RequestsFragment && !((RequestsFragment)frag).isLoggedInUser()) {
                itemToSelect = REQUEST;
            } else if (frag instanceof RequestsFragment) {
                itemToSelect = MINE;
            } else if (frag instanceof ProfileFragment) {
                itemToSelect = PROFILE;
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

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (id == R.id.nav_requests &&
                !(frag instanceof RequestsFragment && !((RequestsFragment)frag).isLoggedInUser())) {
            showFragment(new RequestsFragment(), false, new TransitionParams("", getString(R.string.requests_fragment_tag)));
        } else if (id == R.id.nav_mine &&
                !(frag instanceof RequestsFragment && ((RequestsFragment)frag).isLoggedInUser())) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(RequestsFragment.LOGGED_IN_USER, true);
            Fragment reqFrag = new RequestsFragment();
            reqFrag.setArguments(bundle);
            showFragment(reqFrag, false, new TransitionParams("", getString(R.string.requests_fragment_tag)));
        } else if (id == R.id.nav_profile && !(frag instanceof ProfileFragment)) {
            showFragment(ProfileFragment.newInstance(mApi.getId()), false, new TransitionParams("", getString(R.string.profile_fragment_tag)));
        } else if (id == R.id.nav_card && !(frag instanceof PaymentInfoFragment)) {
            showFragment(new PaymentInfoFragment(), true, new TransitionParams("", getString(R.string.payment_fragment_tag)));
        } else if (id == R.id.nav_settings && !(frag instanceof SettingsFragment)) {
            showFragment(new SettingsFragment(), false, new TransitionParams("", getString(R.string.settings_fragment_tag)));
        } else if (id == R.id.nav_logout) {
            mApi.logout();
            showLogin();
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Select the navigation item at pos
     * @param pos
     */
    private void navSelect(int pos) {
        for(int i=0; i<navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        navigationView.getMenu().getItem(pos).setChecked(true);
    }

    /**
     * Show the login screen
     */
    private void showLogin() {
        navSelect(0);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        showFragment(new LoginFragment(), false, new TransitionParams("", getString(R.string.login_fragment_tag)));
    }

    /**
     * Setup the app after the user logs in
     */
    public void onLogin() {
        Log.i(TAG, "onLogin");
        notificationHandler = new NotificationHandler(getApplicationContext(), mApi.getId());
        notificationHandler.startNotificationCheck();

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        refreshNavHeader();
        navSelect(0);

        handleInitialRouting();
    }

    /**
     * Show an informative dialog
     * @param title
     * @param message
     * @param callback something to run when done with dialog
     */
    public void showInfoDialog(String title, String message, final Callable<Void> callback) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.information)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if(null != callback) {
                                    try {
                                        callback.call();
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            }
                        })
                .show();
    }

    /**
     * Show an error dialog
     * @param title
     * @param message
     */
    public void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.alert_circle)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .show();
    }

    /**
     * Create an indeterminate progress dialog (spinner) with a message
     * @param message
     * @return created dialog
     */
    public ProgressDialog showProgressDialog(String message) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * Generate the hash of the signing key, required for FB auth
     */
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

    private void handleInitialRouting(){
        Intent intent = getIntent();

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if(intent.hasExtra(getString(R.string.notification_type))){
            String notificationType = intent.getStringExtra(getString(R.string.notification_type));

            if(notificationType.equals("review")){
                //getSupportFragmentManager().popBackStack();
                showFragment(ProfileFragment.newInstance(mApi.getId()), false, new TransitionParams("", getString(R.string.profile_fragment_tag)));
            }
            else if(notificationType.equals("request")){
                Bundle bundle = new Bundle();
                bundle.putBoolean(RequestsFragment.LOGGED_IN_USER, true);
                Fragment reqFrag = new RequestsFragment();
                reqFrag.setArguments(bundle);
                showFragment(reqFrag, false, new TransitionParams("", getString(R.string.requests_fragment_tag)));
            }
        }
        else if(!(frag instanceof RequestFragment)){
            Log.i(TAG, "Showing requests fragment after login");
            showFragment(new RequestsFragment(), false, new TransitionParams(getString(R.string.login_fragment_tag), getString(R.string.requests_fragment_tag)));
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(notificationHandler != null) {
            notificationHandler.stopNotificationCheck();
        }
    }
}