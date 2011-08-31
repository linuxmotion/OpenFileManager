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
package org.linuxmotion.utils;

public class Constants {
	
	public static final boolean FULL_DBG = false;
	
	public static final String UPDATE_INTENT = "org.linuxmotion.intent.UPDATE_UI";

	public static final String IMAGE_INTENT = "org.linuxmotion.intent.HANDLE_IMAGE";
	
	public static final String VIDEO_INTENT = "org.linuxmotion.intent.HANDEL_VIDEO";
	
	public static final String DOCUMENT_INTENT = "org.linuxmotion.intent.HANDLE_DOCUMENT";
	
	public static final String SDCARD_DIR = "/sdcard";
	
	
	public static final int REFRESH_UI = 1;

	public enum FileType{ 
		IMAGE(0), 
		PLAIN_TEXT(1), 
		DOCUMENT(2), 
		VIDEO(3),
		UNKNOWN(4);
		
		int TYPE;
		
		
		FileType(int i){
			this.TYPE = i;
			
		}
		
		public int getIntFromType(FileType type){
			
			switch(type){
			case IMAGE: 
				return 0;
			case PLAIN_TEXT:
				return 1;
			case DOCUMENT:
				return 2;
			case VIDEO:
				return 3;
			
			
			}
			return TYPE;
		}
		
		public FileType getTypeFromInt(int type){
		
			switch(type){
			case 0: 
				return IMAGE;
			case 1:
				return PLAIN_TEXT;
			case 2:
				return DOCUMENT;
			case 3:
				return VIDEO;
			default: 
				return UNKNOWN;
			
			
			}
		}
		public int getTypeInt(){
			
			return TYPE;
		}
		
		
	};
	
}
