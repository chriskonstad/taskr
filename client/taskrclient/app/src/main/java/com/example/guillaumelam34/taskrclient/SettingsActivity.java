package com.example.guillaumelam34.taskrclient;


import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load pref XML file
        addPreferencesFromResource(R.xml.preferences);
    }
}
