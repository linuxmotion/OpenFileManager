package org.linuxmotion.filemanager.utils;

import java.util.Vector;

/**
 * Created by john on 6/21/13.
 */
public class NavigationHistory {

    private Vector<String> mHistory;
    private int mLocation;


    public NavigationHistory() {
        mHistory = new Vector<String>();
        mLocation = 0;

    }

    public boolean addToHistory(String path) {
        return mHistory.add(path);
    }

    public boolean resetHistoryTop(int location) {
        // never set a negative location
        if (location < 0)
            return false;

        mHistory.setSize(location + 1);

        return true;
    }

    public String getBackNavigation() {

        return mHistory.get(mLocation - 1);

    }
}
