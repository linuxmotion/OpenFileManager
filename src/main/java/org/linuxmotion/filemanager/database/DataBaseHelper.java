package org.linuxmotion.filemanager.database;

import android.content.Context;

import org.linuxmotion.asyncloaders.LogWrapper;

import java.io.File;
import java.util.List;

public class DataBaseHelper  {
	
	private static final String TAG = DataBaseHelper.class.getSimpleName(); 
	
	onDatabaseTransactionFinished mDatabaseTransactionListener = null;
	Context mContext = null;
	public interface onDatabaseTransactionFinished{
		public static final int TRANSACTION_INCOMPLETE = 0;
		public static final int TRANSACTION_ADD = 1;
		public static final int TRANSACTION_DELETE = 2;
		public void onTransactionFinished(int action);
		
	}
	
	public void setTransactionFinishedListener(onDatabaseTransactionFinished listener){
		
		mDatabaseTransactionListener = listener;
	}
	
	private static StringDatabase mDatabase;
	
	public void initDatabase(Context context){
		mDatabase = new StringDatabase(context);
		mContext = context;
		
	}
	
	public void initDatabase(Context context, onDatabaseTransactionFinished listener){
		setTransactionFinishedListener(listener);
		mDatabase = new StringDatabase(context);
		
	}
	
	public void updatePhotoList(File[] photos){}

	public void updatePhotoList(String[] photos){}

	/**
	 * Add to the database, ran on a seperate thread
	 * @param favorite
	 */
	public  void AddToList(final String favorite) {
		
		new Thread(new Runnable(){

			@Override
			public void run() {
			long transcode = mDatabase.addString(favorite);
			LogWrapper.Logi(TAG, "The add transaction code is = " + transcode);
			if(transcode == StringDatabase.PATH_ERROR   || StringDatabase.INSERT_ERROR == transcode || StringDatabase.DATABASE_NOT_OPEN_ERROR == transcode){
					
				if(mDatabaseTransactionListener != null){
					LogWrapper.Logi(TAG, "Invoking transaction finished with code = " +  onDatabaseTransactionFinished.TRANSACTION_INCOMPLETE);
					mDatabaseTransactionListener.onTransactionFinished(onDatabaseTransactionFinished.TRANSACTION_INCOMPLETE);
					}
			
			}else if(transcode >= 0){
				
				if(mDatabaseTransactionListener != null){
					LogWrapper.Logi(TAG, 
							"Invoking transaction finished with code = " +  onDatabaseTransactionFinished.TRANSACTION_ADD);		
					mDatabaseTransactionListener.onTransactionFinished(onDatabaseTransactionFinished.TRANSACTION_ADD);

				}
			}
			}}).run();	
	}
	
	/**
	 * 
	 * Delete from the database, Ran from another thread
	 * @param favorite
	 */
	public  void RemoveFromList(final String favorite) {

		new Thread(new Runnable(){

			@Override
			public void run() {
				int transcode = mDatabase.deleteImage(favorite);
				LogWrapper.Logi(TAG, "The delete transaction code is = " + transcode);
				if(transcode > 0){
					if(mDatabaseTransactionListener != null){
						LogWrapper.Logi(TAG, 
								"Invoking transaction finished with code = " +  onDatabaseTransactionFinished.TRANSACTION_DELETE);	
						mDatabaseTransactionListener.onTransactionFinished(onDatabaseTransactionFinished.TRANSACTION_DELETE);
						}
				}else if(transcode == 0 || transcode == StringDatabase.PATH_ERROR ){
					if(mDatabaseTransactionListener != null){
						LogWrapper.Logi(TAG, 
								"Invoking transaction finished with code = " +  onDatabaseTransactionFinished.TRANSACTION_INCOMPLETE);		
						
						mDatabaseTransactionListener.onTransactionFinished(onDatabaseTransactionFinished.TRANSACTION_INCOMPLETE);
					}
					
				}
				
			}}).run();	
	}

	public Boolean isInDataBase(String path) {
		if(path == null)return false;
		return mDatabase.isImagePathPresent(path);
	}

	public String[] getAllEntries() {
		
		List<String> im = mDatabase.getAllStrings();
		int count = im.size();
		String[] paths = new String[count]; 
		for(int i = 0; i < count; i++){
			
			paths[i] = im.get(i);
			
		}	
		return paths;	
	}

	public boolean open(){
		if(mDatabase != null || !mDatabase.isOpen()){
			mDatabase.open();
            return true;
        }
		return false;
	}	
	
	public boolean close(){
		
		if(mDatabase != null || mDatabase.isOpen()){

				mDatabase.close();
                return true;
            }
        return false;
	}

	
	
	
	public boolean isOpen(){
		return mDatabase.isOpen();
	}
	
	

}
