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
package org.linuxmotion.filemanager.utils;

import java.io.File;

import org.linuxmotion.filemanager.preferences.PreferenceUtils;

import android.content.Context;
import android.util.Log;

public class FileUtils {
	private static String TAG = FileUtils.class.getCanonicalName();
	
	private final static boolean DUMP_DEBUG = false;

	private static final boolean DGB = (false | Constants.FULL_DBG);
	
	
	
	/**
	 * 
	 * 
	 * @param directory the director path from which to retrive the files from
	 * @return
	 */
	public static File[] getFilesInDirectory(String directory, Context context){
		
		
		File temp = new File(directory);
		if(temp.exists()){
			
			Log.d("FileUtils", "Path exists");
			if(temp.listFiles() != null){
				
				return sortFiles(temp, context);
			}
			else {
				// If this point is reached then the directory doesnt contain any 
				// files
				Log.d(TAG, "the directory is empty, or not accessible");
				return null;
			}
			
		}
		else {
			Log.d(TAG, "Sdcard directory in not present");
			return null;
		}
	} 
	
	
	/**
	 * Starts the sorting process. This is a wrapper function that
	 * calls all of the sort functions, that individually sort 
	 * files by different parameters. Although each function 
	 * only serves one task, each time it is accumulated
	 * 
	 * @param temp
	 * @return
	 */
	private static File[] sortFiles(File temp, Context context) {
		
		File[] FILES = temp.listFiles();
		
		File[] hiddenfiles;
		
		
		SortByFileFolder(FILES, context);
		SortHiddenFilesFolders(FILES, context);
		hiddenfiles = ShowHideHiddenFilesFolders(FILES, context);

		
		return hiddenfiles;
	}


	/**
	 * 
	 * @param files the files to sort
     * @param context To retrieve the sharedPrefreneces
	 */

	private static File[] ShowHideHiddenFilesFolders(File[] files, Context context) {
		
		File[] FILES = files;
	boolean loop = false;
	boolean hide = PreferenceUtils.retreiveShowHideHiddenFilesFoldersPref(context);
	int length = FILES.length;
	boolean hide_folders = hide;
	boolean hide_files = hide;
	
	
	// Set all hidden dir and files to null
	for(int i = 0; i < length-1; i++){
		File f = FILES[i];
		boolean dir = f.isDirectory();
		boolean hidden = f.isHidden();
		
		if( dir && hidden  && hide_folders){
			
			FILES[i] = null;
			
			
		}
		
		
		
		if(!dir && hidden && hide_files){
					
					FILES[i] = null;
					
					
		}
		
		
	}
	
		// Move all null files to the end of the list preserving non null order
		do{
			
			loop = false;
			for(int i = 0; i < FILES.length-1; i++){
				File f = FILES[i];

				if((FILES[i] == null) &&( FILES[i+1] != null)){
					// Switch with the next item
					FILES[i] = FILES[i+1];
					FILES[i+1] = f;
					loop = true;
				}
			}
	
			
		}while(loop);
		
		
		// Shrink the new list
		// Find the first null value
		int nullstart = 0;
	
			for(int i = 0; i < FILES.length-1; i++){
				

				if(FILES[i] == null){
					nullstart = i;
					break;
				}
			}
			
			
			File[] Files = null;
			// Create a new File[] size - 1 of the null poistion
			if(nullstart > 0){
				Files = new File[nullstart-1];
				for(int i = 0; i < nullstart-1; i++){
					
					Files[i] = FILES[i];
		
				}
				
				if(DUMP_DEBUG)dump(Files);
				return Files;
				
			}
			else{
				
				return FILES;
				
			}
			
			
	
	}


	/**
	 * 
	 * @param files the files to sort
     * @param context To retrieve the sharedPrefreneces
	 */
	private static void SortByFileFolder(File[] file, Context context) {
		File[] FILES = file;

		boolean loop = false;
		
		do{
			loop = false;
			
			for(int i = 0; i < FILES.length-1; i++){
				File f = FILES[i];
				File t = FILES[i+1];
				
				if(!f.isDirectory() && t.isDirectory()){
					FILES[i] = t;
					FILES[i+1] = f;
					loop = true;
					
				}
				
				
				
			}
		}while(loop);
		
		boolean FoldersFirst = PreferenceUtils.retreiveSortbyFoldersFilesPref(context);
		
		
		
		if(!FoldersFirst){
			//TODO: reverse the order
		
			int filestart = 0;
			
			for(int i = 0; i < FILES.length-1; i++){
				

				if(!FILES[i].isDirectory()){
					filestart = i;
					break;
				}
			}
			
			
			int o = FILES.length-1;
			for(int i = 0; i <  filestart; i++){
				
				File f = FILES[i];
				File t = FILES[o];
				
					FILES[i] = t;
					FILES[o] = f;
					
				o--;
				
				
			}
		
			
			
		}
		
		if(DUMP_DEBUG)dump(FILES);
			
		file = FILES;
		
	}


	// TODO: At some pint this should sort the list non-hidden -> hidden -> non-hidden -> hidden 
	// the user should then be able to select using a preference
	/**
	 * Sort into hidden folders then folders. Form there is sorts into hidden files then non hiden
	 * files.
	 * 
	 * @param files the files to sort
	 * @param context To retrieve the sharedPrefreneces
	 */
	private static void SortHiddenFilesFolders(File[] files, Context context) {
		
		File[] FILES = files;

		boolean loop = false;
		
		do{
			loop = false;
			
			for(int i = FILES.length-1; i > 0; i--){
				
				File t = FILES[i];
				File f = FILES[i-1];
				
				boolean a = t.isDirectory();
					boolean b = t.isHidden();
						boolean c = f.isDirectory();
							boolean d = f.isHidden();
				
				if( a && b && c && !d){
					
					FILES[i-1] = t;
					FILES[i] = f;
					loop = true;
					
				}
				
				
				
			}
		}while(loop);
		
		if(DUMP_DEBUG)dump(FILES);
		
		files = FILES;
		
	}



	public static boolean hasExtension(String filename){
		
		log("Filename: " + filename);
		char[] tfile = filename.toCharArray();
		for(int i = tfile.length-1; i >= 0 ; i--){
			
			if(tfile[i] == '.' && i != 0){
				log("File extension found");
				return true;
				
			}
			
		}
		
		log("Does not have an extension");
		return false;
			
		
	}
	
	private static void dump(File[] f){
		
	for(int i = 0; i < f.length-1; i++)
		log(i + " --- "  + f[i].getName() );
		
	}
	
	private static void log(String message){
		
		if(DGB)Log.d(TAG, message);
		
	}
	

	
}
