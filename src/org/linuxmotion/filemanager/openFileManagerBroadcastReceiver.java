package org.linuxmotion.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class openFileManagerBroadcastReceiver extends BroadcastReceiver {

	openFileManagerReceiverDispatcher mOpenFileManagerReceiverDispatcher;
	
	@Override
	public void onReceive(Context arg0, Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		
		if(extras.containsKey("PATH")){
			mOpenFileManagerReceiverDispatcher.dispatchPathUpdate(extras.getString("PATH"));
			
			
			}
		else if(extras.containsKey("RESOURCE")){
			mOpenFileManagerReceiverDispatcher.dispatchResourceUpdate(extras.getString("RESOURCE"));
			
		}

		
		
		
		// TODO Auto-generated method stub

	}

	
	public interface openFileManagerReceiverDispatcher{

		void dispatchPathUpdate(String updatePath);

		void dispatchResourceUpdate(String resourcePath);
		
		
		
		
	}
	
	
	public openFileManagerBroadcastReceiver setDispatcher(openFileManagerReceiverDispatcher l){
		
		mOpenFileManagerReceiverDispatcher = l;
		
		return this;
	}
}
