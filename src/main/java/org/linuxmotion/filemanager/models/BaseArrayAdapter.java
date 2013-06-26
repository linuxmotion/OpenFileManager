package org.linuxmotion.filemanager.models;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by john on 6/26/13.
 */
public class BaseArrayAdapter<T>  extends ArrayAdapter<T> {

    private static boolean DBG = false;// (true || Constants.FULL_DBG);
    private static String TAG = "FileArrayAdapter";

    protected ArrayList<T> mArrayList = new ArrayList<T>();

    public BaseArrayAdapter(Context context, int textViewResourceId, T[] data) {
        super(context, textViewResourceId, data);

        mArrayList.clear();
        if (data != null && data.length > 0) {
            for (T item : data) {
                mArrayList.add(item);
            }
        }


    }
    @Override
    public void clear() {
        //super.clear();
        if (mArrayList != null) {
            mArrayList.clear();
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

    protected ArrayList<T> getArrayList(){
        return mArrayList;
    }




}
