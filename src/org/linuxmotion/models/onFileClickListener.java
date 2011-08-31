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
import org.linuxmotion.utils.FileUtils;
import org.linuxmotion.utils.Constants.FileType;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class onFileClickListener implements OnClickListener, OnLongClickListener {

	private static final String TAG = "onFileClickListerner";
	private boolean mIsDirectory;
	private Context mContext;
	private File mFile;
	private ContextMenu mContextMenu = new ContextMenu(){

		@Override
		public void clearHeader() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ContextMenu setHeaderIcon(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ContextMenu setHeaderIcon(Drawable arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ContextMenu setHeaderTitle(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ContextMenu setHeaderTitle(CharSequence arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ContextMenu setHeaderView(View arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MenuItem add(CharSequence arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MenuItem add(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MenuItem add(int arg0, int arg1, int arg2, CharSequence arg3) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MenuItem add(int arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int addIntentOptions(int arg0, int arg1, int arg2,
				ComponentName arg3, Intent[] arg4, Intent arg5, int arg6,
				MenuItem[] arg7) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public SubMenu addSubMenu(CharSequence arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SubMenu addSubMenu(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SubMenu addSubMenu(int arg0, int arg1, int arg2,
				CharSequence arg3) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SubMenu addSubMenu(int arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public MenuItem findItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MenuItem getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasVisibleItems() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isShortcutKey(int arg0, KeyEvent arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean performIdentifierAction(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean performShortcut(int arg0, KeyEvent arg1, int arg2) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeGroup(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeItem(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setGroupCheckable(int arg0, boolean arg1, boolean arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setGroupEnabled(int arg0, boolean arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setGroupVisible(int arg0, boolean arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setQwertyMode(boolean arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		
	};
	
	public onFileClickListener(Context context, File file){
		this.mIsDirectory = file.isDirectory();
		this.mContext = context;
		this.mFile = file;
		
		// Is this file if it is not a directory
		// a image, a video, or a document
		
	}
	
	@Override
	public void onClick(View v) {
		
		//v.setFocusable(true);
		//v.setFocusableInTouchMode(true);
		//v.setSelected(true);
		//v.requestFocus();
		
		if(this.mIsDirectory){
			
			Log.d(TAG, "Is a directory");
			
			Intent updateintent = new Intent(Constants.UPDATE_INTENT);
			updateintent.putExtra("PATH", mFile.getPath());
			openFileManagerActivity.resetExitStatus();
			this.mContext.sendBroadcast(updateintent);
			
		}else{
			
			Log.d(TAG, "The file should be opened here");
			// Open the file here
			FileType type = FileUtils.checkFileExtension(mFile);
		
			switch(type){
			
			case IMAGE: 
				handleImageIntent();
			case PLAIN_TEXT:
				handlePlainTextIntent();
			case DOCUMENT:
				handleDocumentIntent();
			case VIDEO:
				handleVideoIntent();
			
			}
			
			
			
		}
		// TODO Auto-generated method stub

	}
	private void handleVideoIntent() {
		// TODO Auto-generated method stub
		
	}

	private void handleDocumentIntent() {
		// TODO Auto-generated method stub
		
	}

	private void handlePlainTextIntent() {
		// TODO Auto-generated method stub
		
	}

	private void handleImageIntent() {

		// in onCreate or any event where your want the user to
        // select a file
		Log.d(TAG, "Sending image braodcast");
		Intent image_intent = new Intent(Constants.IMAGE_INTENT);
		image_intent.putExtra("IMAGE", this.mFile.toString());
		this.mContext.sendBroadcast(image_intent);

		
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Long click");
		v.setSelected(true);
		v.requestFocus();
		//v.createContextMenu(mContextMenu);
		v.setSelected(false);
		return false;
	}

}
