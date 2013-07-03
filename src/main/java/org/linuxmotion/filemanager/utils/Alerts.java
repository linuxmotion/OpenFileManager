package org.linuxmotion.filemanager.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.FileDeleteDialogClickListener;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper class for showing different alerts.
 *
 * @author john
 */
public class Alerts {

    private static final String TAG = Alerts.class.getSimpleName();
    private Context mContext;
    private static deleteAlertClickDispatcher mDELETEDispatcher;
    private static GPLAlertClickDispatcher mGPLDispatcher;

    /**
     * @param context The application context from which to invoke contextual functins from
     */
    public Alerts(Context context) {
        mContext = context;


    }

    /**
     * @param context Context from which to retrieve the prefreences from
     * @return True if the the GPL license should be shown
     */
    public static boolean shouldIssueGPLLicense(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.OPEN_FILE_MANAGER_PREFERENCES, 0);
        int version = prefs.getInt(Constants.APP_NAME, -1);

        if (version != Constants.VERSION_LEVEL)
            return true;

        return false;
    }


    ////////////////
    public void ShowGPLAlert() {

        GPLAlertBox(mContext);

    }

    private void GPLAlertBox(Context context) {

        Builder bGPL = new AlertDialog.Builder(context);
        String message = "openFileManager  Copyright (C) 2011  \nCreated by John A Weyrauch.\n " +
                "This program comes with ABSOLUTELY NO WARRANTY. For details press menu, then about. " +
                "This is free software, and you are welcome to use, modify, or redistribute it" +
                "under certain conditions.";

        bGPL.setTitle("GPL Usage license").setMessage(message).setCancelable(false);
        bGPL.setPositiveButton("Proceed", new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                if (mGPLDispatcher != null) mGPLDispatcher.onAgreeSelected();

            }


        });
        bGPL.setNegativeButton("Quit", new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                if (mGPLDispatcher != null) mGPLDispatcher.onQuitSelected();

            }


        });


        bGPL.show();
    }

    /**
     * GPL alert box dispatcher. Controls the what
     * happens when the user agrees to the GPL license
     * or not.
     *
     * @author john
     */
    public interface GPLAlertClickDispatcher {

        /**
         * Called when the "QUIT" button is pressed
         */
        void onQuitSelected();

        /**
         * Called when the "AGREE" button is selected
         */
        void onAgreeSelected();


    }


    /**
     * Set the GPL dispatcher
     *
     * @param l The dispatcher to set
     */
    public void setGPLDispatcher(GPLAlertClickDispatcher l) {

        if (l != null) mGPLDispatcher = l;

    }


    //////////////////////////////


    protected void deleteAlertBox(File[] file) {


        Builder delete = new AlertDialog.Builder(this.mContext);
        delete.setTitle("Warning");
        delete.setMessage("Are you sure you want to delete the file(s)");
        delete.setCancelable(false);

        FileDeleteDialogClickListener deletedialog = new FileDeleteDialogClickListener(mContext, file) {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                File[] files = retreiveFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {

                        mDELETEDispatcher.onSelectedDelete(f);
                    }

                }
            }

        };


        delete.setPositiveButton("Delete", deletedialog);

        delete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Do nothing

            }


        });

        delete.show();
    }

    public interface FileAlertBoxListener {

        public void onSelectPositiveButton();
    }

    public static void newFileAlertBox(Context context, final String location, final FileAlertBoxListener listener) {


        Builder delete = new AlertDialog.Builder(context);
        delete.setTitle("Create new file or folder");
        delete.setCancelable(true);
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_new_file_dialog, null, false);
        delete.setView(v);
        final EditText text = (EditText) v.findViewById(R.id.dialog_file_folder_editText);
        final Switch switcher = (Switch) v.findViewById(R.id.switch_file_folder);

        delete.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing if no name
                if (text.getText().toString().equals(""))
                    return;

                File newFile = new File(location + "/" + text.getText().toString());
                if (switcher.isChecked()) {
                    newFile.mkdir();
                } else {
                    try {
                        newFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogWrapper.Logv(TAG, "Couldn't create a new file or folder");
                    }

                }

                listener.onSelectPositiveButton();

            }
        });

        delete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Do nothing

            }


        });

        delete.show();
    }

    public interface deleteAlertClickDispatcher {

        void onSelectedDelete(File f);


    }

    public void setDeleteDispatcher(deleteAlertClickDispatcher l) {

        if (l != null) mDELETEDispatcher = l;

    }

    public void showDeleteAlertBox(File[] f) {

        deleteAlertBox(f);

    }


}
