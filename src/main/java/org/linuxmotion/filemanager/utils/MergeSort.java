package org.linuxmotion.filemanager.utils;

import android.content.Context;
import android.util.Log;

import org.linuxmotion.filemanager.preferences.PreferenceUtils;

import java.io.File;

/**
 * Created by john on 6/19/13.
 */
public  class MergeSort {



    public static class ThreadedMergesort extends Thread{


        private static final String TAG = ThreadedMergesort.class.getSimpleName();

        class SortThread extends Thread{
        int mLow; // Lower bounds of array to sort
        int mHigh;// Upper bounds of array to sort

            /**
             *
             * @param low The lower bounds of the range to sort
             * @param high The upper bounds of the array to sort
             */
        SortThread(int low, int high){
            mLow = low;
            mHigh = high;
        };

        @Override
        public void run(){
            mergesort(mLow, mHigh);
        }

    }
        private File[] mData;
        private File[] mHelper;
        boolean mInAscendingMode;
        private int mNumber;
        private int mDepth;
        private int mMaxDepth;

        public void Sort(File[] values, Context context, int maxDepth) {
            mInAscendingMode = PreferenceUtils.retreiveLexicographicallySmallerFirst(context);// Grab the real value from PrefrenceUtils
            mData = values;
            mNumber = values.length;
            mHelper = new File[mNumber];
            mMaxDepth = maxDepth;
            mDepth = 0;

        }
        @Override
        public void run(){

            mergesort(0, mNumber - 1);

        }

        private void mergesort(int low, int high) {
            // Check if low is smaller then high, if not then the array is sorted
            if (low < high) {
                // Get the index of the element which is in the middle
                int middle = low + (high - low) / 2;
                // Sort the left side of the array
                if(mDepth < mMaxDepth){
                    ThreadedSort(low, high, middle);
                }
                else{

                    mergesort(low, middle);
                    mergesort(middle + 1, high);
                    merge(low, middle, high);

                }
            }


        }

        private void ThreadedSort(int low, int high, int middle) {
            SortThread Left = new SortThread(low, middle);
            SortThread Right = new SortThread(middle + 1, high);

            Left.run();
            Right.run();
            // Sort the right side of the array
            //mergesort(middle + 1, high);

            try{
            Left.join();
            Right.join();

            }
            catch(InterruptedException ex){

                Log.d(TAG, ex.getStackTrace().toString());


            }
        finally{

                // Combine them both
                merge(low, middle, high);


        }
        }


        private void merge(int low, int middle, int high) {

            // Copy both parts into the helper array
            for (int i = low; i <= high; i++) {
                mHelper[i] = mData[i];
            }

            int i = low;
            int j = middle + 1;
            int k = low;
            // Copy the smallest values from either the left or the right side back
            // to the original array
            while (i <= middle && j <= high) {
                // sort low to high
                if (mInAscendingMode ? mHelper[i].compareTo(mHelper[j]) < 0 : mHelper[i].compareTo(mHelper[j]) >= 0) {
                    mData[k] = mHelper[i];
                    i++;
                } else {
                    mData[k] = mHelper[j];
                    j++;
                }
                k++;
            }
            // Copy the rest of the left side of the array into the target array
            while (i <= middle) {
                mData[k] = mHelper[i];
                k++;
                i++;
            }

        }
    }



}
