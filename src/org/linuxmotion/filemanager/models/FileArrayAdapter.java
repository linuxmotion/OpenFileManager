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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.utils.Constants;
import org.linuxmotion.filemanager.utils.FileUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
	
	public FileArrayAdapter(Context context, int textViewResourceId, File[] files) {
		super(context, textViewResourceId, files);
		
		mFiles = files;
		mContext = context;
	
	}
	public FileArrayAdapter(Context context, int textViewResourceId, File file) {
		super(context, textViewResourceId);
		
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

             if(mFiles == null){
            	 Log.d(TAG, "The listview is empty");
             }
             else{
             File it = mFiles[position];
             
             if (it != null) {
            	 log(it.toString());
            	 log("Seting resources");
            	 
                     ImageView iv = (ImageView) v.findViewById(R.id.thumbnail);
                     if (iv != null) {
                    	 
                    
                    	 if(it.isFile()){
                    		 log("Setting file image");
                    		 // If it a pic set it as
                    		 // a pic, else set it as 
                    		 // a blnk doc file icon
                    		 
                    		 String name = it.getName();
                    	                    		 
                    		 log("Full name: " + name);      
                    		 int lastdot = name.lastIndexOf(".");
                    		 String s = name.substring(lastdot+1);
                    		 
                    		 String ext = MimeTypeMap.getFileExtensionFromUrl(it.getName());
                    		 if(ext.equals("")){
                    			 // Fallback case where manual retrieval of the last dot is needed
                    			 // Though this shouldn't happen it does
                    			 setIconType(iv,it, s);
                    			 
                    		 }else{              
                    			 setIconType(iv, it, ext);
                    		 
                    		 }
                    		 
                    		
                         
                    	 }else{

                			 log("Setting folder icon image");
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
             
             }
             
             return v;
     }
	 
	private void setIconType(ImageView iv, File path, String extension) {
		// set the image type by the icon.
		// Creat a maper that map the  extension to a file type
		// video, document, picture, or music
	
		
		ExtendedMimeTypeMap m = ExtendedMimeTypeMap.getSingleton();
		String mime = m.getMimeTypeFromExtension(extension);
		
	if(mime != null && mime.contains("image")){
		iv.setBackgroundResource(R.drawable.ic_menu_gallery);
		
	}else if(mime != null &&  mime.contains("audio")){
		
		iv.setBackgroundResource(R.drawable.ic_list_menu_audio);
		
	}else if(mime != null &&  mime.contains("text") || extension.equals("js")){
		
		iv.setBackgroundResource(R.drawable.ic_menu_compose);
		
	}else if(mime != null &&  mime.contains("video")){
		
		iv.setBackgroundResource(R.drawable.ic_list_menu_video);
		
	}else if(extension.equals("zip") || extension.equals("apk")){
		
		iv.setBackgroundResource(R.drawable.ic_list_menu_application_zip);
		
	}else{
		
		iv.setBackgroundResource(R.drawable.icon);
		
	}
			
			
		
		
		
		
		return;
		
	}
	private static void log(String message){
		
		if(DBG)Log.d(TAG, message);
		
	}



	

}
