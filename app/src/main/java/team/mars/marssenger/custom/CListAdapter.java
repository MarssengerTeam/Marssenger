package team.mars.marssenger.custom;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends BaseAdapter {

    public CListAdapter (){

    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public class ViewHolder{
        private TextView name;
        private TextView lastMessage;
        private ImageView image;
        private TextView counter;
    }

}
