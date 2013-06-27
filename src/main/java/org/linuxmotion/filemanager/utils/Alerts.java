package org.linuxmotion.filemanager.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;

import org.linuxmotion.filemanager.models.FileDeleteDialogClickListener;

import java.io.File;

/**
 * Wrapper class for showing different alerts.
 *
 * @author john
 */
public class Alerts {

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
                if(files != null && files.length > 0){
                    for (File f : files){

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
