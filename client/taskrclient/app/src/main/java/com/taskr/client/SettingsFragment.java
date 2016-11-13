package com.taskr.client;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.Menu;

public class SettingsFragment extends PreferenceFragmentCompat {
    private String TAG;
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        TAG = getString(R.string.settings_fragment_tag);
        // Ensure we get out onPrepareOptionsMenu called back
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide the overflow menu because we don't want that in settings
        for(int i=0; i<menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.title_activity_settings));
    }

}
