package team.mars.marssenger.custom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Chat> chats;

    public CListAdapter (Context context, ArrayList<Chat> list){
        this.context=context;
        this.chats=list;
    }


    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Chat getItem(int i) {
        if (i<chats.size() && i>=0) {
            return chats.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        if (i<chats.size() && i>=0){
            return chats.get(i).getId();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //Listenelement erstellen
        return null;
    }

    public class ViewHolder{
        private TextView name;
        private TextView lastMessage;
        private ImageView image;
        private TextView counter;
    }

}
