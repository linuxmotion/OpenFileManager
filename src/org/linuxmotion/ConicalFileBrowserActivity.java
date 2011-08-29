package org.linuxmotion;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.linuxmotion.models.FileArrayAdapter;
import org.linuxmotion.utils.Constants;
import org.linuxmotion.utils.FileUtils;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConicalFileBrowserActivity extends ListActivity {
	
	private static String TAG = ConicalFileBrowserActivity.class.getSimpleName();

	private static LinearLayout mLayout;
	private static ListView mEmptyListView;
	
	private static String mCurrentPath;
	private static Vector<String> mLastPath = new Vector<String>();

	private static boolean mFirstView = true;
	private static boolean mAboutToExit = false;

	
	private BroadcastReceiver fileBroadcastReciver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			
		
			Intent update = intent;
			Bundle extras = update.getExtras();
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
		
		
		
		
	};


	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mLayout = new LinearLayout(this);
        
        mEmptyListView = new ListView(this);
        mEmptyListView.setId(android.R.id.list);
        mLayout.addView(mEmptyListView);
        
        setContentView(R.layout.main);
        if(mFirstView){
        	Log.d(TAG,"First time starting, or restarting");
        	mFirstView = false;
        	mCurrentPath = Constants.SDCARD_DIR;
        }
        
        ListAdapter adapter = createAdapter(mCurrentPath); 
        
        if(adapter != null){
        	setListAdapter(adapter);
        	ListView list = (ListView)findViewById(android.R.id.list);
        	list.setAdapter(adapter);
        	registerForContextMenu(list);
        }
        
        
        
        IntentFilter filter = new IntentFilter(Constants.UPDATE_INTENT);
        registerReceiver(this.fileBroadcastReciver, filter);
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
		Log.d(TAG, f.toString());
		switch(menuItemIndex){
		
		case 0:{
			Log.d(TAG, "Item number is " + menuItemIndex);
			break;
		}
		case 1:{
			Log.d(TAG, "Item number is " + menuItemIndex);
			if(f.delete()){
				Log.d(TAG, "File deleted");
				
			}else{
				

				Log.d(TAG, "File not deleted");
			}

			createBroadCast(f);
			break;
		}
		case 3:{
			Log.d(TAG, "Item number is " + menuItemIndex);
			break;
		}
		
		}
		return true;
    }
    
    @Override
	public void onBackPressed(){
    	Log.d(TAG, "Back button pressed");
    	backButtonPressed();
		
    }
    
    private void backButtonPressed(){
    	if(mAboutToExit){
    		Log.d(TAG,"About to Exit");
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

    	File f = new File(path);
    	Intent updateintent = new Intent(Constants.UPDATE_INTENT); 	
    	if(f.getParentFile() != null)updateintent.putExtra("PATH",f.getParent());
    	else updateintent.putExtra("PATH","/");
    	mAboutToExit = false;
		sendBroadcast(updateintent);
    	
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
    	Log.d(TAG, "the current path is " + path);
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
    
    
    public static void resetExitStatus(){
    	
    
    	mAboutToExit = false;
    	
    }
  
}
