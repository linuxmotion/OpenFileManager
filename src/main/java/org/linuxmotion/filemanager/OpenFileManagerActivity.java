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


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.database.DataBaseHelper;
import org.linuxmotion.filemanager.models.MenuAction;
import org.linuxmotion.filemanager.models.adapters.ImageArrayAdapter;
import org.linuxmotion.filemanager.models.baseadapters.ExpandableBaseArrayAdapter;
import org.linuxmotion.filemanager.models.fragments.CutPasteFragment;
import org.linuxmotion.filemanager.models.fragments.SideNavigationFragment;
import org.linuxmotion.filemanager.models.fragments.SingleViewFragment;
import org.linuxmotion.filemanager.preferences.ApplicationSettings;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;
import org.linuxmotion.filemanager.utils.Constants;

import java.io.File;



public class OpenFileManagerActivity extends Activity implements Alerts.GPLAlertClickDispatcher,
        SingleViewFragment.ContextualActionBarMenu, CutPasteFragment.onPasteListener,
        SideNavigationFragment.GroupClickCallback, SideNavigationFragment.ChildClickCallback,
        SideNavigationFragment.OnFavoritesCallback, DataBaseHelper.onDatabaseTransactionFinished {

    private static final String TAG = OpenFileManagerActivity.class.getSimpleName();
    private static final boolean DEBUG = (true || Constants.FULL_DBG);


    private Alerts mAlerts;
    private boolean mShowGPL = true;
    private SingleViewFragment mSingleView;
    private CutPasteFragment mCutPasteFragment;
    private SideNavigationFragment mSideNavigationFragment;
    private boolean mTabletMode = false;
    private SlidingMenu mSlidingMenu;
    private boolean mAttached = false;
    DataBaseHelper mHelper= new DataBaseHelper();
    /**
     * Called when the activity is first created.
     */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogWrapper.Logi(TAG, "onCreate called");

        // Setting the content view
        setContentView(R.layout.layout_main_content);
        mSingleView = (SingleViewFragment) getFragmentManager().findFragmentById(R.id.main_fragment_container);

        if(findViewById(R.id.sidenavigation_fragment_container) != null){
            mTabletMode = true;
            LogWrapper.Logd(TAG, "Starting app in tablet mode");
        }
        else{
            LogWrapper.Logd(TAG, "Starting app in phone mode");
        }
        //mTabletMode = true;




        mHelper.initDatabase(this, this);
        mHelper.open();



        if (mTabletMode) {



            // mSingleView = (SingleViewFragment) getFragmentManager().findFragmentById(R.id.main_fragment_container);
             mCutPasteFragment = new CutPasteFragment();
             mSideNavigationFragment = new SideNavigationFragment();
             FragmentTransaction ft = getFragmentManager().beginTransaction();
             ft.replace(R.id.main_fragment_container, mSingleView);
             ft.replace(R.id.sidenavigation_fragment_container, mSideNavigationFragment);
             ft.replace(R.id.cut_paste_fragment_container, mCutPasteFragment);
             ft.commit();
             showHideFragment(mCutPasteFragment, false);
            //



        } else {

            if (savedInstanceState == null) {
               // mSingleView = new SingleViewFragment();
               // FragmentTransaction ft = getFragmentManager().beginTransaction();
               // ft.replace(android.R.id.content, mSingleView);
               // ft.commit();
               // mSingleView.setContextualActionBarMenuInterface(this);

            }

            setupSlidingMenu();


        }
        mSingleView.setContextualActionBarMenuInterface(this);
        setupFragments();
        setupEULA(this);

        //always setup the action bar
        setupActionBar(mTabletMode);


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

        mHelper.close();
    }


    @Override
    public void onBackPressed() {
        LogWrapper.Logi(TAG, "onBackPressed called");

        if (mTabletMode) {
            mSingleView.onBackPressed();


        } else {

            // Is the menu showing
            if (mSlidingMenu.isMenuShowing()) {
                // if a menu is open it need to closed.
                mSlidingMenu.showContent();

            } else {

                mSingleView.onBackPressed();


            }


        }

        return;

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
    public boolean onOptionsItemSelected(MenuItem item) {

        LogWrapper.Logv(TAG, "Item " + item.getItemId());
        switch (item.getItemId()) {

            case android.R.id.home: {

                mSlidingMenu.toggle();
            }
            break;
            case R.id.menu_settings:
                launchSettingMenu();
                break;
            case R.id.menu_left: {
                sendBroadcast(SingleViewFragment.prepareBroadcast(null, Constants.UPDATE_INTENT,
                        new MenuAction(MenuAction.ACTION_BACK), null));
            }
            break;

            case R.id.menu_new_content: {
                mSingleView.createNewFileorDirectory();
            }
            break;
            case R.id.menu_forward: {
                sendBroadcast(SingleViewFragment.prepareBroadcast(null, Constants.UPDATE_INTENT,
                        new MenuAction(MenuAction.ACTION_FORWARD), null));
            }
            break;

        }

        return (super.onOptionsItemSelected(item));
    }

    private void launchSettingMenu() {
        Intent settings = new Intent(this, ApplicationSettings.class);
        startActivity(settings);
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


    private void setupActionBar(boolean tabletMode) {

        if(!tabletMode){
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }
        getActionBar().setTitle(mSingleView.getCurrentPath());


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

    }


    private void setupSlidingMenu() {

        mSlidingMenu = new SlidingMenu(this, SlidingMenu.LEFT);
        //mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        //mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        //mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setMenu(R.layout.layout_side_navigation);
        mSlidingMenu.setFadeDegree(0.35f);
        // Set the menu but don't activate it
        mSlidingMenu.setSecondaryMenu(R.layout.layout_cut_paste_menu);
        mSlidingMenu.getSecondaryMenu().setVisibility(View.INVISIBLE);


        if (!PreferenceUtils.getHasCompletedLeftNavigationTutorial(this)) {

            mSlidingMenu.toggle();
            // Show sliding menu
            PreferenceUtils.putHasCompletedLeftNavigationTutorial(this, true);
        }

        // Find the fragments that are now in the view hierarchy
        mCutPasteFragment =
                (CutPasteFragment) getFragmentManager().findFragmentById(R.id.fragment_cut_paste);
        mSideNavigationFragment = (SideNavigationFragment)
                getFragmentManager().findFragmentById(R.id.fragment_side_navigation);
        // mSlidingMenu.setOnClosedListener(this);
        // mSlidingMenu.setOnOpenedListener(this);
    }


    private void setupFragments() {
        // Both tablet and phone have the same callbacks
        mCutPasteFragment.setPasteListener(this);
        mSideNavigationFragment.setChildCallback(this);
        mSideNavigationFragment.setGroupCallback(this);
        mSideNavigationFragment.setOnFavoriteAddedCallback(this);

    }

    @Override
    public void OnCutCallback(File[] files) {

        if (mTabletMode) {


            showHideFragment(mCutPasteFragment, true);

        } else {
            mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        }
        // Refresh the entire adapter
        ((ImageArrayAdapter) mCutPasteFragment.getAdapter()).clear();
        ImageArrayAdapter adapter = (ImageArrayAdapter) mCutPasteFragment.getAdapter();
        // get the entire list of held files
        // includes the appended files if any
        //files = mFileAction.getHeldFiles();
        for (File f : files) {
            adapter.add(f);

        }
        // Notify the adapter that it has changed
        adapter.notifyDataSetChanged();

    }

    @Override
    public void OnFirstTimeCutCallback() {
        if (mTabletMode) {

            return;
        }
        mSlidingMenu.showSecondaryMenu();
    }

    @Override
    public void onPaste(File[] files) {
        if (mTabletMode) {
            showHideFragment(mCutPasteFragment, false);
            return;
        } else {
            mSlidingMenu.setMode(SlidingMenu.LEFT);
            mSlidingMenu.showContent();
        }
        mSingleView.pasteCutItems();
        ((ImageArrayAdapter) mCutPasteFragment.getAdapter()).clear();
        ((ImageArrayAdapter) mCutPasteFragment.getAdapter()).notifyDataSetChanged();
        mSingleView.updateAdapter(mSingleView.getCurrentPath());
    }

    private void showHideFragment(Fragment frag, boolean show) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(show){
            ft.show(frag);
            findViewById(R.id.cut_paste_fragment_container).setVisibility(View.VISIBLE);
        }else{

            ft.hide(frag);
            findViewById(R.id.cut_paste_fragment_container).setVisibility(View.GONE);
        }

        ft.commit();
    }


    @Override
    public void onCancelPaste() {
        if (mTabletMode) {
            showHideFragment(mCutPasteFragment, false);
            return;
        } else {
            mSlidingMenu.setMode(SlidingMenu.LEFT);
            mSlidingMenu.showContent();
        }
        mSingleView.pasteSelectionCanceled();

        ((ImageArrayAdapter) mCutPasteFragment.getAdapter()).clear();
        ((ImageArrayAdapter) mCutPasteFragment.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean OnGroupClick(int groupPosition) {
        if (mTabletMode) {

            return false;
        } else {
            mSingleView.selectDrawerItem(groupPosition);
            mSlidingMenu.showContent();
            return true;
        }

    }

    @Override
    public boolean OnChildClick(String childFilePath, int groupPosition, int childInGroup) {
        if (mTabletMode) {

            return false;
        } else {
            mSingleView.performClick(new File((childFilePath)));
            if(mSlidingMenu.isMenuShowing())
                mSlidingMenu.toggle();

            return false;
        }
    }

    @Override
    public void OnChildItemLongClicked(int childPosition) {

    }

    @Override
    public void OnFavoriteAdded(String path, int child) {
        if (mTabletMode) {

            return;
        } else {

        }

        mHelper.AddToList(path);

       // PreferenceUtils.putFavorite(this, child, path);
    }

    @Override
    public void OnFavoriteRemoved(String path, int group, int child) {
        LogWrapper.Logi(TAG, "OnFavoriteRemoved() called");
        if (mTabletMode) {

            return;
        } else {

        }
        mHelper.RemoveFromList(path);
    }

    @Override
    public String[] OnFavoritesInitialized() {
        String[] favorites = mHelper.getAllEntries();
        LogWrapper.Logv(TAG, "Found " + favorites.length + " favorites to add");
        return favorites;
    }

    @Override
    public void OnMenuFavoriteCallback(ExpandableBaseArrayAdapter.Child child) {
        mSideNavigationFragment.AddFavorite(child);
    }

    @Override
    public boolean OnRenameCallback() {
        if (mTabletMode) {


        } else {

        }
        return false;
    }

    @Override
    public void onTransactionFinished(int action) {
        switch (action){
            case DataBaseHelper.onDatabaseTransactionFinished.TRANSACTION_ADD:{
                LogWrapper.Logd(TAG, "Succesfully added favorite to database");

            }break;
            case DataBaseHelper.onDatabaseTransactionFinished.TRANSACTION_DELETE:{
                LogWrapper.Logd(TAG, "Succesfully delete favorite to database");

            }break;
            case DataBaseHelper.onDatabaseTransactionFinished.TRANSACTION_INCOMPLETE:{
                LogWrapper.Logd(TAG, "Database transaction was incomplete");

            }break;
        }
    }
}
