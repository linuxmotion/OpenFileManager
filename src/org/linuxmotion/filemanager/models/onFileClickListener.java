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
package org.linuxmotion.filemanager.models;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class onFileClickListener implements AdapterView.OnItemLongClickListener{

	private static final String TAG = onFileClickListener.class.getSimpleName();
	

  

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		
		Log.d(TAG, "Item long clicked");
		// TODO Auto-generated method stub
		return false;
	}


}
