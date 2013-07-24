package org.linuxmotion.filemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


import org.linuxmotion.asyncloaders.AeSimpleSHA1;
import org.linuxmotion.asyncloaders.LogWrapper;

import java.util.ArrayList;
import java.util.List;

public class StringDatabase extends SQLiteOpenHelper {


    private SQLiteDatabase mDatabase;
    private Cursor mCursor;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "selectedImages";
 
    // Path table name
    private static final String PATHS_TABLE = "imagepath";
 
 // Path Table Columns names
    private static final String KEY_ID = "id";
    // Path Table Columns names
    private static final String PATH_ID = "path";

	private static final String TAG = "MyImageDatabase";
    
    /**
     * Wrapper function , use defaults when called
     * @param context
     */
	public StringDatabase(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
        //mDatabase = this.getWritableDatabase();
        //this.close();
		//super(context, name, factory, version);
	}
	
	public StringDatabase(Context context, String name, CursorFactory factory,
			int version) {
        super(context, name, null, version);
		//super(context, name, factory, version);
	}
	 
	public void open(){
		mDatabase = this.getWritableDatabase();
		
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogWrapper.Logd(TAG, "Creating database");
		// TODO Auto-generated method stub
		 String CREATE_IMAGE_PATH_TABLE = "CREATE TABLE " + PATHS_TABLE + "("
         + KEY_ID + " INTERGER PRIMARY KEY," + PATH_ID + " TEXT )";
		 db.execSQL(CREATE_IMAGE_PATH_TABLE);
		 //db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + PATHS_TABLE);
 
        // Create tables again
        onCreate(db);// Will close the db
		
	}
	
	
	public static final int INSERT_ERROR = -1;
	public static final int PATH_ERROR = -2;
    public static final long DATABASE_NOT_OPEN_ERROR = -3;
	/**
	 * 
	 * @param path The path to the image to add
	 * @return The row ID of the newly inserted row, -1 if an error occurred while inserting,<br> or -2 if the path was not present 
	 */
	public synchronized long addString(String path) {

		 
	    ContentValues values = new ContentValues();
	    
	  
	    
	   if(!isImagePathPresent(path)){ // No need to insert into db if present
		   
		   String hash = AeSimpleSHA1.SHA1(path);
		   LogWrapper.Logdb(TAG, "Adding image with the path: " + path + " and hash: " + hash);
	
		    values.put(KEY_ID, hash); //  String hash
		    values.put(PATH_ID, path); // String path
		 
		    // Inserting Row
           if(mDatabase == null || mDatabase.isOpen())
	    	    return mDatabase.insert(PATHS_TABLE, null, values);
           else
               return DATABASE_NOT_OPEN_ERROR;
	   
	    }
	   return PATH_ERROR;
	}
	 
	// Getting single contact

	 
	// Getting All Contacts
	public synchronized  List<String> getAllStrings() {
	    List<String> pathList = new ArrayList<String>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + PATHS_TABLE;
	 
	    mCursor = mDatabase.rawQuery(selectQuery, null);
	
	    // looping through all rows and adding to list
	    if (mCursor.moveToFirst()) {
	        do {
	        	String path  = mCursor.getString(1);
	            // Adding contact to list
	            pathList.add(path);
	        } while (mCursor.moveToNext());
	    }
	    mCursor.close();

	    // return contact list
	    return pathList;
		}
	 
	// Getting contacts Count
	public synchronized  int getImagesCount() {
		 String selectQuery = "SELECT  * FROM " + PATHS_TABLE;
		 
	    	
		    mCursor = 	mDatabase.rawQuery(selectQuery, null);
		    int count = mCursor.getCount();
		    mCursor.close();

		    return count;
		}
	// Updating single contact
	public synchronized  int updateImage(String contact) {
		return 0;}
	 
	// Deleting single contact
	/**
	 * 
	 * @param path
	 * @return The error code if no image path is present, the number of rows affected if a whereClause is passed in, or 0 otherwise.
	 */
	public synchronized int deleteImage(String path) {

		if(isImagePathPresent(path)){
			String hash = AeSimpleSHA1.SHA1(path);		
			LogWrapper.Logdb(TAG, "Deleting image with name " + path + " and hash " + hash);	
    		return mDatabase.delete(PATHS_TABLE, KEY_ID + " = ?", new String[]{hash});
		}
		
		return PATH_ERROR;
    	
  
	}
	

	public synchronized boolean isImagePathPresent(String path) {

	    // Select All Query
		String hash = AeSimpleSHA1.SHA1(path);
	    String selectQuery = "SELECT  * FROM " + PATHS_TABLE + " WHERE " + KEY_ID + " = \"" + hash +"\"";
	 
    	mDatabase = this.getWritableDatabase();
	    mCursor = 	mDatabase.rawQuery(selectQuery, null);
	    if(mCursor.getCount() > 0){
	    	mCursor.moveToFirst();
	    	LogWrapper.Logdb(TAG, "Entry is present for " + mCursor.getInt(0));
	    	mCursor.close();

	    	return true; // A key entry was found
	    	
	    }
		return false; // A key entry was not found
	}
	

	public boolean isOpen(){

		return (mDatabase != null ? mDatabase.isOpen() : false);

	}
    

}
