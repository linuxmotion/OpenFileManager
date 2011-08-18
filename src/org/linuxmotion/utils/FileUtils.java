package org.linuxmotion.utils;

import java.io.File;

public class FileUtils {

	
	
	
	
	public static File[] getFilesInDirectory(String directory){
		
		
		File temp = new File(directory);
		
		return temp.listFiles();	
		
	} 
	
	
}
