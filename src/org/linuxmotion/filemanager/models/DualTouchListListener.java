package org.linuxmotion.filemanager.models;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;

public class DualTouchListListener extends SimpleOnGestureListener {

	private static final int REL_SWIPE_MAX_OFF_PATH = 60;

	private static final int REL_SWIPE_THRESHOLD_VELOCITY = 30;

	private static final int REL_SWIPE_MIN_DISTANCE = 10;
	
	private static String TAG = DualTouchListListener.class.getSimpleName();
	private DualTouchListListenerDispatcher mDualTouchListListenerDispatcher;

	private Context mContext;
	
	public DualTouchListListener(Context context){
		
		mContext = context;
		
		
	}
	

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		Log.d(TAG, "Fling detected");
		
		try {
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH) {
				Log.d(TAG,"Fling consumed");
				return false; 
			}
	        if(e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && 
	            Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) { 
	        	if(mDualTouchListListenerDispatcher != null)mDualTouchListListenerDispatcher.dispatchLeftFling(); 
	        }  else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE && 
	            Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) 
	        { 
	        	
	        	if(mDualTouchListListenerDispatcher != null)mDualTouchListListenerDispatcher.dispatchRightFling(); 
	        } 
	        
            
        } catch (Exception e) {
            // nothing
        }

		
		return true;
	}
	
	
	public interface DualTouchListListenerDispatcher{
		
		void dispatchLeftFling();
		void dispatchRightFling();
		
		
	}
	
	public DualTouchListListener setDispatcher(DualTouchListListenerDispatcher l){
		
		if(l != null)mDualTouchListListenerDispatcher = l;
		
		return this;
		
		
	}
	
	
}
