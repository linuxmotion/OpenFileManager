package org.linuxmotion.filemanager.models;

import org.linuxmotion.filemanager.utils.Constants;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DualTouchListView extends ListView {

	private static boolean DBG = (true | Constants.FULL_DBG);
	private String TAG = this.getClass().getSimpleName(); 
	ListView mListView;
	
	private static final int INVALID_POINTER_ID = -1;

	
	private static float mInitialx, mInitialy, mInitialx2, mInitialy2;
	
	float mFirstTouchX, mFirstTouchY;
	float mSecondTouchX, mSecondTouchY;
	private boolean mStartPinchMode;
	private boolean mIsPinched;
	private boolean mTwoPoints = false;
	private boolean mInTwoTouchMode = false;
	private int mActivePointerId;
	
	private static DualPointListViewEventDispatcher mDualPointListViewDispatcher;
	
	public DualTouchListView(Context context){
		this(context, null);
		
	}
	
	public DualTouchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
	}
	/*
	@Override
	public boolean onTouchEvent(MotionEvent event){
		//Log.d(TAG, "DualTouchListView motion event");
		
		final int NumPoints = event.getPointerCount();
		final int action = event.getAction();
		
		 switch (action & MotionEvent.ACTION_MASK){
			 
			 case MotionEvent.ACTION_DOWN:{
				 
				 log("Action-down");
				 mActivePointerId = event.getPointerId(0);
				
				 
				 
			 }break;
			 
			 case MotionEvent.ACTION_POINTER_1_DOWN:{
				 
				 log("Action-pointer-1-down");
				 mInTwoTouchMode = true;
				
				 
				 
			 }break;
			 
			 
			 case MotionEvent.ACTION_MOVE:{
				
				 log("Action-move");
				 final int pointerIndex = event.findPointerIndex(mActivePointerId);
				 
				 if(!mInTwoTouchMode){
					 log("single-touch-move");
					 super.onTouchEvent(event);
				 }
				 

			 } break;
			
			 
			 case MotionEvent.ACTION_UP: {
				 
				 	log("Action-up");
				 	mInTwoTouchMode = false;
			        mActivePointerId = INVALID_POINTER_ID;
			       
			 }break;
			        
			    case MotionEvent.ACTION_CANCEL: {
			    	
			    	log("Action-cancel");
			        mActivePointerId = INVALID_POINTER_ID;
			        
			 }break;
	
			    case MotionEvent.ACTION_POINTER_UP: {
			    	
			    	log("Action-pointer-up");
			        // Extract the index of the pointer that left the touch sensor
			        final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
			                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			        final int pointerId = event.getPointerId(pointerIndex);
			        if (pointerId == mActivePointerId) {
			            // This was our active pointer going up. Choose a new
			            // active pointer and adjust accordingly.
			            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			            mSecondTouchX = event.getX(newPointerIndex);
			            mSecondTouchY = event.getY(newPointerIndex);
			            mActivePointerId = event.getPointerId(newPointerIndex);
			        }
			        
			        
			    }break;

		 
		 }
		
		/
		// Only handle to point else let the system do it
		if(NumPoints > 1){
			mInTwoTouchMode = true;
			mTwoPoints = true;
			mFirstTouchY = event.getY(0);
			mFirstTouchX = event.getX(0);

			mSecondTouchY = event.getY(1);
			mSecondTouchX = event.getX(1);
			
			Log.d(TAG, "Dual point touch");
			switch(action){
				
			case MotionEvent.ACTION_DOWN:
				mInitialx = mFirstTouchX;
				mInitialy = mFirstTouchY;
				mInitialx2 = mSecondTouchY;
				mInitialy2 = mSecondTouchY; 
				
				mStartPinchMode = isInPinchMode();
				mIsPinched = isPointerSeperated( mFirstTouchX, mSecondTouchX, mFirstTouchY, mSecondTouchY);
				break;
			
			case MotionEvent.ACTION_MOVE:
				try{
				
					if (!mStartPinchMode && mIsPinched)
					mDualPointListViewDispatcher.dispatchDualPointUnpinch();
				}
				catch(NullPointerException e){
					e.printStackTrace();
					return false;
				}
				break;
			
				
			}
			
				
    		
		}
		else{

			switch(action){

			case MotionEvent.ACTION_POINTER_2_UP:
				Log.d(TAG, "second pointer lifeted");
			
			}
			
				super.onTouchEvent(event);
			
		}
		/
		
		return true;
		
			
	}
	*/
	private boolean isInPinchMode() {
		
	
		
		float initx = Math.abs(mInitialx - this.mInitialx2);
		float inity = Math.abs(mInitialy - this.mInitialy2);
		
		float tmp = Math.abs(mSecondTouchX - mFirstTouchX);
		float tmp2 = Math.abs(mSecondTouchY - mFirstTouchY);
		
		if((initx < tmp) && (inity < tmp2))
			return true;
		
		return false;
	}

	public interface DualPointListViewEventDispatcher{
		
		void dispatchDualPointPinch();
		void dispatchDualPointUnpinch();
		
	}

	
	

	private boolean isPointerSeperated(float x1, float x2,float y1, float y2){
		
		int defaultspace = 50;
		
		float tmp = x2 - x1;
		float tmp2 = y2 - y1;
		
		if((tmp < defaultspace) && (tmp2 < defaultspace ))
			return false;
			
		return true;
	}

    
	public void setDispatcher(DualPointListViewEventDispatcher l){
		Log.d(TAG, "Setting the dispatcher");
		
		if(l != null) mDualPointListViewDispatcher = l;
		else Log.d(TAG, "The dispatcher is null");
	}
	
	public void setListView(ListView list){
		mListView = list;
		
	}
	
	private void log(String msg){
		
		if(DBG) Log.d(TAG, msg);
		
	}

}
