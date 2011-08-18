package org.linuxmotion.models;

import org.linuxmotion.R;

import java.io.File;
import java.util.List;


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
