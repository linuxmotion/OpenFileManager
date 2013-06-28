/*
 *    This file is part of openFileManager.
 *
 *    openFileManager is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    openFileManager is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.*
 *
 *    You should have received a copy of the GNU General Public License
 *    along with openFileManager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.linuxmotion.filemanager.models.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import org.linuxmotion.asyncloaders.BitmapHelper;
import org.linuxmotion.asyncloaders.ImageLoader;
import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.ExtendedMimeTypeMap;
import org.linuxmotion.filemanager.models.baseadapters.BaseArrayAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileArrayAdapter extends BaseArrayAdapter<File> {

    private static String TAG = "FileArrayAdapter";
    protected ImageLoader mImageLoader;
    protected Bitmap mFolderBG;
    protected Bitmap mAudioBG;
    protected Bitmap mTextBG;
    protected Bitmap mVideoBG;
    protected Bitmap mZipBG;
    protected Bitmap mUnknownBG;

    public FileArrayAdapter(Context context, File[] files) {
        super(context, 0, files);

        mImageLoader = new ImageLoader(context, R.drawable.ic_menu_gallery);

        setupBGImages(context);

    }

    public FileArrayAdapter(Context context, File file) {
        this(context, file.listFiles());
    }


    void setupBGImages(Context context) {
        mFolderBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_folder);
        mAudioBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_menu_audio);
        mTextBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_menu_compose);
        mVideoBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_menu_video);
        mZipBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_menu_application_zip);
        mUnknownBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.icon);
    }

    private class ViewHolder {
        public ImageView mThumbnail;
        public TextView mFilePath;
        public TextView mFileMain;
        public TextView mFileExtras;
        public TextView mFileExtrasTwo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder Holder = new ViewHolder();
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.file_list_item, parent, false);
            Holder.mThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            Holder.mFilePath = (TextView) v.findViewById(R.id.file_path);
            Holder.mFileMain = (TextView) v.findViewById(R.id.file_main);
            Holder.mFileExtras = (TextView) v.findViewById(R.id.file_extras);
            Holder.mFileExtrasTwo = (TextView) v.findViewById(R.id.file_extras_two);
            v.setTag(Holder);

        } else {

            Holder = (ViewHolder) v.getTag();
        }

        if (getArrayList().isEmpty()) {
            Log.d(TAG, "The listview is empty");
        } else {
            File it = getArrayList().get(position);

            if (it != null) {
                LogWrapper.Logv(TAG, it.toString());
                LogWrapper.Logv(TAG, "Setting resources");


                if (Holder.mThumbnail != null) {


                    if (it.isFile()) {
                        LogWrapper.Logv(TAG, "Setting file image");
                        // If it a pic set it as
                        // a pic, else set it as
                        // a blnk doc file icon

                        String name = it.getName();

                        LogWrapper.Logv(TAG, "Full name: " + name);
                        int lastdot = name.lastIndexOf(".");
                        String s = name.substring(lastdot + 1);

                        String ext = MimeTypeMap.getFileExtensionFromUrl(it.getName());
                        if (ext.equals("")) {
                            // Fallback case where manual retrieval of the last dot is needed
                            // Though this shouldn't happen it does
                            setIconType(Holder.mThumbnail, it, s);

                        } else {
                            setIconType(Holder.mThumbnail, it, ext);

                        }


                    } else {
                        LogWrapper.Logv(TAG, "Setting folder background");
                        Holder.mThumbnail.setImageBitmap(mFolderBG);
                    }
                }


                if (Holder.mFilePath != null) {
                    LogWrapper.Logv(TAG, "Setting text");
                    Holder.mFilePath.setText(it.getName());
                }

                {

                    String extras;
                    long size = it.length() / 1000000;
                    if (size == 0) {
                        size = it.length();
                        extras = Long.toString(size) + "Kb";
                    } else {

                        extras = Long.toString(size) + "Mb";
                    }
                    Holder.mFileMain.setText(extras);

                }

                Date d = new Date(it.lastModified());
                {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String dateString = sdf.format(d);
                    Holder.mFileExtras.setText(dateString);
                }
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                    String dateString = sdf.format(d);
                    Holder.mFileExtrasTwo.setText(dateString);

                }

            }

        }

        return v;
    }

    protected void setIconType(ImageView iv, File path, String extension) {
        // set the image type by the icon.
        // Creat a maper that map the  extension to a file type
        // video, document, picture, or music


        ExtendedMimeTypeMap m = ExtendedMimeTypeMap.getSingleton();
        String mime = m.getMimeTypeFromExtension(extension);

        if (mime != null && mime.contains("image")) {

            mImageLoader.setImage(path.toString(), iv);

        } else if (mime != null && mime.contains("audio")) {
            iv.setImageBitmap(mAudioBG);

        } else if (mime != null && mime.contains("text") || extension.equals("js")) {

            //iv.setBackgroundResource(R.drawable.ic_menu_compose);
            iv.setImageBitmap(mTextBG);

        } else if (mime != null && mime.contains("video")) {

            //iv.setBackgroundResource(R.drawable.ic_list_menu_video);
            iv.setImageBitmap(mVideoBG);

        } else if (extension.equals("zip") || extension.equals("apk")) {

            //iv.setBackgroundResource(R.drawable.ic_list_menu_application_zip);
            iv.setImageBitmap(mZipBG);

        } else {

            //iv.setBackgroundResource(R.drawable.icon);
            iv.setImageBitmap(mUnknownBG);

        }


        return;

    }


}
