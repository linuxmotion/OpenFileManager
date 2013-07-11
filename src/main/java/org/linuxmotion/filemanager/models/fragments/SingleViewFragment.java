package org.linuxmotion.filemanager.models.fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.MenuAction;
import org.linuxmotion.filemanager.models.adapters.ExpandableDrawerListAdapter;
import org.linuxmotion.filemanager.models.adapters.FileArrayAdapter;
import org.linuxmotion.filemanager.models.baseadapters.ExpandableBaseArrayAdapter;
import org.linuxmotion.filemanager.openFileManagerBroadcastReceiver;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtilAction;
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


    private String mCurrentPath;
    private Vector<String> mLastPath;
    private int mCurrentLocation = 0;
    private boolean mFirstView = true;
    private boolean mAboutToExit = false;
    private boolean mStubIsInflated = false;
    private static openFileManagerBroadcastReceiver sReceiver;
    private ShareActionProvider mShareActionProvider;


    private Alerts mDeleteAlert;
    ActionMode mActionMode;
    private FileUtilAction mFileAction = new FileUtilAction();

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
    private ContextualActionBarMenu mContextualActionBarMenu;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogWrapper.Logi(TAG, "onAttach called");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogWrapper.Logi(TAG, "onCreate called");
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogWrapper.Logi(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.layout_single_main, container, false);
        mContentLayout = (LinearLayout) v.findViewById(R.id.content_frame);
        return v;//super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogWrapper.Logi(TAG, "onActivityCreated called");
        setupVariables();
        setupMainListView();
        //setupActionBar();


    }


    @Override
    public void onStart() {
        super.onStart();
        LogWrapper.Logi(TAG, "onStart called");


        // Show GPL usage license

        sReceiver = new openFileManagerBroadcastReceiver().setDispatcher(this);

        IntentFilter filter = new IntentFilter(Constants.UPDATE_INTENT);
        filter.addAction(Constants.RESOURCE_VIEW_INTENT);
        LogWrapper.Logv(TAG, "registering receiver");
        getActivity().registerReceiver(sReceiver, filter);

        // Always update the adapter when navigating back
        // Something may have changed, whether it
        // be a preference or a new file
        updateAdapter(mCurrentPath);
    }


    @Override
    public void onResume() {
        super.onResume();
        LogWrapper.Logi(TAG, "onResume called");
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
        LogWrapper.Logi(TAG, "onViewCreated called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogWrapper.Logi(TAG, "onDestroyView called");
    }


    public void onBackPressed() {
        LogWrapper.Logv(TAG, "Back button pressed");


        // navigate up
        sendBroadcast(prepareBroadcast(null, Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_UP)));


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogWrapper.Logi(TAG, "onCreateOptionsMenu called");
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_default, menu);
        //return true;
    }


    public void createNewFileorDirectory() {


        Alerts.newFileAlertBox(getActivity(), mCurrentPath, new Alerts.FileAlertBoxListener() {
            @Override
            public void onSelectPositiveButton() {
                updateAdapter(mCurrentPath);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogWrapper.Logi(TAG, "onConfigurationChanged called");
        if (mStubIsInflated && (mInflatedStub.getVisibility() == View.VISIBLE)) {
            handleEmptyListBG();
        }

    }

    public void pasteSelectionCanceled() {
        mFileAction.setHeldFiles(null);
    }

    public void pasteCutItems() {

        mFileAction.cutPasteHeldFiles(mFileAction.getHeldFiles(), new File(mCurrentPath));
        mFileAction.setHeldFiles(null);
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

    public void updateAdapter(String path) {

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





    public void setContextualActionBarMenuInterface(ContextualActionBarMenu listener) {
        mContextualActionBarMenu = listener;
    }

    public interface ContextualActionBarMenu{
        public void OnCutCallback(File[] files);
        public void OnFirstTimeCutCallback();
        public void OnMenuFavoriteCallback(ExpandableBaseArrayAdapter.Child child);
        public boolean OnRenameCallback();

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
                    LogWrapper.Logv(TAG, "Found " + mList.getCheckedItemCount() + " checked items");
                    // Is the number of items check more than 1
                    // is the multislect flag already set
                    if (mList.getCheckedItemCount() > 1) {
                        mMultiSelected = true;
                        LogWrapper.Logd(TAG, "Invalidating the actionmode");
                        mActionMode.invalidate();
                    } else if (mList.getCheckedItemCount() == 1) {
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
                    LogWrapper.Logv(TAG, "Action item clicked from multi choice");
                    // Respond to clicks on the actions in the CAB
                    switch (item.getItemId()) {
                        case R.id.menu_delete: {
                            LogWrapper.Logi(TAG, "Calling menu item DELETE");
                            deleteSelectedItems();
                            updateAdapter(mCurrentPath);
                            mode.finish(); // Action picked, so close the CAB
                        }
                        return true;
                        case R.id.menu_cut: {
                            LogWrapper.Logi(TAG, "Calling menu item CUT");
                            File[] files = getCheckedFiles();
                            // See if there are files held from a prevois
                            // cut operation that was not finished
                            if (mFileAction.getHeldFiles().length > 0) {
                                // if it has not been finished, add to the
                                // cut selection
                                mFileAction.appendToHeldFiles(files);
                            } else {
                                mFileAction.setHeldFiles(files);
                            }


                            mContextualActionBarMenu.OnCutCallback(files);


                            mode.finish(); // Action picked, so close the CAB

                            if (!PreferenceUtils.getHasCompletedRightCutPasteTutorial(getActivity())) {
                                // set showcase view to highlight the menu
                                mContextualActionBarMenu.OnFirstTimeCutCallback();
                                PreferenceUtils.putHasCompletedRightCutPasteTutorial(getActivity(), true);

                            }
                        }
                        return true;
                        case R.id.menu_share: {
                            LogWrapper.Logi(TAG, "Action item SHARE selected");
                            Intent shareIntent;
                            ArrayList<Uri> files = new ArrayList<Uri>();
                            File[] checkedFiles = getCheckedFiles();

                            if(checkedFiles.length == 1){
                                shareIntent = new Intent(Intent.ACTION_SEND);
                                for(File f : checkedFiles)
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

                            }else {
                                shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                if(!determineDataType(shareIntent, checkedFiles)){
                                    LogWrapper.Logd(TAG, "Thee were mulitple data types in the list");
                                    return true;
                                }

                                for(File f : checkedFiles){

                                    files.add(Uri.fromFile(f));
                                }

                                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                            }



                                if (mShareActionProvider != null) {
                                    mShareActionProvider.setShareIntent(shareIntent);
                                }

                            //mShareActionProvider
                            mShareActionProvider.onPerformDefaultAction();

                        }
                        return true;
                        case R.id.menu_rename: {
                            LogWrapper.Logi(TAG, "Action item RENAME selected");
                            // need to show an alert dialog

                            final ActionMode fmode = mode;
                            Alerts.RenameAlertListener listner = new Alerts.RenameAlertListener() {
                                @Override
                                public void onSelectPositiveButton(String newFile) {
                                    LogWrapper.Logi(TAG, "onSelectPositiveButton called for rename dialog");
                                    mFileAction.renameFiles(getCheckedFiles(), mCurrentPath, newFile);
                                    LogWrapper.Logi(TAG, "Updating adapter after renaming");
                                    fmode.finish(); // Action picked, so close the CAB
                                    updateAdapter(mCurrentPath);
                                    //mContextualActionBarMenu.OnRenameCallback();
                                }
                            };

                            Alerts.renameAlertBox(getActivity(),listner);





                        }
                        return true;
                        case R.id.menu_open_as: {
                            // need to show an alert dialog
                            // to manully set the type
                            // of intent that should be sent
                            // display to user as
                            // TEXT
                            // VIDEO
                            // PICTURE


                        }
                        return true;
                        case R.id.menu_favorite: {


                            File[] files = getCheckedFiles();

                            for (int i = 0; i < files.length; i++) {
                                ExpandableBaseArrayAdapter.Child child = new ExpandableBaseArrayAdapter.Child(ExpandableDrawerListAdapter.
                                        FAVORITE_INDEX, ExpandableDrawerListAdapter.ITEM_NEW,
                                        files[i].getName(),
                                        files[i].toString());


                                mContextualActionBarMenu.OnMenuFavoriteCallback(child);
                                //mSideNavigationFragment.AddFavorite(child);
                            }

                            mode.finish(); // Action picked, so close the CAB


                        }
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
                    MenuItem item = menu.findItem(R.id.menu_share);
                    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
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
                    if (mMultiSelected) {


                        if(determineDataType(
                                new Intent(), getCheckedFiles())){
                            inflater.inflate(R.menu.menu_context_actions_multiple_items_share, menu);

                        }else {
                            inflater.inflate(R.menu.menu_context_actions_multiple_items, menu);
                        }


                    } else {
                        inflater.inflate(R.menu.menu_context_actions, menu);


                    }
                    // Here you can perform updates to the CAB due to
                    // an invalidate() request
                    return true;
                }
            });
        }


    }

    private boolean determineDataType(Intent shareIntent, File[] checkedFiles) {


        for(File f : checkedFiles){
            String ext = FileUtils.getExtension(f);
            LogWrapper.Logv(TAG, "Found ext "+ ext);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            LogWrapper.Logv(TAG, "Found type "+ type);
            if ((shareIntent.getType() == null))
                shareIntent.setType(type);
            else if( shareIntent.getType().equals(type)){
                shareIntent.setType(type);
            }
            else
                return false; // there were two different type in the checked fiels

        }
        return true;
    }

    private void deleteSelectedItems() {


        File[] f = getCheckedFiles();
        mDeleteAlert.showDeleteAlertBox(f);

    }

    private File[] getCheckedFiles() {
        LogWrapper.Logi(TAG, "getCheckedFiles()");
        SparseBooleanArray array = mList.getCheckedItemPositions();
        ArrayList<File> files = new ArrayList<File>();
        LogWrapper.Logi(TAG, "Found " + mList.getCheckedItemCount() + " checked items");

        SparseBooleanArray checked = mList.getCheckedItemPositions();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value) {
                files.add((File) mList.getAdapter().getItem(key));
                LogWrapper.Logv(TAG, "Found item with key " + key);
            }

        }

        File[] f = new File[files.size()];
        LogWrapper.Logv(TAG, "Preparing the checked files");
        for (int i = 0; i < files.size(); i++) {
            f[i] = files.get(i);
            LogWrapper.Logv(TAG, "\t--" + f[i].toString());
        }
        return f;
    }


    public void selectDrawerItem(int position) {

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


    public static Intent prepareBroadcast(String f, String type, MenuAction action) {

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


    public void performClick(File f) {

        if (f.isDirectory()) {

            Log.d(TAG, "Sending UI refresh broadcast [dir=" + f.toString() + "]");
            PreferenceUtils.resetExitStatus(this.getActivity());
            sendBroadcast(prepareBroadcast(f.getPath(), Constants.UPDATE_INTENT, new MenuAction(MenuAction.ACTION_FORWARD)));
        } else {
            Log.d(TAG, "Sending media broadcast");
            sendBroadcast(prepareBroadcast(f.toString(), Constants.RESOURCE_VIEW_INTENT, null));
        }


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

    public String getCurrentPath() {
        return mCurrentPath;
    }

}
