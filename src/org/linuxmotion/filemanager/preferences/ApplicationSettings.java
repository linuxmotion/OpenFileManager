package org.linuxmotion.filemanager.preferences;

import org.linuxmotion.filemanager.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;



public class ApplicationSettings extends PreferenceActivity {
	


	// checkbox prefrences
	
	// Show/Hide hidden folders
	// 

	//ListPreferences

	// Arrange file/folders by
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.layout.application_settings);
	}
	
	

}
