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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import org.linuxmotion.filemanager.models.ExtendedMimeTypeMap;

import java.io.File;

public class Constants {

    public static final boolean FULL_DBG = false;

    public static final String UPDATE_INTENT = "org.linuxmotion.intent.UPDATE_UI";

    public static final String RESOURCE_VIEW_INTENT = "org.linuxmotion.intent.HANDLE_VIEW_RESOURCE";

    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getPath();

    public static final int REFRESH_UI = 1;

    public static final String OPEN_FILE_MANAGER_PREFERENCES = "OpenFileManagerPreferences";

    public static final String APP_NAME = "OpenFileManager-Version-Level";

    public static final int VERSION_LEVEL = 3;

    public static final String ABOUT_TO_EXIT = "ApplicationAboutToExit";

    public static final int UNKNOWN_FILE_TYPE = 10;

    public static final String HIDDEN_FILES_FOLDERS_PREF = "hidden_files_folders_pref";

    public static final String SORT_BY_FOLDERS_FILES_PREF = "sort_by_folders_then_files_pref";

    public static final String SORT_LEXICOGRAPHICALLY_SMALLER_FIRST_PREF = "sort_lexicographically_smaller_first_pref ";

    public static final String LEFT_NAVIGATION_TUTORIAL_PREF = "left_navigation_tutorial";

    public static final String RIGHT_CUT_PASTE_TUTORIAL_PREF = "right_cut_paste_tutorial";

    public static final String HOME_DIRECTORY_PREF = "favorite_base_pref_";


    public static ExtendedMimeTypeMap MIME = ExtendedMimeTypeMap.getSingleton();




}
