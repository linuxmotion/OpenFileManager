package org.linuxmotion.filemanager.preferences;

import org.linuxmotion.filemanager.utils.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
	
	
	
	   public static void resetExitStatus(Context context){
	    	
	    	
	    	SharedPreferences prefs = context.getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean(Constants.ABOUT_TO_EXIT, false);
			edit.commit();
	    	
	    	
	    }
	   
	    public static void setExitStatus(Context context) {
			
	    	SharedPreferences prefs = context.getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean(Constants.ABOUT_TO_EXIT, true);
			edit.commit();
			
		}
	
	
	

}
