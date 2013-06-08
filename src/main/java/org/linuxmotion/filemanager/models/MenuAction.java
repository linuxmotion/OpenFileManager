package org.linuxmotion.filemanager.models;

import org.linuxmotion.filemanager.R;

/**
 * Created by john on 6/19/13.
 */
public class MenuAction{
    public static final int ACTION_NONE = 0;
    public static final int ACTION_BACK = 1;
    public static final int ACTION_UP = 2;
    public static final int ACTION_FORWARD =  3;



    private int mCurrentAction;

    public MenuAction(int action){

        mCurrentAction = action;
    }

    public int getCurrentAction() {
        return mCurrentAction;
    }
}