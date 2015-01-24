package team.mars.marssenger.custom;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import team.mars.marssenger.R;
import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends RecyclerView.Adapter<CListAdapter.ViewHolder> {

    private ChatDatabase chats;
    private ArrayList<Chat> chatlist;

    //layout-attr
    private CardView layout;

    public CListAdapter (ChatDatabase list){
        this.chats=list;
        chatlist = chats.getAllChatByTime();
    }

    public Chat getItem(int position){
        return chatlist.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        layout =(CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.name.setText(chatlist.get(i).getName());
        holder.text.setText(chats.getLastMessage(chatlist.get(i)).getMessage());
        holder.counter.setText(chats.getUnreadMessages(chatlist.get(i))+"");
    }

    public void updateCardView(){
        chatlist = chats.getAllChatByTime();
        notifyDataSetChanged();
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

        public ViewHolder(CardView layout) {
            super(layout);
            this.name=(TextView) layout.findViewById(R.id.listitem_name);
            this.text=(TextView) layout.findViewById(R.id.listitem_text);
            this.counter=(TextView) layout.findViewById(R.id.listitem_counter);
            this.image=(ImageView) layout.findViewById(R.id.listitem_image);
        }
    }

}