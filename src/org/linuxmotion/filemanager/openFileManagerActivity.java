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
import java.util.Vector;

import org.linuxmotion.filemanager.models.FileArrayAdapter;
import org.linuxmotion.filemanager.models.FileDeleteDialogClickListener;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class openFileManagerActivity extends ListActivity {
	
	private static String TAG = openFileManagerActivity.class.getSimpleName();
	private static boolean DEBUG = (true || Constants.FULL_DBG);

	
	private static String mCurrentPath;
	private static Vector<String> mLastPath = new Vector<String>();

	private static boolean mFirstView = true;
	private static boolean mShowGPL = true;
	private static boolean mAboutToExit = false;

	
	public final Handler mUIRefresher = new Handler(){
		
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			
			case Constants.REFRESH_UI:
				createBroadCast(mCurrentPath);		
				
			case 10:
				unknown();
				
			}
			
			
		}
			
		
		
	};
	
	private void unknown(){
		
		Toast.makeText(this, "Unknown file type", Toast.LENGTH_SHORT).show();
	}
	
	private BroadcastReceiver fileBroadcastReciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
		Log.d(TAG, "intentReceived");
			
		Bundle extras = intent.getExtras();
		
		
		if(extras.containsKey("PATH")){
			log("Standard UI refresh");
			String path = extras.getString("PATH");
			mLastPath.add(mCurrentPath);
			mCurrentPath = path;
			
				 ListAdapter adapter = createAdapter(mCurrentPath); 
			        if(adapter != null){
			        	setListAdapter(adapter);
			        	ListView list = (ListView)findViewById(android.R.id.list);
			        	list.setAdapter(adapter);
			        	//this.registerForContextMenu(list);
			        }
			
			
			}
		else if(extras.containsKey("RESOURCE")){
		MimeTypeMap MIME = MimeTypeMap.getSingleton();
		

		String path = extras.getString("RESOURCE");
		String mimetype = MIME.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
			if(mimetype != null){
				Intent resourceintent = new Intent(Intent.ACTION_VIEW);
	            		resourceintent.setDataAndType(Uri.parse("file://" + path), mimetype );
	            		try{
	            			startActivity(resourceintent);
	            		}
	            		catch(ActivityNotFoundException e){
	            			e.printStackTrace();
	            			mUIRefresher.sendEmptyMessage(10);
	            		}
			}
			else
				mUIRefresher.sendEmptyMessage(10);
		}
		
		
		}
		
	};


	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     
        setContentView(R.layout.main);
        
        if(mFirstView){
        	log("First time starting, or restarting");
        	mFirstView = false;
        	mCurrentPath = Constants.SDCARD_DIR;
        	
        	
        }
        // Show GPL usage license
        mShowGPL = shouldIssueGPLLicense();
        if(mShowGPL)GPLAlertBox();
        
        ListAdapter adapter = createAdapter(mCurrentPath); 
        
        if(adapter != null){
        	setListAdapter(adapter);
        	ListView list = (ListView)findViewById(android.R.id.list);
        	list.setAdapter(adapter);
        	registerForContextMenu(list);
        }
        
        
        
        IntentFilter filter = new IntentFilter(Constants.UPDATE_INTENT);
        filter.addAction(Constants.RESOURCE_VIEW_INTENT);
        
        registerReceiver(this.fileBroadcastReciver, filter);
    }
    private boolean shouldIssueGPLLicense() {

    		SharedPreferences prefs = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
    		int version = prefs.getInt(Constants.APP_NAME, -1);
    		
    		if(version != Constants.VERSION_LEVEL)
    			return true;
    			
    		
    	
		return false;
	}
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      if (v.getId()==android.R.id.list) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        ListAdapter adapt = this.getListAdapter();
        
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
			log("Item number is " + menuItemIndex);
			break;
		}
		case 1:{
			mCurrentPath = f.getPath();
			deleteAlertBox(f);
			
			log( "Item number is " + menuItemIndex);
			
			// Instead post a handler that refreshes the ui
			
			break;
		}
		case 3:{
			log("Item number is " + menuItemIndex);
			break;
		}
		
		}
		return true;
    }
    
    protected void GPLAlertBox(){
    	
    	Builder bGPL = new AlertDialog.Builder(this);
    	String message = "openFileManager  Copyright (C) 2011  \nCreated by John A Weyrauch.\n " + 
    "This program comes with ABSOLUTELY NO WARRANTY. For details press menu, then about. " +
    "This is free software, and you are welcome to use, modify, or redistribute it" +
    "under certain conditions.";
    	
    	bGPL.setTitle("GPL Usage license").setMessage(message).setCancelable(false);
    	bGPL.setPositiveButton("Proceed", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

	    		SharedPreferences prefs = getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
	    		SharedPreferences.Editor edit = prefs.edit();
	    		edit.putInt(Constants.APP_NAME, Constants.VERSION_LEVEL);
	    		edit.commit();
	    		
				mShowGPL = false;
			}
    		
    		
    		
    	});
    	bGPL.setNegativeButton("Quit", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mFirstView = true;
				mShowGPL = true;
				finish();
			}
    		
    		
    		
    	});
    	

    	bGPL.show();
    }
    
    protected void deleteAlertBox(File file) {
  
    	
    	Builder delete = new AlertDialog.Builder(this);
    	delete.setTitle("Warning");
    	delete.setMessage("Are you sure you want to delete the file");
    	delete.setCancelable(false);
    	
    	FileDeleteDialogClickListener deletedialog = new FileDeleteDialogClickListener(this, file){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				File f = retreiveFile();
				
				if(f.delete()){
					
					Toast.makeText(this.retreiveApplicationContext(), "Your file has been deleted",Toast.LENGTH_SHORT).show();
					mUIRefresher.sendEmptyMessage(Constants.REFRESH_UI);
				}
				else{
					
					// Did not delete file
					// post a handler
					Toast.makeText(this.retreiveApplicationContext(), "Your file has been not deleted",Toast.LENGTH_SHORT).show();
					mUIRefresher.sendEmptyMessage(Constants.REFRESH_UI);
					
				}
			}
    		
    		
    	};
    
    	delete.setPositiveButton("Delete", deletedialog);
    	
    	delete.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// Do nothing
				
			}
    		
    		
    		
    	});
    	
    	delete.show();
    }

	

    
    @Override
	public void onBackPressed(){
    	log( "Back button pressed");
    	backButtonPressed();
		
    }
    
    private void backButtonPressed(){
    	if(mAboutToExit){
    		log("About to Exit");
    		mAboutToExit = false;
    		mFirstView = true;
    		unregisterReceiver(fileBroadcastReciver);
    		finish();
    		
    		
    	}
    	else{
    		mAboutToExit = false;
    		
	       	File f = new File(mCurrentPath);
	    	if(f.getParent() != null)Log.d(TAG, f.getParent());
	    	
	    	
	    	if(f.getParent()== null){

	    		Toast.makeText(this, "Press back once more to exit", 1000).show();
	    		mAboutToExit = true;
	    		
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
    	mAboutToExit = false;
		sendBroadcast(updateintent);
    	
    }
    
    
    /**
    * Creates and returns a list adapter for the current list activity
    * @return
    */
    protected FileArrayAdapter createAdapter(String path)
    { 
    	log( "the current path is " + path);
    	File[] files = FileUtils.getFilesInDirectory(path);
    	FileArrayAdapter adapter;
    	if(files != null){
    		
    		adapter = new FileArrayAdapter(this.getBaseContext(),  R.layout.simple_text_view, files);
    		
    	}
    	else {
    		adapter = new FileArrayAdapter(this.getBaseContext(),  R.layout.simple_text_view, new File(path));
    	}
    	
    	return adapter;
    }
    
    private void log(String message){
    	
    	if(DEBUG)Log.d(TAG, message);
    	
    }
    
    
    
    public static void resetExitStatus(){
    	
    
    	mAboutToExit = false;
    	
    }
  
}
