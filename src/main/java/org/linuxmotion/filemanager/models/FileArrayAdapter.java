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
package org.linuxmotion.filemanager.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.linuxmotion.asyncloaders.BitmapHelper;
import org.linuxmotion.asyncloaders.ImageLoader;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.utils.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileArrayAdapter extends ArrayAdapter<File> {

    private static boolean DBG = false;// (true || Constants.FULL_DBG);
    private static String TAG = "FileArrayAdapter";
    ImageLoader mImageLoader;
    Bitmap mFolderBG;
    private File[] mFiles;
    private ArrayList<File> mFileList = new ArrayList<File>();
    private Context mContext;
    private Bitmap mAudioBG;
    private Bitmap mTextBG;
    private Bitmap mVideoBG;
    private Bitmap mZipBG;
    private Bitmap mUnkownBG;

    public FileArrayAdapter(Context context, int textViewResourceId, File[] files) {
        super(context, textViewResourceId, files);

        mImageLoader = new ImageLoader(context, R.drawable.ic_menu_gallery);
        mFiles = files;
        mContext = context;
        mFileList.clear();
        if (mFiles != null && mFiles.length > 0) {
            for (File f : mFiles) {
                mFileList.add(f);
            }
        }

        setupBGImages(context);

    }

    public FileArrayAdapter(Context context, int textViewResourceId, File file) {
        super(context, textViewResourceId);
        mImageLoader = new ImageLoader(context, R.drawable.ic_menu_gallery);
        mFiles = file.listFiles();
        mContext = context;
        mFileList.clear();
        if (mFiles != null && mFiles.length > 0) {
            for (File f : mFiles) {
                mFileList.add(f);
            }
        }
        setupBGImages(context);
    }

    private static void log(String message) {

        if (DBG) Log.d(TAG, message);

    }

    void setupBGImages(Context context) {
        mFolderBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_folder);
        mAudioBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_menu_audio);
        mTextBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_menu_compose);
        mVideoBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_menu_video);
        mZipBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.ic_list_menu_application_zip);
        mUnkownBG = BitmapHelper.decodeSampledBitmapFromResource(context.getResources(), 50, 50, R.drawable.icon);
    }

    @Override
    public void clear() {
        super.clear();
        if (mFileList != null) {
            mFileList.clear();
        }
    }

    @Override
    public void add(File f) {
        mFileList.add(f);

    }

    public void updateList() {
        mFiles = new File[mFileList.size()];
        for (int i = 0; i < mFileList.size(); i++) {
            mFiles[i] = mFileList.get(i);
        }
    }

    @Override
    public long getItemId(int pos) {

        return pos;

    }

    @Override
    public int getCount() {

        return (mFileList.size());

    }

    @Override
    public File getItem(int pos) {

        return mFileList.get(pos);

    }
private class ViewHolder{
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
            LayoutInflater vi = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.file_list_item, parent, false);
            Holder.mThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
            Holder.mFilePath = (TextView) v.findViewById(R.id.file_path);
            Holder.mFileMain = (TextView) v.findViewById(R.id.file_main);
            Holder.mFileExtras = (TextView) v.findViewById(R.id.file_extras);
            Holder.mFileExtrasTwo = (TextView) v.findViewById(R.id.file_extras_two);
            v.setTag(Holder);

        }else{

            Holder = (ViewHolder) v.getTag();
        }

        if (mFiles == null) {
            Log.d(TAG, "The listview is empty");
        } else {
            File it = mFiles[position];

            if (it != null) {
                log(it.toString());
                log("Setting resources");



                if (Holder.mThumbnail != null) {


                    if (it.isFile()) {
                        log("Setting file image");
                        // If it a pic set it as
                        // a pic, else set it as
                        // a blnk doc file icon

                        String name = it.getName();

                        log("Full name: " + name);
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
                        log("Setting folder background");
                        Holder.mThumbnail.setImageBitmap(mFolderBG);
                    }
                }



                if (Holder.mFilePath != null) {
                    log("Setting text");
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

    private void setIconType(ImageView iv, File path, String extension) {
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
            iv.setImageBitmap(mUnkownBG);

        }


        return;

    }


}
