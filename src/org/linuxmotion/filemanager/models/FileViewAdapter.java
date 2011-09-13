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


import java.io.File;
import java.util.List;

import org.linuxmotion.filemanager.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class FileViewAdapter extends BaseAdapter {

	private List<File> mFileList;
	private Context mContext;
	
	FileViewAdapter(Context context, List<File> files){
		
		this.mContext = context;
		this.mFileList = files;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return this.mFileList.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
	LinearLayout itemLayout;
	File files = this.mFileList.get(position);
	
	itemLayout= (LinearLayout) LayoutInflater.from(this.mContext).inflate(R.layout.file_list_item, parent, false);
	
	TextView filepath = (TextView) itemLayout.findViewById(R.id.file_path);
	filepath.setText(files.toString());
	
	ImageView thumbnail = (ImageView) itemLayout.findViewById(R.id.thumbnail);
	thumbnail.setBackgroundResource(R.drawable.icon);

	return itemLayout;
		
	}


}
