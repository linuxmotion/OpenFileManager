package org.linuxmotion.filemanager.models;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.baseadapters.ExpandableBaseArrayAdapter;
import org.linuxmotion.filemanager.openFileManagerBroadcastReceiver;
import org.linuxmotion.filemanager.preferences.ApplicationSettings;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by john on 6/26/13.
 */
public class SingleViewFragment extends Fragment implements Alerts.deleteAlertClickDispatcher, openFileManagerBroadcastReceiver.openFileManagerReceiverDispatcher {


    private static final String TAG = SingleViewFragment.class.getSimpleName();
    private static final boolean DEBUG = (true || Constants.FULL_DBG);
    private static final int NOTIFY_DATA_CHANGED = 5;

    private ListView mList;
    private LinearLayout mContentLayout;
    private ExpandableListView mDrawerList;
    private String mCurrentPath;
    private Vector<String> mLastPath;
    private int mCurrentLocation = 0;
    private boolean mFirstView = true;
    private boolean mAboutToExit = false;
    private boolean mStubIsInflated = false;
    private static openFileManagerBroadcastReceiver sReceiver;
    private SlidingMenu mSlidingMenu;
    private  Alerts mDeleteAlert;
    ActionMode mActionMode;

    private LinearLayout mInflatedStub;

    public final Handler mUIRefresher = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.REFRESH_UI:
                    getActivity().sendBroadcast(prepareBroadcast(mCurrentPath, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));
                    //createBroadCast(mCurrentPath);

                case Constants.UNKNOWN_FILE_TYPE:
                    unknown();

