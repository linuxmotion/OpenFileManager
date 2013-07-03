package org.linuxmotion.filemanager.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.baseadapters.ExpandableBaseArrayAdapter;

import java.util.ArrayList;

/**
 * Created by john on 6/21/13.
 */
public class ExpandableDrawerListAdapter extends ExpandableBaseArrayAdapter<String> {
    public static final int HOME_INDEX = 0;
    public static final int SCDARD_INDEX = 1;
    public static final int FAVORITE_INDEX = 2;


    public static final int ITEM_NEW = 10;

    public ExpandableDrawerListAdapter(Context context, String[] objects) {
        super(context, objects, null);

    }

    public ExpandableDrawerListAdapter(Context context, String[] objects, ArrayList<ArrayList<Child>> children) {
        super(context, objects, children);

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


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


        Holder.mThumbnail.setVisibility(View.VISIBLE);
        Holder.mName.setText(((String) getGroup(groupPosition)));

        switch (groupPosition) {
            case HOME_INDEX: {
                Holder.mThumbnail.setImageResource(R.drawable.ic_thumb_home);
            }
            break;
            case SCDARD_INDEX: {
                Holder.mThumbnail.setImageResource(R.drawable.ic_thumb_home);
            }
            break;
            case FAVORITE_INDEX: {
                Holder.mThumbnail.setImageResource(R.drawable.ic_drawer_scaled);
            }
            break;
        }


        return convertView;
    }


    @Override
    public View getChildView(int group, int child, boolean isLastChild, View view, ViewGroup viewGroup) {

        DrawerListHolder Holder = new DrawerListHolder();
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.drawer_list_item, viewGroup, false);
            Holder.mName = (TextView) view.findViewById(R.id.list_drawer_main_name);
            Holder.mThumbnail = (ImageView) view.findViewById(R.id.list_drawer_thumbnail);
            Holder.mThumbnailExtra = (TextView) view.findViewById(R.id.list_drawer_thumbnail_extra);
            Holder.mThumbnailLayout = (LinearLayout) view.findViewById(R.id.list_drawer_thumbnail_layout);
            view.setTag(Holder);
        } else {

            Holder = (DrawerListHolder) view.getTag();

        }

        //Holder.mThumbnail.setImageResource(R.drawable.ic_thumb_home);


        Holder.mThumbnail.setVisibility(View.INVISIBLE);
        String txt = ((Child) getChild(group, child)).mTitle;
        Holder.mName.setText(txt);


        return view;
    }

    @Override
    public boolean addChild(int group, Child newChild) {
        if (group != newChild.mGroup)
            return false;

        return super.addChild(group, newChild);


    }

    private class DrawerListHolder {

        ImageView mThumbnail;
        TextView mName;
        TextView mThumbnailExtra;
        LinearLayout mThumbnailLayout;

    }

}
