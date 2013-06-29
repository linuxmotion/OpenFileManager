package org.linuxmotion.filemanager.models.baseadapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;

import org.linuxmotion.asyncloaders.AeSimpleSHA1;
import org.linuxmotion.asyncloaders.LogWrapper;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by john on 6/26/13.
 */
public abstract class ExpandableBaseArrayAdapter<T>  implements ExpandableListAdapter {

    private static boolean DBG = false;// (true || Constants.FULL_DBG);
    private static String TAG = ExpandableBaseArrayAdapter.class.getSimpleName();
    public static long SINGLE_ID_CONST = 100L;
    public static long COMBINED_ID_CONST = 100L;

    protected ArrayList<T> mGroupList = new ArrayList<T>();
    protected ArrayList<ArrayList<Child>> mChildList = new ArrayList<ArrayList<Child>>();
    protected WeakReference<Context> mWeakContextReference;
    protected DataSetObserver mDataSetObserver;


    class ID{

        public long mGroup;
        public long mChild;
        ID(long group, long child ){
            mGroup = group;
            mChild = child;
        }

    }

    public static class Child{
        public long mGroup;
        public long mChild;
        public String mTitle;
        public String mPath;
        public Child(long group, long child, String title, String path ){
            mGroup = group;
            mChild = child;
            mPath = path;
            mTitle = title;
        }


    }



    public ExpandableBaseArrayAdapter(Context context,  T[] groups, ArrayList<ArrayList<Child>> children ) {
        mWeakContextReference = new WeakReference<Context>(context);



        mGroupList.clear();
        mChildList.clear();


        if (groups != null && groups.length > 0) {
            long it = 0;
            for (T item : groups) {

                mGroupList.add(item);
                // Bad hack to enable hashing
                // should be able to handle any object
                // Add a child group to the group list
                // this list may be empty
                //mChildList.add(new ArrayList<Child>());
                it += 1;

            }
        }


        mChildList = children;


    }



    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public void onGroupExpanded(int i) {

    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return  null;
    }

    @Override
    public View getChildView(int group, int child, boolean isLastChild, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int group) {
        return mChildList.get(group).size();
    }

    @Override
    public Object getGroup(int group) {
        return mGroupList.get(group);
    }

    @Override
    public Object getChild(int group, int child) {
        return mChildList.get(group).get(child);
    }

    @Override
    public boolean isEmpty() {
       return mGroupList.size() == 0 ? false : true;

    }

    @Override
    public long getCombinedGroupId(long groupId) {
        //String id = mCombinedID.get(new ID(groupId, -1));
       // long l = Long.getLong(id);

        return groupId * COMBINED_ID_CONST;
       // return l;
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return groupId * COMBINED_ID_CONST+ childId;

    }


    @Override
    public boolean isChildSelectable(int group, int child) {

        // All children are curerntly selectable
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public long getGroupId(int group) {
        return group * SINGLE_ID_CONST ;
    }

    @Override
    public long getChildId(int group, int child) {

        return group * SINGLE_ID_CONST + child;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        mDataSetObserver = null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        mDataSetObserver = dataSetObserver;
    }


    public boolean hasChildren(int group){

        LogWrapper.Logv(TAG,"Child list for group " + group+ " is size " +mChildList.get(group).size());
        if(mChildList.get(group).size() > 0)
            return true;

        return false;
    }
    /*
    public void removGroup(int group) {
        //super.remove(object);
        mChildList.remove(group);
        mGroupList.remove(group);

    }
    public void removeChild(int group, Object child) {
        //super.remove(object);
        mChildList.get(group).remove(child);

    }
    public void removeChild(int group, int child) {
        //super.remove(object);
        mChildList.get(group).remove(child);

    }

    public void clear() {
        //super.clear();
        if (mGroupList!= null) {
            mGroupList.clear();
        }
    }

    //@Override
    public void add(T item) {
        mGroupList.add(item);

    }

    //@Override
    public long getItemId(int pos) {

        return pos;

    }
*/

    public Context getContext(){
        return mWeakContextReference.get();
    }




}
