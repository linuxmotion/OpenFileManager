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


import java.io.File;
import java.io.InvalidObjectException;
import java.util.Vector;

import org.linuxmotion.filemanager.models.DualTouchListListener;
import org.linuxmotion.filemanager.models.FileArrayAdapter;
import org.linuxmotion.filemanager.openFileManagerBroadcastReceiver.openFileManagerReceiverDispatcher;
import org.linuxmotion.filemanager.preferences.ApplicationSettings;
import org.linuxmotion.filemanager.preferences.PreferenceUtils;
import org.linuxmotion.filemanager.utils.Alerts;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtils;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class openFileManagerActivity extends ListActivity implements Alerts.GPLAlertClickDispatcher, 
Alerts.deleteAlertClickDispatcher, DualTouchListListener.DualTouchListListenerDispatcher, openFileManagerReceiverDispatcher{
	
	private static String TAG = openFileManagerActivity.class.getSimpleName();
	private static boolean DEBUG = (true | Constants.FULL_DBG);

	/*Standard variable components*/	
	private String mCurrentPath;
	private static Vector<String> sLastPath;

	private boolean mFirstView = true;
	private boolean mShowGPL = true;
	private boolean mAboutToExit = false;
	private boolean mStubIsInflated = false; 
	
	/*Standard UI components*/
	LinearLayout mInflatedStub;
	private ListView mList;
	
	/*Custom components*/
	private static openFileManagerBroadcastReceiver sReceiver;
	private Alerts mAlerts;

	
	public final Handler mUIRefresher = new Handler(){
		
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			
			case Constants.REFRESH_UI:
				createBroadCast(mCurrentPath);		
				
			case Constants.UNKNOWN_FILE_TYPE:
				unknown();
				
			}
			
			
		}

	};
	
	
    /** Called when the activity is first created. */
    @Override
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     mAlerts = new Alerts(this);
     mAlerts.setGPLDispatcher(this);
     mAlerts.setDeleteDispatcher(this);
        
     
        setContentView(R.layout.main);
        
        if(mFirstView){
        	log("First time starting, or restarting");
        	mFirstView = false;
        	mCurrentPath = Constants.SDCARD_DIR;
        	 
        	sLastPath = new Vector<String>();
        	
        	
        }
        // Show GPL usage license
        mShowGPL = Alerts.shouldIssueGPLLicense(this);
        if(mShowGPL)mAlerts.ShowGPLAlert();
        
        ListAdapter adapter = createAdapter(mCurrentPath); 
        
        if(adapter != null){
        	setListAdapter(adapter);
        	mList = (ListView)findViewById(android.R.id.list);
        	mList.setLongClickable(true);
        	mList.setAdapter(adapter);
        	final GestureDetector gestureDetector = new GestureDetector(new DualTouchListListener(this).setDispatcher(this));
        	View.OnTouchListener gestureListener = setupTouchListeners(gestureDetector);        	
        	mList.setOnTouchListener(gestureListener);           	
        	registerForContextMenu(mList);
        }
         
    }
    
    private View.OnTouchListener setupTouchListeners(final GestureDetector gestureDetector) {
    	

        
   	 View.OnTouchListener gestureListener = new View.OnTouchListener() {
   		 
   		 private float mPx;
   		 private float mPy;
   		 private boolean startDrag = false;  
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				//log("On touch called");
				arg0.cancelLongPress();
				
				 arg0.clearFocus();
				arg0.setClickable(false);
				arg0.setLongClickable(false);
				
				if(!startDrag){
					mPx = event.getX();
					mPy = event.getY();
					startDrag = true;
				}
				
				 if(  (Math.abs(event.getX() - mPx) > DualTouchListListener.REL_SWIPE_MIN_DISTANCE )   && 
		        		 (Math.abs(event.getY(0) - mPy)  < DualTouchListListener.REL_SWIPE_MAX_OFF_PATH)  ){
		        	
		        	Log.d(TAG,"Setting slide mode");
		        	gestureDetector.onTouchEvent(event); 
		        	return true;
		        }
				
				 if(event.getAction() == MotionEvent.ACTION_UP){
					 Log.d(TAG,"Setting slide mode inactive");
					 startDrag = false;
				 }
				 
				 
				 arg0.cancelLongPress();
					arg0.setFocusable(true);
					arg0.setClickable(true);
					arg0.setLongClickable(true);
				
				return false;
				
				
				
			}
			
   	 };
		

   	 return gestureListener;
		
	}

	@Override
    public void onStart(){
    	super.onStart();
    	log("On start called - registering receiver");
    	
    	sReceiver = new openFileManagerBroadcastReceiver().setDispatcher(this);
    	
    	IntentFilter filter = new IntentFilter(Constants.UPDATE_INTENT);
        filter.addAction(Constants.RESOURCE_VIEW_INTENT);
    	 registerReceiver(sReceiver, filter);
    	 
    	// Always refresh the UI when navigating back
    	 // Something may have changed, whether it 
    	 // be a preference or a new file
    	 refreshPathUI();
    	
    }
    
    @Override
    public void onStop(){
    	super.onStop();

    	log("On stop called - unregistering receiver");
    	unregisterReceiver(sReceiver);
    	
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      if (v.getId()==android.R.id.list) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        
        //Log.d(TAG,(String) adapt.getItem(0) );
        menu.setHeaderTitle(R.string.file_options);
        String[] menuItems = getResources().getStringArray(R.array.menu);
        for (int i = 0; i<menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
      }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		
		File f = (File) getListAdapter().getItem(info.position);
		log( f.toString());
		switch(menuItemIndex){
		
		case 0:{
			performClick(f);
			break;
		}
		case 2:{
			mCurrentPath = f.getPath();
			mAlerts.showDeleteAlertBox(f);
			
			break;
		}
		case 3:{
			log("Item number is " + menuItemIndex);
			break;
		}
		
		}
		return true;
    }
 
    @Override
	public void onBackPressed(){
    	log( "Back button pressed");
    	
    	mAboutToExit = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0).getBoolean(Constants.ABOUT_TO_EXIT, false);
    	if(mAboutToExit){
    		log("About to Exit");
    		mAboutToExit = false;
    		mFirstView = true;
    		
    		finish();
    		
    		
    	}
    	else{
    		
	       	File f = new File(mCurrentPath);
	    	if(f.getParent() != null)Log.d(TAG, f.getParent());
	    	
	    	
	    	if(f.getParent()== null){

	    		Toast.makeText(this, "Press back once more to exit", 1000).show();
	    		PreferenceUtils.setExitStatus(this);
	    		
	    	}else if(f.getParent().equals("/")){
	
	    		Log.d(TAG, "Set exit status. Dir = " + f.getParent());
	    		createBroadCast(f);
	    		
	    	}else{
	    		Log.d(TAG, f.getParent());
	    		createBroadCast(f);
	    	
	    		
	    	}
    	}
		
    }
       
	public void createBroadCast(String path){
    	
    	createBroadCast(new File(path));
    	
    }
    
    private void createBroadCast(File path){
    	
    	Intent updateintent = new Intent(Constants.UPDATE_INTENT); 	
    	if(path.getParentFile() != null)updateintent.putExtra("PATH",path.getParent());
    	else updateintent.putExtra("PATH","/");
		PreferenceUtils.resetExitStatus(this);
		sendBroadcast(updateintent);

    }
    
    /**
    * Creates and returns a list adapter for the current list activity
    * @return
    */
    protected FileArrayAdapter createAdapter(String path)
    { 
    	log( "the current path is " + path);
    	File[] files = FileUtils.getFilesInDirectory(path, this.getApplicationContext());
    	FileArrayAdapter adapter;
    	if(files != null){
    		
    		adapter = new FileArrayAdapter(this.getBaseContext(),  R.layout.simple_text_view, files);
    		
    	}
    	else {
    		adapter = new FileArrayAdapter(this.getBaseContext(),  R.layout.simple_text_view, new File(path));
    	}
    	
    	return adapter;
    }
    
    @Override
    public void onListItemClick(ListView l, View v,int position, long id){
    	log("List item clicked");
    	
    	
    	
    		File f = (File) getListAdapter().getItem(position);
    		performClick(f);
    	
    	
    	
    }
    
    private void performClick(File f){
    	
 	if(f.isDirectory()){
			
			Log.d(TAG, "Sending UI refresh broadcast");
			
			Intent updateintent = new Intent(Constants.UPDATE_INTENT);
			updateintent.putExtra("PATH", f.getPath());
			PreferenceUtils.resetExitStatus(this);
			sendBroadcast(updateintent);
			
		}else{
			
			Log.d(TAG, "Sending media broadcast");
			Intent resource_intent = new Intent(Constants.RESOURCE_VIEW_INTENT);
			resource_intent.putExtra("RESOURCE", f.toString());
			sendBroadcast(resource_intent);
					
			
		}
    	
    	
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
		
		
		if(f.delete()){
			
			Toast.makeText(getApplicationContext(), "Your file has been deleted",Toast.LENGTH_SHORT).show();
			mUIRefresher.sendEmptyMessage(Constants.REFRESH_UI);
		}
		else{
			
			// Did not delete file
			// post a handler
			Toast.makeText(getApplicationContext(), "Your file has been not deleted",Toast.LENGTH_SHORT).show();
			
			
		}
	}

	@Override
	public void dispatchLeftFling() {
		
		
		log("Dispatching RTL fling event");
		
	}

	@Override
	public void dispatchRightFling() {
		this.onBackPressed();
		mList.cancelLongPress();
		log("Dispatching LTR fling event");
		
	}
		
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.main_menu, menu);
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.settings_menu:
			launchSettingMenu();
			break;
		}
	    return(super.onOptionsItemSelected(item));
	}	
	
	 private void launchSettingMenu() {    	
	        Intent settings = new Intent(this, ApplicationSettings.class);
	        startActivity(settings);
		}

	@Override
	public void dispatchPathUpdate(String updatePath) {
		
		log("Standard UI refresh");
		
		sLastPath.add(mCurrentPath);
		mCurrentPath = updatePath;
		
			 ListAdapter adapter = createAdapter(mCurrentPath); 
		        if(adapter != null){
		        	setListAdapter(adapter);
		        	
		        	ListView list = (ListView)findViewById(android.R.id.list);
		        	list.setAdapter(adapter);
		        	if(adapter.isEmpty() && !mStubIsInflated){
		        		ViewStub Stub = (ViewStub) findViewById(R.id.stub);
		        		mInflatedStub = (LinearLayout) Stub.inflate();
		        		handleEmptyListBG();
		        		mStubIsInflated = true;
		        		
		        	}
		        	else if(adapter.isEmpty() && mStubIsInflated && (mInflatedStub.getVisibility() == View.GONE)){
		        		mInflatedStub.setVisibility(View.VISIBLE);
		        		 handleEmptyListBG();
		        		
		        	}
		        	else if(mStubIsInflated && (mInflatedStub.getVisibility() == View.VISIBLE)){
		        		mInflatedStub.setVisibility(View.GONE);
		        	}
		        
		        }
		        
		
	}
	
	private void handleEmptyListBG() {

		ImageView EmptyBG = (ImageView) mInflatedStub.findViewById(R.id.Empty_List_Image_View);
		Configuration config = getResources().getConfiguration();
		if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
			EmptyBG.setBackgroundResource(R.drawable.list_bg_empty_port);
		}else{

			EmptyBG.setBackgroundResource(R.drawable.list_bg_empty_land);
			
		}
		
	}

	private void refreshPathUI() {
		
		log("Normal UI refresh");
		
				
			 ListAdapter adapter = createAdapter(mCurrentPath); 
		        if(adapter != null){
		        	setListAdapter(adapter);
		        	
		        	ListView list = (ListView)findViewById(android.R.id.list);
		        	list.setAdapter(adapter);
		        	
		        }
		        
		
	}

	@Override
	public void dispatchResourceUpdate(String resourcePath) {
		

		
		MimeTypeMap MIME = MimeTypeMap.getSingleton();
		

		String path = resourcePath;
		String mimetype = MIME.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
			if(mimetype != null){
				Intent resourceintent = new Intent(Intent.ACTION_VIEW);
	            		resourceintent.setDataAndType(Uri.parse("file://" + path), mimetype );
	            		try{
	            			startActivity(resourceintent);
	            		}
	            		catch(ActivityNotFoundException e){
	            			e.printStackTrace();
	            			mUIRefresher.sendEmptyMessage(Constants.UNKNOWN_FILE_TYPE);
	            		}
			}
			else
				mUIRefresher.sendEmptyMessage(Constants.UNKNOWN_FILE_TYPE);
			
		
	}
	

	
	private void unknown(){
		
		Toast.makeText(this, "Unknown file type", Toast.LENGTH_SHORT).show();
	}

	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);

    	if(mStubIsInflated && (mInflatedStub.getVisibility() == View.VISIBLE)){
    		 handleEmptyListBG();    		
    	}

    }

	@Override
	public void onDestroy(){
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
	
	
	
	private void log(String message){
    	
    	if(DEBUG)Log.d(TAG, message);
    	
    }
    

  
}
