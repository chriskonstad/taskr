package com.example.guillaumelam34.taskrclient;


import android.os.Bundle;

public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load pref XML file
        addPreferencesFromResource(R.xml.preferences);
    }
}