                case NOTIFY_DATA_CHANGED: {

                }
            }
        }

    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_single_main, container, false);
        mContentLayout = (LinearLayout) v.findViewById(R.id.content_frame);
        return v;//super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupVariables();
        setupMainListView();
        setupActionBar();
        setupSlidingMenu();
        setupDrawerListView();


    }


    @Override
    public void onStart() {
        super.onStart();
        LogWrapper.Logv(TAG, "On start called - registering receiver");

        // Show GPL usage license

        sReceiver = new openFileManagerBroadcastReceiver().setDispatcher(this);

        IntentFilter filter = new IntentFilter(Constants.UPDATE_INTENT);
        filter.addAction(Constants.RESOURCE_VIEW_INTENT);
        getActivity().registerReceiver(sReceiver, filter);

        // Always update the adapter when navigating back
        // Something may have changed, whether it
        // be a preference or a new file
        updateAdapter(mCurrentPath);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();

        LogWrapper.Logi(TAG, "On stop called - unregistering receiver");
        getActivity().unregisterReceiver(sReceiver);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void onBackPressed() {
        LogWrapper.Logv(TAG, "Back button pressed");

        // Check to see if the drawer is open
        if (mSlidingMenu.isMenuShowing()) {
            // if it was we need to close it
            mSlidingMenu.showContent();
        } else {
            // If it was closed though we should
            // navigate up
            sendBroadcast(prepareBroadcast(null, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_UP)));
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //super.onCreateOptionsMenu(menu, inflater);
        LogWrapper.Logv(TAG, "Creating options menu");
        inflater.inflate(R.menu.action_bar_default, menu);
        //return true;
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
                sendBroadcast(prepareBroadcast(null, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_BACK)));
            }
            break;
            case R.id.menu_up: {
                onBackPressed();
                // same as
                // sendBroadcast(prepareBroadcast(null, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_UP)));
            }
            break;
            case R.id.menu_forward: {
                sendBroadcast(prepareBroadcast(null, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));
            }
            break;

        }

        return (super.onOptionsItemSelected(item));
    }


    @Override
    public void dispatchPathUpdate(String updatePath, int action) {

        LogWrapper.Logv(TAG, "Standard UI refresh");

        String newUpdatePath = getUpdatePath(updatePath, action);

        updateAdapter(newUpdatePath);


        if (mList.getAdapter().isEmpty()) {

            // Check to see if the adapter is empty
            if (!mStubIsInflated) {
                ViewStub Stub = (ViewStub) mContentLayout.findViewById(R.id.stub);
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

    private String getUpdatePath(String updatePath, int action) {
        String newUpdatePath = null;
        LogWrapper.Logv(TAG, "Got action " + action);
        // updatePath is null it mean that the action
        // bar was used to navigate
        // therefore we have no update path but must
        // figure out where to go

        switch (action) {

            case MenuAction.ACTION_BACK: {
                newUpdatePath = actionBack(updatePath);
            }
            ;
            break;
            case MenuAction.ACTION_UP: {
                newUpdatePath = actionUp();
            }
            ;
            break;
            case MenuAction.ACTION_FORWARD: {
                newUpdatePath = actionForward(updatePath);

            }
            ;
            break;
        }

        return newUpdatePath;
    }

    private String actionBack(String updatePath) {
        LogWrapper.Logv(TAG, "Action Back dispatched");
        // only go back if we are not at the start
        // Start is considered the home directory
        if (mCurrentLocation > 0) {
            updatePath = mLastPath.get(mCurrentLocation);
            mCurrentLocation--;
        }
        return updatePath;
    }

    private String actionUp() {
        LogWrapper.Logv(TAG, "Action up dispatched");
        // Either to onBackPressed() or action_up was presses
        String newUpdatePath = null;
        mAboutToExit = getActivity().getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0).getBoolean(Constants.ABOUT_TO_EXIT, false);
        if (mAboutToExit) {
            LogWrapper.Logv(TAG, "About to Exit");
            mAboutToExit = false;
            mFirstView = true;

            getActivity().finish();


        } else {

            LogWrapper.Logv(TAG, "The current path is = " + mCurrentPath);
            File f = new File(mCurrentPath);
            if (f.getParent() != null)
                Log.d(TAG, "Parent directory = " + f.getParent());
            else
                LogWrapper.Logv(TAG, "Parent directory is null");


            if (f.getParent() == null) {

                Toast.makeText(this.getActivity(), "Press back once more to exit", Toast.LENGTH_SHORT).show();
                PreferenceUtils.setExitStatus(this.getActivity());
                // mCurrentLocation--;

            } else if (f.getParent().equals("/")) {

                Log.d(TAG, "Setting exit status = true.");
                //mCurrentLocation--;
                newUpdatePath = f.getParent();
                mCurrentPath = newUpdatePath;

            } else {
                Log.d(TAG, f.getParent());
                //mCurrentLocation--;
                newUpdatePath = f.getParent();
                mCurrentPath = newUpdatePath;


            }
        }
        return newUpdatePath;
    }

    private String actionForward(String updatePath) {
        String newUpdatePath = null;
        LogWrapper.Logv(TAG, "Action forward dispatched");

        if (updatePath == null) {
            // It means the forward button was pressed

        } else {
            // it means a click was performed
            // updatePath is the dir we want to got to
            mCurrentPath = updatePath; // set the new current path
            newUpdatePath = mCurrentPath;

        }
        //check to see if we are at the top
        /*
        LogWrapper.Logv(TAG, "mLastPath.size = " + mLastPath.size() );
        LogWrapper.Logv(TAG, "Current loaction = " +mCurrentLocation);
        if(mLastPath.size() == mCurrentLocation){
            // it seems we are not at the top
            // We have a new top
            mLastPath.setSize(mCurrentLocation+1);
            // Add the next location
            newUpdatePath = updatePath;
            mLastPath.add(mCurrentPath);
            mCurrentLocation++;
        }
        else{
            // it seems we are at the top

            // is the forawrd path the top path
            if(!mCurrentPath.equals(updatePath)){
                // The update path is not the top path
                // do nothing
                mLastPath.add(updatePath);
                mCurrentPath = updatePath;
                newUpdatePath = mCurrentPath;
                mCurrentLocation++;
            }

            // reminder
            // newUpdatePath = null

        }
        */
        return newUpdatePath;
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

        if (path == null) {
            LogWrapper.Logd(TAG, "Couldnt find a path to update with");
            return;
        }
        LogWrapper.Logv(TAG, "Normal UI refresh");
        AdapterThread update = new AdapterThread(path, (FileArrayAdapter) mList.getAdapter(), this.getActivity());
        update.run();
        getActionBar().setTitle(mCurrentPath);

    }

    @Override
    public void onSelectedDelete(File f) {



        if (f.delete()) {

            Toast.makeText(this.getActivity(), "Your file has been deleted", Toast.LENGTH_SHORT).show();
            mUIRefresher.sendEmptyMessage(Constants.REFRESH_UI);
        } else {

            // Did not delete file
            // post a handler
            Toast.makeText(getActivity(), "Your file has been not deleted", Toast.LENGTH_SHORT).show();


        }
    }


    @Override
    public void dispatchResourceUpdate(String resourcePath) {


        LogWrapper.Logv(TAG, "DispatchResourceUpdate()");
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

        Toast.makeText(getActivity(), "Unknown file type", Toast.LENGTH_SHORT).show();
    }


    /***********************************/

    /**
     * Creates and returns a list adapter for the current list activity
     *
     * @return
     */
    protected FileArrayAdapter createAdapter(String path) {

        // Instatitate a adapter object
        return new FileArrayAdapter(this.getActivity(), new File(path));

    }


    private void setupVariables() {
        if (mFirstView) {
            LogWrapper.Logv(TAG, "First time starting, or restarting");
            mFirstView = false;
            mCurrentPath = Constants.SDCARD_DIR;
            mLastPath = new Vector<String>();
        }

        mDeleteAlert = new Alerts(getActivity());
        mDeleteAlert.setDeleteDispatcher(this);
    }


    private void setupMainListView() {
        ListAdapter adapter = createAdapter(mCurrentPath);

        if (adapter != null) {

            //setListAdapter(adapter);
            mList = (ListView) mContentLayout.findViewById(android.R.id.list);
            mList.setLongClickable(true);
            mList.setAdapter(adapter);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    LogWrapper.Logv(TAG, "List item clicked. position = [" + position + "]");

                        File f = (File) mList.getAdapter().getItem(position);
                        performClick(f);

                }
            });

            mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            // mList.setItemsCanFocus(false);
            mList.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {

                Boolean mMultiSelected = false;
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {


                    LogWrapper.Logi(TAG, "onItemCheckedStateChanged called");
                    LogWrapper.Logv(TAG, "Found " + mList.getCheckedItemCount()+ " checked items");
                    // Is the number of items check more than 1
                    // is the multislect flag already set
                    if(mList.getCheckedItemCount() > 1){
                        mMultiSelected = true;
                        LogWrapper.Logd(TAG, "Invalidating the actionmode");
                        mActionMode.invalidate();
                    }else if(mList.getCheckedItemCount() == 1) {
                        // Is the number of items check is 1
                        // reset multislect flag
                        mMultiSelected = false;
                        LogWrapper.Logd(TAG, "Invalidating the actionmode");
                       mActionMode.invalidate();
                    }



                    // Here you can do something when items are selected/de-selected,
                    // such as update the title in the CAB
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    LogWrapper.Logv(TAG, "Action item clicked from multi chioce");
                    // Respond to clicks on the actions in the CAB
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            deleteSelectedItems();

                            mode.finish(); // Action picked, so close the CAB
                            return true;
                        default:
                            return false;
                    }
                }


                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate the menu for the CAB
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_context_actions, menu);
                    mActionMode = mode;
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // Here you can make any necessary updates to the activity when
                    // the CAB is removed. By default, selected items are deselected/unchecked.
                    mActionMode = null;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    LogWrapper.Logi(TAG, "onPrepareActionMode called");
                    menu.clear();
                    MenuInflater inflater = mActionMode.getMenuInflater();
                    if (mMultiSelected){
                        inflater.inflate(R.menu.menu_context_actions_multiple_items, menu);
                    }else{
                        inflater.inflate(R.menu.menu_context_actions, menu);

                    }
                    // Here you can perform updates to the CAB due to
                    // an invalidate() request
                    return true;
                }
            });
        }


    }

    private void deleteSelectedItems(){
        SparseBooleanArray array = mList.getCheckedItemPositions();
       ArrayList<File> files = new ArrayList<File>();


        LogWrapper.Logi(TAG, "Found "+mList.getCheckedItemCount() + " checked items");

        SparseBooleanArray checked = mList.getCheckedItemPositions();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value){
                files.add ((File)mList.getAdapter().getItem(key));
                LogWrapper.Logv(TAG, "Found item with key " + key );
            }

        }

        File[] f = new File[files.size()];
        LogWrapper.Logv(TAG, "Preparing to delete files");
        for(int i = 0; i < files.size(); i++){
            f[i] = files.get(i);
            LogWrapper.Logv(TAG, "\t--" + f[i].toString());
        }

       mDeleteAlert.showDeleteAlertBox(f);



    }

    private void setupSlidingMenu() {
        mSlidingMenu = new SlidingMenu(this.getActivity());
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        //mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setMenu(R.layout.layout_listview);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this.getActivity(), SlidingMenu.SLIDING_CONTENT);


        // mSlidingMenu.setOnClosedListener(this);
        // mSlidingMenu.setOnOpenedListener(this);
    }


    private void setupDrawerListView() {


        String[] groups = {"Home", "SdCard", "Favorites"};
        String[][] children = { {}, {}, {"Fav 1", "Fav 2"} };
        mDrawerList = (ExpandableListView) mSlidingMenu.findViewById(R.id.left_drawer);

        ArrayList<ArrayList<ExpandableBaseArrayAdapter.Child>> childrenList = new  ArrayList<ArrayList<ExpandableBaseArrayAdapter.Child>>();

        for (int i = 0; i < groups.length; i++){

            // Fill the group array
            childrenList.add(new ArrayList<ExpandableBaseArrayAdapter.Child>());
            for (int j = 0; j < children[i].length; j++){
                // fill the children for the specifed groups
                childrenList.get(i).add(new ExpandableBaseArrayAdapter.Child(i, j, children[i][j], "/sdcard"));
            }

        }


        mDrawerList.setAdapter(new ExpandableDrawerListAdapter(getActivity(), groups, childrenList));

       // mDrawerList.setAdapter(new DrawerListAdapter(this.getActivity(), s));
        //mDrawerList.setOnChildClickListener();
        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {


            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogWrapper.Logv(TAG, "Drawer item clicked");


                ExpandableDrawerListAdapter adapter = (ExpandableDrawerListAdapter) mDrawerList.getExpandableListAdapter();

                if(adapter.hasChildren(groupPosition)){
                    if(mDrawerList.isGroupExpanded(groupPosition)){
                        mDrawerList.collapseGroup(groupPosition);

                    }else{

                        mDrawerList.expandGroup(groupPosition);
                    }


                }else{
                    selectDrawerItem(groupPosition);
                    mSlidingMenu.showContent();
                }

                return true;
            }

        });

        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){


            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int child, long id) {


                ExpandableDrawerListAdapter adapter = (ExpandableDrawerListAdapter) mDrawerList.getExpandableListAdapter();
                String childPath = ((ExpandableDrawerListAdapter.Child)adapter.getChild(group, child)).mPath;
                performClick(new File(childPath));
                mSlidingMenu.showContent();

                return true;
            }



        });



    }


    private void selectDrawerItem(int position) {

        LogWrapper.Logv(TAG, "Selecting drawer position" + position);
        switch (position) {
            case 0: {
                mCurrentPath = Constants.SDCARD_DIR;
            }
            break;
            case 1: {
                mCurrentPath = Constants.SDCARD_DIR;
            }
            break;
            /*
            case 2:{
                This would be the favorites
                entries, i think, the number might
                be different
            }break;
            */
        }

        // Tell the UI to update
        sendBroadcast(prepareBroadcast(mCurrentPath, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));
        // Close the drawer
        //mDrawerLayout.closeDrawer(mDrawerList);

    }

    private void setupActionBar() {

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(mCurrentPath);


    }


    public Intent prepareBroadcast(String f, String type, MenuAction action) {

        Intent intent = new Intent(Constants.RESOURCE_VIEW_INTENT);

        if (type.equals(Constants.UPDATE_INTENT)) {
            intent = new Intent(Constants.UPDATE_INTENT);
            intent.putExtra("PATH", f);
            intent.putExtra("ACTION", action.getCurrentAction());

        } else if (type.equals(Constants.RESOURCE_VIEW_INTENT)) {
            intent.putExtra("RESOURCE", f);

        }


        return intent;

    }


    private void performClick(File f) {

        if (f.isDirectory()) {

            Log.d(TAG, "Sending UI refresh broadcast [dir=" + f.toString() + "]");
            PreferenceUtils.resetExitStatus(this.getActivity());
            sendBroadcast(prepareBroadcast(f.getPath(), Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));
        } else {
            Log.d(TAG, "Sending media broadcast");
            sendBroadcast(prepareBroadcast(f.toString(), Constants.RESOURCE_VIEW_INTENT, null));
        }


    }

    private void launchSettingMenu() {
        Intent settings = new Intent(this.getActivity(), ApplicationSettings.class);
        startActivity(settings);
    }

    private ActionBar getActionBar() {
        return this.getActivity().getActionBar();

    }

    private void sendBroadcast(Intent i) {
        this.getActivity().sendBroadcast(i);
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
            LogWrapper.Logv(TAG, "The current path is " + mPath);
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
                LogWrapper.Logv(TAG, "Files " + files.length + "");
                files = FileUtils.sortFiles(files, mWContext.get());

                LogWrapper.Logv(TAG, "Files " + files.length + " after sorting");

                for (File f : files) {
                    list.add(f);
                    list.notifyDataSetChanged();
                }

            } else {

                LogWrapper.Logv(TAG, "No files to sort");
            }
            // No longer need to Manually update the list
            // BaseArrayAdapter<T> takes care of that

        }


    }

}
