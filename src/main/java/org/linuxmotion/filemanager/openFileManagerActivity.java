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

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.models.FileArrayAdapter;
import org.linuxmotion.filemanager.models.MenuAction;
import org.linuxmotion.filemanager.openFileManagerBroadcastReceiver.openFileManagerReceiverDispatcher;
import org.linuxmotion.filemanager.preferences.ApplicationSettings;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Vector;



import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.app.SherlockListActivity;

public class openFileManagerActivity extends SherlockListActivity implements Alerts.GPLAlertClickDispatcher,
        Alerts.deleteAlertClickDispatcher, openFileManagerReceiverDispatcher {

    private static final String TAG = openFileManagerActivity.class.getSimpleName();
    private static final boolean DEBUG = (true || Constants.FULL_DBG);

    /*Standard variable components*/
    private String mCurrentPath;
    private static Vector<String> sLastPath;
    private static int mCurrentLocation;

    private boolean mFirstView = true;
    private boolean mShowGPL = true;
    private boolean mAboutToExit = false;
    private boolean mStubIsInflated = false;

    /*Standard UI components*/
    LinearLayout mInflatedStub;
    private ListView mList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    /*Custom components*/
    private static openFileManagerBroadcastReceiver sReceiver;
    private Alerts mAlerts;

    private static final int NOTIFY_DATA_CHANGED = 5;

    public final Handler mUIRefresher = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.REFRESH_UI:
                    createBroadCast(mCurrentPath);

                case Constants.UNKNOWN_FILE_TYPE:
                    unknown();

                case NOTIFY_DATA_CHANGED: {

                }
            }
        }

    };


    /**
     * Called when the activity is first created.
     */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAlerts = new Alerts(this);
        mAlerts.setGPLDispatcher(this);
        mAlerts.setDeleteDispatcher(this);

        setContentView(R.layout.main);

        if (mFirstView) {
            log("First time starting, or restarting");
            mFirstView = false;
            mCurrentPath = Constants.SDCARD_DIR;
            sLastPath = new Vector<String>();
        }
        // Show GPL usage license
        mShowGPL = Alerts.shouldIssueGPLLicense(this);
        if (mShowGPL) mAlerts.ShowGPLAlert();

        ListAdapter adapter = createAdapter(mCurrentPath);

        if (adapter != null) {
            setListAdapter(adapter);
            mList = (ListView) findViewById(android.R.id.list);
            mList.setLongClickable(true);
            mList.setAdapter(adapter);
            registerForContextMenu(mList);
        }


        setupActionBar();
        setupListView();

    }


    private void  setupListView() {



        //mDrawerList
    //    mDrawerList


    }

    private void setupActionBar() {

        mDrawerList = (ListView)findViewById(R.id.left_drawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.open,  /* "open drawer" description */
                R.string.closed  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle("Closed");
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle("Open");
                super.onDrawerOpened(drawerView);
            }
        };



        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setLogo(R.drawable.ic_drawer);
        getSupportActionBar().setTitle(mCurrentPath);



    }


    @Override
    public void onStart() {
        super.onStart();
        log("On start called - registering receiver");

        sReceiver = new openFileManagerBroadcastReceiver().setDispatcher(this);

        IntentFilter filter = new IntentFilter(Constants.UPDATE_INTENT);
        filter.addAction(Constants.RESOURCE_VIEW_INTENT);
        registerReceiver(sReceiver, filter);

        // Always update the adapter when navigating back
        // Something may have changed, whether it
        // be a preference or a new file
        updateAdapter(mCurrentPath);

    }

    @Override
    public void onStop() {
        super.onStop();

        log("On stop called - unregistering receiver");
        unregisterReceiver(sReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getSupportMenuInflater();
        inflater.inflate(R.menu.action_bar_default, menu);
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            //Log.d(TAG,(String) adapt.getItem(0) );
            menu.setHeaderTitle(R.string.file_options);
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }



    //@Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        File f = (File) getListAdapter().getItem(info.position);
        log(f.toString());
        switch (menuItemIndex) {

            case 0: {
                performClick(f);
                break;
            }
            case 2: {
                mCurrentPath = f.getPath();
                mAlerts.showDeleteAlertBox(f);
                break;
            }
            case 3: {
                log("Item number is " + menuItemIndex);
                break;
            }

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        log("Back button pressed");

        mAboutToExit = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0).getBoolean(Constants.ABOUT_TO_EXIT, false);
        if (mAboutToExit) {
            log("About to Exit");
            mAboutToExit = false;
            mFirstView = true;

            finish();


        } else {

            File f = new File(mCurrentPath);
            if (f.getParent() != null) Log.d(TAG, f.getParent());


            if (f.getParent() == null) {

                Toast.makeText(this, "Press back once more to exit", Toast.LENGTH_SHORT).show();
                PreferenceUtils.setExitStatus(this);

            } else if (f.getParent().equals("/")) {

                Log.d(TAG, "Set exit status. Dir = " + f.getParent());
                createBroadCast(f);

            } else {
                Log.d(TAG, f.getParent());
                createBroadCast(f);


            }
        }

    }

    public void createBroadCast(String path) {

        createBroadCast(new File(path));

    }

    private void createBroadCast(File path) {

        Intent updateintent = new Intent(Constants.UPDATE_INTENT);
        if (path.getParentFile() != null) updateintent.putExtra("PATH", path.getParent());
        else updateintent.putExtra("PATH", "/");
        PreferenceUtils.resetExitStatus(this);
        sendBroadcast(updateintent);

    }

    /**
     * Creates and returns a list adapter for the current list activity
     *
     * @return
     */
    protected FileArrayAdapter createAdapter(String path) {

        // Instatitate a adapter object
        return new FileArrayAdapter(this.getBaseContext(), R.layout.simple_text_view, new File(path));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        log("List item clicked");


        File f = (File) getListAdapter().getItem(position);
        performClick(f);


    }

    private void performClick(File f) {

        if (f.isDirectory()) {

            Log.d(TAG, "Sending UI refresh broadcast [dir=" + f.toString() + "]");
            PreferenceUtils.resetExitStatus(this);
            sendBroadcast(prepareBroadcast(f.getPath(),Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));

        } else {

            Log.d(TAG, "Sending media broadcast");
            sendBroadcast(prepareBroadcast(f.toString(),Constants.RESOURCE_VIEW_INTENT, null));


        }


    }

    public Intent prepareBroadcast(String f, String type, MenuAction action){

        Intent intent = new Intent(Constants.RESOURCE_VIEW_INTENT);

        if(type.equals(Constants.UPDATE_INTENT)){
            intent = new Intent(Constants.UPDATE_INTENT);
            intent.putExtra("PATH", f);
            intent.putExtra("ACTION", action.getCurrentAction());

        }
        else if(type.equals(Constants.RESOURCE_VIEW_INTENT)){
            intent.putExtra("RESOURCE", f);

        }




        return intent;

    }

    @Override
    public void onAgreeSelected() {

        SharedPreferences prefs = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(Constants.APP_NAME, Constants.VERSION_LEVEL);
        edit.commit();


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

    @Override
    public void onSelectedDelete(File f) {


        if (f.delete()) {

            Toast.makeText(getApplicationContext(), "Your file has been deleted", Toast.LENGTH_SHORT).show();
            mUIRefresher.sendEmptyMessage(Constants.REFRESH_UI);
        } else {

            // Did not delete file
            // post a handler
            Toast.makeText(getApplicationContext(), "Your file has been not deleted", Toast.LENGTH_SHORT).show();


        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:{

                if (mDrawerLayout.isDrawerOpen(mDrawerList))
                {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
            }
            break;
            case R.id.menu_settings:
                launchSettingMenu();
                break;
            case R.id.menu_left:{
                sendBroadcast(prepareBroadcast(mCurrentPath, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_BACK)));
            }
            case R.id.menu_up:{
                onBackPressed();
            }

            case R.id.menu_forward:{
                sendBroadcast(prepareBroadcast(mCurrentPath, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));
            }

        }

        log("Item = " + item.getItemId());
        return (super.onOptionsItemSelected(item));
    }

    private void launchSettingMenu() {
        Intent settings = new Intent(this, ApplicationSettings.class);
        startActivity(settings);
    }

    @Override
    public void dispatchPathUpdate(String updatePath, int action) {

        log("Standard UI refresh");

        String newUpdatePath = null;
        LogWrapper.Logv(TAG, "Got action "+ action);
        switch(action){

            case  MenuAction.ACTION_BACK: {
                mCurrentLocation--;

            };
             break;
            case  MenuAction.ACTION_UP: {
                mCurrentLocation--;
            };
            break;
            case MenuAction.ACTION_FORWARD:{

                //check to see if we are at the top
                LogWrapper.Logv(TAG, "SlastPath.size = " + sLastPath.size() );
                LogWrapper.Logv(TAG, "Current loaction = " +mCurrentLocation);
                if(sLastPath.size() == mCurrentLocation){
                    // it seems we are not at the top
                    // We have a new top
                    sLastPath.setSize(mCurrentLocation+1);
                    // Add the next location
                    newUpdatePath = updatePath;
                    sLastPath.add(mCurrentPath);
                    mCurrentLocation++;
                }
                else{
                    // it seems we are at the top

                    // is the forawrd path the top path
                    if(!mCurrentPath.equals(updatePath)){
                        // The update path is not the top path
                        // do nothing
                        sLastPath.add(updatePath);
                        mCurrentPath = updatePath;
                        newUpdatePath = mCurrentPath;
                        mCurrentLocation++;
                    }

                    // reminder
                    // newUpdatePath = null

                }

            };
            break;
        }

        updateAdapter(newUpdatePath);



        if (getListAdapter().isEmpty()) {

            // Check to see if the adapter is empty
            if (!mStubIsInflated) {
                ViewStub Stub = (ViewStub) findViewById(R.id.stub);
                mInflatedStub = (LinearLayout) Stub.inflate();
                handleEmptyListBG();
                mStubIsInflated = true;

            } else if (mStubIsInflated && (mInflatedStub.getVisibility() == View.GONE)) {
                mInflatedStub.setVisibility(View.VISIBLE);
                handleEmptyListBG();

            }

        } else {
            if (mStubIsInflated && (mInflatedStub.getVisibility() == View.VISIBLE)) {
                mInflatedStub.setVisibility(View.GONE);
            }

        }


    }

    private void handleEmptyListBG() {

        ImageView EmptyBG = (ImageView) mInflatedStub.findViewById(R.id.Empty_List_Image_View);
        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            EmptyBG.setBackgroundResource(R.drawable.list_bg_empty_port);
        } else {

            EmptyBG.setBackgroundResource(R.drawable.list_bg_empty_land);

        }

    }

    private void updateAdapter(String path) {

        if(path == null)
            return;
        log("Normal UI refresh");
        AdapterThread update = new AdapterThread(path, (FileArrayAdapter) getListAdapter(), getApplicationContext());
        update.run();
        getSupportActionBar().setTitle(mCurrentPath);

    }


    class AdapterThread extends Thread {

        WeakReference<FileArrayAdapter> mWList;
        WeakReference<Context> mWContext;
        String mPath;

        AdapterThread(String path, FileArrayAdapter adapter, Context context) {

            mWContext = new WeakReference<Context>(context);
            mWList = new WeakReference<FileArrayAdapter>(adapter);
            mPath = path;
        }

        @Override
        public void run() {
            log("the current path is " + mPath);
            // Retireve the files in the directory specified by path
            File[] files = FileUtils.getFilesInDirectory(mPath, mWContext.get());
            // Sort the files

            FileArrayAdapter list = mWList.get();
            // clear the adapter
            list.clear();
            if (files != null && files.length > 0) {
                // TODO: Make this faster for large data sets
                // Perceived lag on data sets > 1000
                // unkown for smaller data sets
                // when perceived lag starts
                log("Files " + files.length + "");
                try{
                files = FileUtils.sortFiles(files, mWContext.get());
                }
                catch(RuntimeException ex){
                    // Somthing happemed while processing
                    // the file listing
                    // Tell the user to submit a bug report
                    LogWrapper.Loge(TAG,ex);

                    // nasty hack to show error without a dialog
                   list.add(new File("ERROR OCCURED"));
                    list.notifyDataSetChanged();
                    return;

                }

                log("Files " + files.length + " after sorting");

                for (File f : files) {
                    list.add(f);
                    list.notifyDataSetChanged();
                }

            } else {

                log("No files to sort");
            }
            // Manually update the list
            list.updateList();
            // notify the adapter that the data has change
            //list.notifyDataSetChanged();

        }




    }

    @Override
    public void dispatchResourceUpdate(String resourcePath) {


        log("DispatchResourceUpdate()");
        MimeTypeMap MIME = MimeTypeMap.getSingleton();


        String mimetype = MIME.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(resourcePath));
        if (mimetype != null) {
            Intent resourceintent = new Intent(Intent.ACTION_VIEW);
            resourceintent.setDataAndType(Uri.parse("file://" + resourcePath), mimetype);
            try {
                startActivity(resourceintent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                mUIRefresher.sendEmptyMessage(Constants.UNKNOWN_FILE_TYPE);
            }
        } else
            mUIRefresher.sendEmptyMessage(Constants.UNKNOWN_FILE_TYPE);


    }


    private void unknown() {

        Toast.makeText(this, "Unknown file type", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mStubIsInflated && (mInflatedStub.getVisibility() == View.VISIBLE)) {
            handleEmptyListBG();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        log("Destroying the activity");

        // Set all static variables to null
        // for garbage collection

        sLastPath = null;
        sReceiver = null;

        // If the activity is destroy but the back
        // button is not pressed the exit status
        // still needs to be set
        PreferenceUtils.resetExitStatus(this);

    }


    private void log(String message) {

        if (DEBUG) Log.d(TAG, message);

    }




}
