package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public class SettingsActivity extends Activity {

    public static final String USERNAME_KEY = Settings.CHAT_NAME_KEY;

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
            EditTextPreference edit = (EditTextPreference) findPreference(key);
            String s = edit.getText();
            Settings.saveChatName(s);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the messages content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}