package team.mars.marssenger.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import team.mars.marssenger.R;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Chat> chats;

    private LayoutInflater layoutInflater;

    public CListAdapter (Context context, ArrayList<Chat> list){
        this.context=context;
        this.chats=list;
        this.layoutInflater=(LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
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
        ViewHolder holder;

        if (view==null){

            holder=new ViewHolder();

            view=layoutInflater.inflate(R.layout.mars_list_item,null);

            holder.name=(TextView) view.findViewById(R.id.listitem_name);
            holder.lastMessage=(TextView) view.findViewById(R.id.listitem_text);
            holder.image=(ImageView) view.findViewById(R.id.listitem_image);

            view.setTag(holder);
        } else {
            holder=(ViewHolder) view.getTag();
        }

        holder.name.setText(chats.get(position).getName());
        //TODO set image and set last message

        return view;
    }

    public class ViewHolder{
        public TextView name;
        public TextView lastMessage;
        public ImageView image;
        public TextView counter;
    }

}
