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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<File> {
	
	private static boolean DBG = (true || Constants.FULL_DBG);
	private static String TAG = "FileArrayAdapter";
	private File[] mFiles;
	private Context mContext;
	private boolean mEmptylist = false;
	
	public FileArrayAdapter(Context context, int textViewResourceId, File[] files) {
		super(context, textViewResourceId, files);
		
		this.mFiles = files;
		this.mContext = context;
	
	}
	public FileArrayAdapter(Context context, int textViewResourceId, File file) {
		super(context, textViewResourceId);
		
		mEmptylist = true;
		mFiles = file.listFiles();
		mContext = context;
	
	}
	
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
             View v = convertView;
             if (v == null) {
                     LayoutInflater vi = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     v = vi.inflate(R.layout.file_list_item, null);
             }

             if(mFiles.length == 1 && mEmptylist){
            	 Log.d(TAG, "The listview is empty");
             }
             else{
             File it = this.mFiles[position];
             log(it.toString());
             if (it != null) {
            	 log("Seting resources");
            	 
                     ImageView iv = (ImageView) v.findViewById(R.id.thumbnail);
                     if (iv != null) {
                    	 log("Setting image");
                    	 //iv.setImageURI(uri)
                    	 if(it.isFile()){
                    		 // If it a pic set it as
                    		 // a pic, else set it as 
                    		 // a blnk doc file icon
                    		 
                    		 String name = it.getName();
                    	                    		 
                    		 log("Full name: " + name);
                    		 if(FileUtils.hasExtension(name)){
                    			 
                    			 String[] name_extension = name.split("\\.",2);
                    			 log("Name: "+ name_extension[0] + "\nExtension: " + name_extension[1]  );
                    		 
                    		 }
                    		 else {
                    			 
                    			 
                    			 
                    		 }
                    		 
                    		 String ext = MimeTypeMap.getFileExtensionFromUrl(it.getName());
                    		 setIconType(iv, ext);
                    		 
                    		
                         
                    	 }else{

                			 log("Setting icon image");
                    		  iv.setBackgroundResource(R.drawable.ic_list_folder);
                    	 }
                     }
                     
                    TextView FilePath = (TextView) v.findViewById(R.id.file_path);
	             	if(FilePath != null){

                   	 log("Setting text");
	             		FilePath.setText(it.getName());
	             	}
	             	
	             	TextView FileExtras = (TextView) v.findViewById(R.id.file_extras);
	             	if(FileExtras != null){
	             		
	             		log("Setting extras");
                   	 	Date d = new Date(it.lastModified());
                   	 	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy | hh:mm:ss");
                   		String dateString = sdf.format(d);
                   		String extras;
                   		long size = it.length()/1000000;
                   		if(size == 0){
                   			size = it.length();
                   			extras = Long.toString(size) + "Kb | " + dateString ;
                   		}else{
                   			
                   			extras = Long.toString(size) + "Mb | " + dateString ;
                   		}
                    	FileExtras.setMaxLines(1);
	             		FileExtras.setText(extras);
	             	}
             }
             //HMMMMM
            // onFileClickListener listener = new onFileClickListener(this.mContext, it);
             
            // v.setOnClickListener(listener);
             //v.setOnLongClickListener(listener);
             
             }
             
             return v;
     }
	 
	private void setIconType(ImageView iv, String ext) {
		// set the image type by the icon.
		// Creat a maper that map the  extension to a file type
		// video, document, picture, or music
		
		iv.setBackgroundResource(R.drawable.icon);
		return;
		
	}
	private static void log(String message){
		
		if(DBG)Log.d(TAG, message);
		
	}



	

}
