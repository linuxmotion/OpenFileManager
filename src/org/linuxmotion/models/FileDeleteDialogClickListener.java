package org.linuxmotion.models;

import java.io.File;

import android.content.Context;
import android.content.DialogInterface;

public abstract class FileDeleteDialogClickListener implements DialogInterface.OnClickListener {

	private static File mFile;
	Context mContext;
	
	public FileDeleteDialogClickListener(Context context, File file){
		
		mFile = file;
		mContext = context;
		
	}
	
	
	public File retreiveFile(){
		
		return mFile;
		
	}
	
	public Context retreiveApplicationContext(){
		
		return mContext;
		
		
	}
	
	
	
	
}


