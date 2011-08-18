package org.linuxmotion.models;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.linuxmotion.R;
import org.linuxmotion.utils.Constants;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		
		this.mFiles = files;
		this.mContext = context;
	
	}
	
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
             View v = convertView;
             if (v == null) {
                     LayoutInflater vi = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             v = vi.inflate(R.layout.file_list_item, null);
             }

             
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
                    		 name.endsWith(".png");
                    		 String[] name_extension = name.split(".",2);
                    		 log("Name: "+ name_extension[0] + "  Extension: " + name_extension[1]);
                    		 
                    		 if(name.endsWith(".png") || name.endsWith(".jpg")){
                    			 log("Setting gallery image");
                    			 iv.setBackgroundResource(R.drawable.ic_menu_gallery); 
                    			 
                    		 }else{

                    			 log("Setting compose image");
                    			 iv.setBackgroundResource(R.drawable.ic_menu_compose);
                    		 }
                    		
                         
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
             onFileClickListener listener = new onFileClickListener(it.isDirectory(), this.mContext, it.getPath());
             
             v.setOnClickListener(listener);
             v.setOnLongClickListener(listener);
             

             return v;
     }
	 
	private static void log(String message){
		
		if(DBG)Log.d(TAG, message);
		
	}



	

}
