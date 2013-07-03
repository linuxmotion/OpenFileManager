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
package org.linuxmotion.filemanager.utils;

import android.content.Context;
import android.util.Log;

import org.linuxmotion.filemanager.preferences.PreferenceUtils;

import java.io.File;

public class FileUtils {
    private static String TAG = FileUtils.class.getSimpleName();

    private final static boolean DUMP_DEBUG = false;

    private static final boolean DGB = (true || Constants.FULL_DBG);

    /**
     * @param directory the director path from which to retrive the files from
     * @return
     */
    public static File[] getFilesInDirectory(String directory, Context context) {


        File temp = new File(directory);
        if (temp.exists()) {


            if (temp.listFiles() != null) {
                Log.d(TAG, "[Path = " + temp.toString() + " exists");
                return temp.listFiles();
            } else {
                // If this point is reached then the directory doesnt contain any
                // files
                Log.d(TAG, "the directory is empty, or not accessible");
                return null;
            }

        } else {
            Log.d(TAG, "Sdcard directory in not present");
            return null;
        }
    }

    /**
     * Starts the sorting process. This is a wrapper function that
     * calls all of the sort functions, that individually sort
     * files by different parameters. Although each function
     * only serves one task, each time it is accumulated
     *
     * @param toSort
     * @return
     */
    public static File[] sortFiles(File[] toSort, Context context) {


        //Debug.startMethodTracing();

        SortHiddenFilesFolders(toSort, context);
        Mergesort sorter = new Mergesort();
        sorter.Sort(toSort, context);
        SortByFileFolder(toSort, context);
        File[] hiddenfiles = ShowHideHiddenFilesFolders(toSort, context);

        // Debug.stopMethodTracing();
        return hiddenfiles;
    }

    public static class Mergesort {
        private File[] mData;
        private File[] mHelper;
        boolean mInAscendingMode;
        private int number;

        public void Sort(File[] values, Context context) {
            mInAscendingMode = PreferenceUtils.retreiveLexicographicallySmallerFirst(context);// Grab the real value from PrefrenceUtils
            mData = values;
            number = values.length;
            mHelper = new File[number];
            mergesort(0, number - 1);
        }

        private void mergesort(int low, int high) {
            // Check if low is smaller then high, if not then the array is sorted
            if (low < high) {
                // Get the index of the element which is in the middle
                int middle = low + (high - low) / 2;
                // Sort the left side of the array
                mergesort(low, middle);
                // Sort the right side of the array
                mergesort(middle + 1, high);
                // Combine them both
                merge(low, middle, high);
            }
        }

        private void merge(int low, int middle, int high) {

            // Copy both parts into the helper array
            for (int i = low; i <= high; i++) {
                mHelper[i] = mData[i];
            }

            int i = low;
            int j = middle + 1;
            int k = low;
            // Copy the smallest values from either the left or the right side back
            // to the original array
            while (i <= middle && j <= high) {
                // sort low to high
                if (mInAscendingMode ? mHelper[i].compareTo(mHelper[j]) < 0 : mHelper[i].compareTo(mHelper[j]) >= 0) {
                    mData[k] = mHelper[i];
                    i++;
                } else {
                    mData[k] = mHelper[j];
                    j++;
                }
                k++;
            }
            // Copy the rest of the left side of the array into the target array
            while (i <= middle) {
                mData[k] = mHelper[i];
                k++;
                i++;
            }

        }
    }

