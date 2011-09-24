package org.linuxmotion.filemanager.models;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;

public class DualTouchListListener extends SimpleOnGestureListener {

	public static final int REL_SWIPE_MAX_OFF_PATH = 50;

	public static final int REL_SWIPE_THRESHOLD_VELOCITY = 20;

	public static final int REL_SWIPE_MIN_DISTANCE = 10;
	
	private static String TAG = DualTouchListListener.class.getSimpleName();
	private DualTouchListListenerDispatcher mDualTouchListListenerDispatcher;

	private Context mContext;

	private boolean mInSlideMode = false;;
	
	public DualTouchListListener(Context context){
		
		mContext = context;
		
	}
	

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		Log.d(TAG, "Fling detected");
		
		try {
			if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH) {
				Log.d(TAG,"Fling consumed");
				
			}
	        if(e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && 
	            Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) { 
	        	Log.d(TAG,"Calling left fling");
	        	
	        	if(mDualTouchListListenerDispatcher != null)mDualTouchListListenerDispatcher.dispatchLeftFling();
	        	return true;
	        }  else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE &&
	        		Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) 
	        { 
	        	Log.d(TAG,"Calling right fling");
	        	if(mDualTouchListListenerDispatcher != null)mDualTouchListListenerDispatcher.dispatchRightFling();
	        	return true;
	        
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
