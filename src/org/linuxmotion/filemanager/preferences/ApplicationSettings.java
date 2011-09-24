package org.linuxmotion.filemanager.preferences;

import org.linuxmotion.filemanager.R;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;



public class ApplicationSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	
	private String TAG = this.getClass().getSimpleName();


	// checkbox prefrences
	CheckBoxPreference mHiddenFilesAndFoldersPreference;
	CheckBoxPreference mSortbyFileThenFolderPreference;
	
	// Show/Hide hidden folders
	// 

	//ListPreferences

	// Arrange file/folders by
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        addPreferencesFromResource(R.layout.application_settings);
        
        mHiddenFilesAndFoldersPreference = (CheckBoxPreference) findPreference(getString(R.string.hidden_files_folder_pref));
        mHiddenFilesAndFoldersPreference.setOnPreferenceChangeListener(this);
        
        mSortbyFileThenFolderPreference = (CheckBoxPreference) findPreference(getString(R.string.sort_file_folder_pref));
        mSortbyFileThenFolderPreference.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		
		
		
		if(arg0.equals(mHiddenFilesAndFoldersPreference)){
			Log.d(TAG, "Hidden File folders preference changed");
			
			if(mHiddenFilesAndFoldersPreference.isChecked()){
				mHiddenFilesAndFoldersPreference.setChecked(false);
				PreferenceUtils.showHiddenFilesFolders(getApplicationContext());
				
			}
			else{
				mHiddenFilesAndFoldersPreference.setChecked(true);
				PreferenceUtils.hideHiddenFilesFolders(getApplicationContext());
			}
			
		}
		
		if(arg0.equals(mSortbyFileThenFolderPreference)){

			Log.d(TAG, "Sorting preference changed");
			if(mSortbyFileThenFolderPreference.isChecked()){
				
				mSortbyFileThenFolderPreference.setChecked(false);
				PreferenceUtils.sortFilesFolders(getApplicationContext());
			}
			else{
				mSortbyFileThenFolderPreference.setChecked(true);
				PreferenceUtils.sortFoldersFiles(getApplicationContext());
			}
			
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
