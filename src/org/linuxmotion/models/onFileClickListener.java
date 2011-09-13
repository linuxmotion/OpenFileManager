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
package org.linuxmotion.models;

import java.io.File;

import org.linuxmotion.openFileManagerActivity;
import org.linuxmotion.utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class onFileClickListener implements OnClickListener, OnLongClickListener {

	private static final String TAG = "onFileClickListerner";
	private boolean mIsDirectory;
	private Context mContext;
	private File mFile;
	
	
	public onFileClickListener(Context context, File file){
		mIsDirectory = file.isDirectory();
		mContext = context;
		mFile = file;
		
	}
	
	@Override
	public void onClick(View v) {
		
		if(mIsDirectory){
			
			Log.d(TAG, "Sending UI refresh broadcast");
			
			Intent updateintent = new Intent(Constants.UPDATE_INTENT);
			updateintent.putExtra("PATH", mFile.getPath());
			openFileManagerActivity.resetExitStatus();
			mContext.sendBroadcast(updateintent);
			
		}else{
			
			Log.d(TAG, "Sending media broadcast");
			Intent resource_intent = new Intent(Constants.RESOURCE_VIEW_INTENT);
			resource_intent.putExtra("RESOURCE", mFile.toString());
			mContext.sendBroadcast(resource_intent);
					
			
		}

	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Long click");
		v.setSelected(true);
		v.requestFocus();
		return false;
	}

}
