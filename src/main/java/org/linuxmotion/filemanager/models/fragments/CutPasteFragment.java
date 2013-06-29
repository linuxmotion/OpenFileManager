package org.linuxmotion.filemanager.models.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.adapters.ImageArrayAdapter;

import java.io.File;

/**
 * Created by john on 6/27/13.
 */
public class CutPasteFragment extends Fragment {

    private static final String TAG = CutPasteFragment.class.getSimpleName();

    private GridView mGridView;
    private onPasteListener mPasteListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogWrapper.Logi(TAG, "onCreateView called");

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.layout_cut_paste_grid, container, false);

        mGridView = (GridView) layout.findViewById(R.id.grid_cut_paste);
        mGridView.setAdapter(new ImageArrayAdapter(this.getActivity(), new File[]{}));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // do someting

            }
        });
        Button ok = (Button) layout.findViewById(R.id.button_grid_select_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteSelection();
            }
        });
        Button cancel = (Button) layout.findViewById(R.id.button_grid_select_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelPasteSelection();
            }
        });

        return layout;
    }


    public GridView getGridView() {
        return mGridView;
    }

    public ListAdapter getAdapter() {
        return mGridView.getAdapter();
    }

    public void setPasteListener(onPasteListener l) {
        mPasteListener = l;
    }

    private void cancelPasteSelection() {

        if (mPasteListener == null)
            throw new RuntimeException("Class must implement PasteListener()");

        LogWrapper.Logv(TAG, "canceling selected paste operation");
        mPasteListener.onCancelPaste();
    }

    private void pasteSelection() {
        if (mPasteListener == null)
            throw new RuntimeException("Class must implement PasteListener()");
        ImageArrayAdapter adapter = ((ImageArrayAdapter) mGridView.getAdapter());
        File[] files = new File[adapter.getCount()];//
        for (int i = 0; i < adapter.getCount(); i++) {
            files[i] = adapter.getItem(i);

        }

        mPasteListener.onPaste(files);

    }


    public interface onPasteListener {

        public void onPaste(File[] files);

        public void onCancelPaste();


    }


}
