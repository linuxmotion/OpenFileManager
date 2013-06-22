package org.linuxmotion.filemanager.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

import org.linuxmotion.filemanager.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by john on 6/21/13.
 */
public class DrawerListAdapter extends ArrayAdapter<String> {

    private WeakReference<Context> mWeakContextReference;
    ArrayList<String> mArrayList;
    String[] mStringList;


    public DrawerListAdapter(Context context, String[] objects){
        super(context, 0, objects);
        mStringList = objects;
        mWeakContextReference = new WeakReference<Context>(context);
        mArrayList = new ArrayList<String>();
        for (int i = 0; i < mStringList.length; i ++){
            mArrayList.add(mStringList[i]);
        }
    }
    @Override
    public void clear() {
        super.clear();
        if (mArrayList != null) {
            mArrayList.clear();
        }
        updateList();
    }

    @Override
    public void add(String s) {
        mArrayList.add(s);
        updateList();

    }
    public void updateList() {
        mStringList = new String[mArrayList.size()];
        for (int i = 0; i < mArrayList.size(); i++) {
            mStringList[i] = mArrayList.get(i);
        }
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
    public String getItem(int pos) {

        return mArrayList.get(pos);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DrawerListHolder Holder = new DrawerListHolder();
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mWeakContextReference.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.drawer_list_item, parent, false);
            Holder.mName = (TextView) convertView.findViewById(R.id.list_drawer_main_name);
            Holder.mThumbnail = (ImageView) convertView.findViewById(R.id.list_drawer_thumbnail);
            Holder.mThumbnailExtra = (TextView) convertView.findViewById(R.id.list_drawer_thumbnail_extra);
            Holder.mThumbnailLayout = (LinearLayout) convertView.findViewById(R.id.list_drawer_thumbnail_layout);
            convertView.setTag(Holder);
        } else {

            Holder = (DrawerListHolder) convertView.getTag();
        }


        Holder.mThumbnail.setImageResource(R.drawable.ic_thumb_home);
        Holder.mThumbnail.setVisibility(View.VISIBLE);
        Holder.mName.setText(mStringList[position]);




            return convertView;
    }


    private class DrawerListHolder{

        ImageView mThumbnail;
        TextView mName;
        TextView mThumbnailExtra;
        LinearLayout mThumbnailLayout;

    }

}
