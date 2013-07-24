package org.linuxmotion.filemanager.models.baseadapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.linuxmotion.asyncloaders.LogWrapper;

import java.util.ArrayList;

/**
 * Created by john on 6/26/13.
 */
public class BaseArrayAdapter<T> extends ArrayAdapter<T> {

    private static final boolean DBG = false;// (true || Constants.FULL_DBG);
    private static final String TAG = "FileArrayAdapter";

    protected ArrayList<T> mArrayList = new ArrayList<T>();

    public BaseArrayAdapter(Context context, int textViewResourceId, T[] data) {
        super(context, textViewResourceId, data);
        LogWrapper.Logv(TAG, "Creating an adapter from BaseArrayAdapter");
        mArrayList.clear();
        if (data != null && data.length > 0) {
            for (T item : data) {
                mArrayList.add(item);
            }
            LogWrapper.Logv(TAG, "Added " + mArrayList.size() + " items");
        }


    }

    @Override
    public void clear() {
        //super.clear();
        if (mArrayList != null) {
            LogWrapper.Logd(TAG,"Clearing " + mArrayList.size() + " items");
            mArrayList.clear();
            if (mArrayList.size() != 0){
                // Wtf happened to get here
                // why did the adapter not clear ??!!
                LogWrapper.Loge(TAG,"Couldn't clear the adapter");
                throw new RuntimeException("Couldn't clear the adapter");
            }
        }
    }

    @Override
    public void add(T item) {
        mArrayList.add(item);

    }

    @Override
    public long getItemId(int pos) {

        return pos;

    }

    @Override
    public int getCount() {

        return (mArrayList.size());

    }

    @Override
    public T getItem(int pos) {

        return mArrayList.get(pos);

    }

    @Override
    public void remove(T object) {
        super.remove(object);
        mArrayList.remove(object);

    }

    public ArrayList<T> getArrayList() {
        return mArrayList;
    }


}
