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
import android.widget.TextView;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.FileDeleteDialogClickListener;
import org.linuxmotion.filemanager.models.adapters.OpenAsAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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


    public interface RenameAlertListener{
        public void onSelectPositiveButton(String newName);

    }


    public static void renameAlertBox(Context context, final RenameAlertListener listener){

        Builder rename = new AlertDialog.Builder(context);
        rename.setTitle("Rename files");
        rename.setCancelable(true);
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_dialog_rename, null, false);
        rename.setView(v);
        final EditText text = (EditText) v.findViewById(R.id.dialog_file_folder_rename);

        rename.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing if no name
                if (text.getText().toString().equals(""))
                    return;

                LogWrapper.Logv(TAG, "Invoking the callback listener");
                listener.onSelectPositiveButton(text.getText().toString());

            }


        });
        rename.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Do nothing

            }


        });

        rename.show();

    }


    public interface OpenAsListener {
        public void onSelectTypeText();
        public void onSelectTypePictures();
        public void onSelectTypeMusic();
        public void onSelectTypeVideo();
    }
    public static void OpenAsAlertBox(Context context,  final OpenAsListener listener){


        String[] dialogs = {"Text","Music", "Video", "Picture"};

        AlertDialog.Builder rename = new AlertDialog.Builder(context);
        rename.setTitle("Open As");
        rename.setCancelable(true);
        //View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_dialog_open_as, null, false);
        //rename.setView(v);

        rename.setAdapter(new OpenAsAdapter(context, 0, dialogs), new OnClickListener(){

                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                          switch (i){

                              case 0:{
                                  listener.onSelectTypeText();
                              }break;
                              case 1:{
                                  listener.onSelectTypeMusic();
                              }break;
                              case 2:{
                                  listener.onSelectTypeVideo();
                              }break;
                              case 3:{
                                  listener.onSelectTypePictures();
                              }break;
                          }

                      }
                  });

        rename.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Do nothing

            }


        });


        rename.create().show();
    }
}