    /**
     * @param files   the files to sort
     * @param context To retrieve the sharedPrefreneces
     */
    private static File[] ShowHideHiddenFilesFolders(File[] files, Context context) {

        log("Show/Hiding hidden files");
        File[] FILES = files;
        boolean loop = false;
        boolean hide = PreferenceUtils.retreiveShowHideHiddenFilesFoldersPref(context);
        int length = FILES.length;
        boolean hide_folders = hide;
        boolean hide_files = hide;

        if (hide || hide_folders || hide_files) {

            // Set all hidden dir and files to null
            for (int i = 0; i < length; i++) {
                File f = FILES[i];
                boolean dir = f.isDirectory();
                boolean hidden = f.isHidden();

                if (dir && hidden && hide_folders) {

                    FILES[i] = null;


                }
                if (!dir && hidden && hide_files) {

                    FILES[i] = null;


                }


            }

            // Move all null files to the end of the list preserving non null order
            do {

                loop = false;
                for (int i = 0; i < FILES.length - 1; i++) {
                    File f = FILES[i];

                    if ((FILES[i] == null) && (FILES[i + 1] != null)) {
                        // Switch with the next item
                        FILES[i] = FILES[i + 1];
                        FILES[i + 1] = f;
                        loop = true;
                    }
                }


            } while (loop);


            // Shrink the new list
            // Find the first null value
            int nullstart = 0;

            for (int i = 0; i < FILES.length; i++) {


                if (FILES[i] == null) {
                    nullstart = i;
                    break;
                }
            }


            File[] Files = null;
            // Create a new File[] size - 1 of the null poistion
            if (nullstart > 0) {
                files = new File[nullstart];
                for (int i = 0; i < nullstart; i++) {

                    files[i] = FILES[i];

                }

                if (DUMP_DEBUG) dump(files);
                log("There is " + files.length + " files");
                return files;

            } else {

                return FILES;

            }

        } else {

            return files;

        }

    }

    /**
     * @param file    the files to sort
     * @param context To retrieve the sharedPrefreneces
     */
    private static void SortByFileFolder(File[] file, Context context) {
        File[] FILES = file;

        boolean loop = false;

        do {
            loop = false;

            for (int i = 0; i < FILES.length - 1; i++) {
                File f = FILES[i];
                File t = FILES[i + 1];

                if (!f.isDirectory() && t.isDirectory()) {
                    FILES[i] = t;
                    FILES[i + 1] = f;
                    loop = true;

                }


            }
        } while (loop);

        boolean FoldersFirst = PreferenceUtils.retreiveSortbyFoldersFilesPref(context);


        if (!FoldersFirst) {
            //TODO: reverse the order

            int filestart = 0;

            for (int i = 0; i < FILES.length; i++) {


                if (!FILES[i].isDirectory()) {
                    filestart = i;
                    break;
                }
            }


            int o = FILES.length - 1;
            for (int i = 0; i < filestart; i++) {

                File f = FILES[i];
                File t = FILES[o];

                FILES[i] = t;
                FILES[o] = f;

                o--;


            }


        }

        if (DUMP_DEBUG) dump(FILES);

        file = FILES;

    }

    // TODO: At some pint this should sort the list non-hidden -> hidden -> non-hidden -> hidden
    // the user should then be able to select using a preference

    /**
     * Sort into hidden folders then folders. Form there is sorts into hidden files then non hiden
     * files.
     *
     * @param files   the files to sort
     * @param context To retrieve the sharedPrefreneces
     */
    private static void SortHiddenFilesFolders(File[] files, Context context) {

        File[] FILES = files;

        boolean loop;
        do {
            loop = false;

            for (int i = FILES.length - 1; i > 0; i--) {

                File t = FILES[i];
                File f = FILES[i - 1];

                boolean a = t.isDirectory();
                boolean b = t.isHidden();
                boolean c = f.isDirectory();
                boolean d = f.isHidden();

                if (a && b && c && !d) {

                    FILES[i - 1] = t;
                    FILES[i] = f;
                    loop = true;

                }


            }
        } while (loop);

        if (DUMP_DEBUG) dump(FILES);

        files = FILES;

    }

    public static boolean hasExtension(String filename) {

        log("Filename: " + filename);
        char[] tfile = filename.toCharArray();
        for (int i = tfile.length - 1; i >= 0; i--) {

            if (tfile[i] == '.' && i != 0) {
                log("File extension found");
                return true;

            }

        }

        log("Does not have an extension");
        return false;


    }

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {

        String s = f.getName();
        int i = s.lastIndexOf('.');
         return s.substring(i + 1).toLowerCase();

    }

    private static void dump(File[] f) {

        for (int i = 0; i < f.length - 1; i++)
            log(i + " --- " + f[i].getName());

    }

    private static void log(String message) {

        if (DGB) Log.d(TAG, message);

    }


}
