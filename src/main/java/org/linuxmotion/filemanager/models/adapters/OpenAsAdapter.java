package org.linuxmotion.filemanager.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.linuxmotion.asyncloaders.LogWrapper;
import org.linuxmotion.filemanager.R;
import org.linuxmotion.filemanager.models.baseadapters.BaseArrayAdapter;

/**
 * Created by john on 7/24/13.
 */
public class OpenAsAdapter extends BaseArrayAdapter<String> {
    private static final String TAG = OpenAsAdapter.class.getSimpleName();

    public OpenAsAdapter(Context context, int textViewResourceId, String[] data) {
        super(context, textViewResourceId, data);
    }

    private class Holder{
        TextView mTextHolder;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogWrapper.Logi(TAG, "getView() called");
        Holder holder = new Holder();
        if(convertView == null){

            LogWrapper.Logv(TAG, "Inflating the convertView");
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_list_item_open_as, parent, false);
            TextView text = (TextView) convertView.findViewById(R.id.list_item_open_as);
            holder.mTextHolder = text;
            convertView.setTag(holder);
        }
        else{
            LogWrapper.Logv(TAG, "reusing the convertView");
            holder = (Holder) convertView.getTag();
        }

        holder.mTextHolder.setText(mArrayList.get(position));

        return convertView;
    }


}
