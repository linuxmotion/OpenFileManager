package org.linuxmotion.filemanager.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.linuxmotion.filemanager.R;

/**
 * Created by john on 6/21/13.
 */
public class DrawerListAdapter extends BaseArrayAdapter<String> {



    public DrawerListAdapter(Context context, String[] objects){
        super(context, 0, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DrawerListHolder Holder = new DrawerListHolder();
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        Holder.mName.setText(getArrayList().get(position));




            return convertView;
    }


    private class DrawerListHolder{

        ImageView mThumbnail;
        TextView mName;
        TextView mThumbnailExtra;
        LinearLayout mThumbnailLayout;

    }

}
