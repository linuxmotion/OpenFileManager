package org.linuxmotion.filemanager.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import org.linuxmotion.asyncloaders.ImageLoader;
import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;

import java.io.File;

/**
 * Created by john on 6/27/13.
 */
public class ImageArrayAdapter extends FileArrayAdapter {

    private static final String TAG = ImageArrayAdapter.class.getSimpleName();
    ImageLoader mImageLoader;

    public ImageArrayAdapter(Context context, File[] data) {
        super(context, data);

        mImageLoader = new ImageLoader(context, R.drawable.ic_menu_gallery);

    }

    private class GridItemHolder {
        public ImageView mImage;
        public TextView mImageName;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridItemHolder gridItem = new GridItemHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_grid_item, parent, false);

            gridItem.mImage = (ImageView) convertView.findViewById(R.id.grid_item_image);
            gridItem.mImageName = (TextView) convertView.findViewById(R.id.grid_item_text);
            convertView.setTag(gridItem);

        } else {

            gridItem = (GridItemHolder) convertView.getTag();

        }


        File it = getItem(position);
        if (it.isFile()) {


            String name = it.getName();

            LogWrapper.Logv(TAG, "Full name: " + name);
            int lastdot = name.lastIndexOf(".");
            String s = name.substring(lastdot + 1);

            String ext = MimeTypeMap.getFileExtensionFromUrl(it.getName());
            LogWrapper.Logv(TAG, "Ext = " + ext + " secondary ext = " + s);
            if (ext.equals("")) {
                // Fallback case where manual retrieval of the last dot is needed
                // Though this shouldn't happen it does
                setIconType(gridItem.mImage, it, s);

            } else {
                setIconType(gridItem.mImage, it, ext);

            }

        } else {
            LogWrapper.Logv(TAG, "Setting folder background");
            gridItem.mImage.setImageBitmap(mFolderBG);

        }

        gridItem.mImageName.setText(it.getName());


        return convertView;


    }


}
