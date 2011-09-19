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

import android.util.Log;

public class FileUtils {
	private static String TAG = FileUtils.class.getCanonicalName();
	
	
	
	/**
	 * 
	 * 
	 * @param directory the director path from which to retrive the files from
	 * @return
	 */
	public static File[] getFilesInDirectory(String directory){
		
		
		File temp = new File(directory);
		if(temp.exists()){
			
			Log.d("FileUtils", "Path exists");
			if(temp.listFiles() != null){
				
				return sortFiles(temp);
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
	private static File[] sortFiles(File temp) {
		
		File[] FILES = temp.listFiles();
		
		
		
		SortByFileFolder(FILES);
		SortHiddenFilesFolders(FILES);
		//FILES = ShowHideHiddenFilesFolders(FILES);

		
		return FILES;
	}



	private static File[] ShowHideHiddenFilesFolders(File[] FILES) {
		
	boolean loop = false;
	boolean hide = true;
	int length = FILES.length;
	for(int i = 0; i < length-1; i++){
		File f = FILES[i];
		
		if(f.isDirectory() && f.getName().startsWith(".") && hide ){
			
			FILES[i] = null;
			
			
		}
		
		
	}
	
		
		do{
			
			loop = false;
			for(int i = 0; i < FILES.length-1; i++){
				

				if(FILES[i] == null){
					FILES[i] = FILES[i+1];
					loop = true;
				}
			}
	
			
		}while(loop);
		
		int nullstart = 0;
	
			for(int i = 0; i < FILES.length-1; i++){
				

				if(FILES[i] == null){
					nullstart = i;
					break;
				}
			}
	
			File[] Files = new File[nullstart];
			for(int i = 0; i < nullstart; i++){
				
				Files[i] = FILES[i];
				
			}
	
		return Files;
	}



	private static void SortByFileFolder(File[] FILES) {
		

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
		
	}



	private static void SortHiddenFilesFolders(File[] FILES) {
		

		boolean loop = false;
		
		do{
			loop = false;
			
			for(int i = FILES.length-1; i > 0; i--){
				
				File t = FILES[i];
				File f = FILES[i-1];
				
				boolean a = t.isDirectory();
					boolean b = t.getName().startsWith(".");
						boolean c = f.isDirectory() ;
							boolean d = f.getName().startsWith(".");
				
				if( a && b && c && !d){
					
					FILES[i-1] = t;
					FILES[i] = f;
					loop = true;
					
				}
				
				
				
			}
		}while(loop);
		
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
	
	private static void log(String message){
		
		Log.d(TAG, message);
		
	}
	

	
}
