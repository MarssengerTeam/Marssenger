package team.mars.marssenger.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import team.mars.marssenger.R;
import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends RecyclerView.Adapter<CListAdapter.ViewHolder> {

    private Context context;
    private ChatDatabase chats;
    private ArrayList<Chat> chatlist;

    //layout-attr
    private RelativeLayout relativeLayout;

    public CListAdapter (Context context, ChatDatabase list){
        this.context=context;
        this.chats=list;
        chatlist = list.getAllChat();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        relativeLayout =(RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mars_list_item, viewGroup, false);

        return new ViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        //TODO set text and image and counter
        holder=new ViewHolder(relativeLayout);
        holder.name.setText(chatlist.get(i).getName());
        holder.text.setText(chats.getLastMessage(chatlist.get(i)).getMessage());
        holder.counter.setText(chats.getUnreadMessages(chatlist.get(i))+"");
    }

    @Override
    public long getItemId(int i) {
        if (i<chatlist.size() && i>=0){
            return chatlist.get(i).getId();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView text;
        public ImageView image;
        public TextView counter;

        public ViewHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            this.name=(TextView) relativeLayout.findViewById(R.id.listitem_name);
            this.text=(TextView) relativeLayout.findViewById(R.id.listitem_text);
            this.counter=(TextView) relativeLayout.findViewById(R.id.listitem_counter);
            this.image=(ImageView) relativeLayout.findViewById(R.id.listitem_image);
        }
    }

}