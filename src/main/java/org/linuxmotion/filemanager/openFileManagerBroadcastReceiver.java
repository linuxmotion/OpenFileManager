package org.linuxmotion.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.linuxmotion.filemanager.R;

public class openFileManagerBroadcastReceiver extends BroadcastReceiver {



    openFileManagerReceiverDispatcher mOpenFileManagerReceiverDispatcher;

    @Override
    public void onReceive(Context arg0, Intent intent) {

        Bundle extras = intent.getExtras();


        if (extras.containsKey("PATH")) {
            mOpenFileManagerReceiverDispatcher.dispatchPathUpdate(extras.getString("PATH"),extras.getInt("ACTION"));


        } else if (extras.containsKey("RESOURCE")) {
            mOpenFileManagerReceiverDispatcher.dispatchResourceUpdate(extras.getString("RESOURCE"));

        }


    }


    public interface openFileManagerReceiverDispatcher {

        void dispatchPathUpdate(String updatePath, int action );
         void dispatchResourceUpdate(String resourcePath);


    }


    public openFileManagerBroadcastReceiver setDispatcher(openFileManagerReceiverDispatcher l) {

        mOpenFileManagerReceiverDispatcher = l;

        return this;
    }
}
