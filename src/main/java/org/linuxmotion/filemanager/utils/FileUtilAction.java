package org.linuxmotion.filemanager.utils;

import org.linuxmotion.asyncloaders.LogWrapper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by john on 6/27/13.
 */
public class FileUtilAction {

    private static final String TAG = FileUtilAction.class.getSimpleName();

    private ArrayList<File> mHeldArrayFile = new ArrayList<File>();

    public boolean setHeldFiles(File[] toCut) {


        //Check to see if there are files
        // to cut, if there are non
        // clear the files, and exit
        if (toCut == null) {
            // clear the olf files
            mHeldArrayFile.clear();
            return false;
        }
        // Clear the old files
        mHeldArrayFile.clear();

        for (File f : toCut)
            mHeldArrayFile.add(f);

        LogWrapper.Logv(TAG, "Holding " + mHeldArrayFile.size());

        return true;


    }

    public boolean appendToHeldFiles(File[] toCut) {

        for (File f : toCut) {
            mHeldArrayFile.add(f);
        }
        return true;


    }

    public File[] getHeldFiles() {
        File[] files = new File[mHeldArrayFile.size()];
        for (int i = 0; i < mHeldArrayFile.size(); i++)
            files[i] = mHeldArrayFile.get(i);
        return files;
    }


    public static boolean cutPasteHeldFiles(File[] toCut, File location) {

        boolean success = false;
        if (toCut == null || toCut.length == 0)
            return false;

        LogWrapper.Logv(TAG, "Cutting " + toCut.length + " files to location " + location);
        for (File f : toCut) {
            File tmp = new File(location + "/" + f.getName());
            LogWrapper.Logv(TAG, "New file = " + tmp);
            if (f.renameTo(tmp)) {
                //f.delete();
                LogWrapper.Logd(TAG, "Successfully pasted file to" + tmp);
                success = true;
            } else {
                LogWrapper.Logd(TAG, "Unsuccessfully pasted file to" + tmp);

            }
        }


        return success;


    }

    public boolean shareFiles(File[] toShare) {


        return false;


    }

    public boolean renameFiles(File[] toRename, String fileName) {

        int i = 0;
        boolean success = false;

        if (toRename.length == 1) {
            File tmp = new File(toRename[0].getPath() + fileName);
            success = toRename[0].renameTo(tmp);
        }

        for (File f : toRename) {

            File tmp = new File(f.getPath() + fileName + 0);
            success = f.renameTo(tmp);

        }
        return success;


    }


}
