/*
 *    This file is part of openFileManager.
 *
 *    openFileManager is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    openFileManager is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.*
 *
 *    You should have received a copy of the GNU General Public License
 *    along with openFileManager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.linuxmotion.filemanager;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.models.fragments.SingleViewFragment;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;
import org.linuxmotion.filemanager.utils.Constants;


public class OpenFileManagerActivity extends ListActivity implements Alerts.GPLAlertClickDispatcher {

    private static final String TAG = OpenFileManagerActivity.class.getSimpleName();
    private static final boolean DEBUG = (true || Constants.FULL_DBG);


    private Alerts mAlerts;
    private boolean mShowGPL = true;
    private SingleViewFragment mSingleView;
    private boolean mDualPane;

    /**
     * Called when the activity is first created.
     */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogWrapper.Logi(TAG, "onCreate called");

        mDualPane = false;


        if (mDualPane) {


        } else {

            if(savedInstanceState == null){
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(android.R.id.content, new SingleViewFragment());
                ft.commit();
            }

        }
        setupEULA(this);




    }

    @Override
    public void onStart() {
        super.onStart();
        LogWrapper.Logi(TAG, "onStart called");

        // Show GPL usage license
        if (mShowGPL)
            mAlerts.ShowGPLAlert();

    }

    @Override
    public void onStop() {
        super.onStop();
        LogWrapper.Logi(TAG, "onStop called");

    }


    @Override
    public void onBackPressed() {
        LogWrapper.Logi(TAG, "onBackPressed called");

        if (mDualPane) {


        } else {
            Fragment frag = getFragmentManager().findFragmentById(android.R.id.content);
            // It should always be the single view fragment
            // if this point is reached, check anyway
            if (frag instanceof SingleViewFragment) {
                ((SingleViewFragment) frag).onBackPressed();
                return;
            }
            LogWrapper.Loge(TAG, "Instance of SingleViewFragment was not found");
            throw new RuntimeException("Instance of SingleViewFragment was not found");

        }


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogWrapper.Logi(TAG, "onConfigurationChanged called");
       // if (mStubIsInflated && (mInflatedStub.getVisibility() == View.VISIBLE)) {
       //     handleEmptyListBG();
       // }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogWrapper.Logi(TAG, "onDestroy called");

        // If the activity is destroy but the back
        // button is not pressed the exit status
        // still needs to be set
        PreferenceUtils.resetExitStatus(this);

    }

    private void log(String message) {

        if (DEBUG) Log.d(TAG, message);

    }


    private void setupEULA(Context context) {
        mAlerts = new Alerts(context);
        mAlerts.setGPLDispatcher(this);
        mShowGPL = Alerts.shouldIssueGPLLicense(context);


    }


    @Override
    public void onAgreeSelected() {

        SharedPreferences prefs = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(Constants.APP_NAME, Constants.VERSION_LEVEL);
        edit.commit();
        mShowGPL = false;

    }

    @Override
    public void onQuitSelected() {

        SharedPreferences prefs = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(Constants.APP_NAME, -1);
        edit.commit();
        finish();
        // TODO Auto-generated method stub

    }


}
