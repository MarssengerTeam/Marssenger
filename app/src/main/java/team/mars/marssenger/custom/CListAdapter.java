package team.mars.marssenger.custom;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import team.mars.marssenger.R;
import team.mars.marssenger.database.ChatDatabase;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.TextMessage;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends RecyclerView.Adapter<CListAdapter.ViewHolder> {

    private ChatDatabase chats;
    private ArrayList<Chat> chatlist;

    //layout-attr
    private RelativeLayout layout;

    public CListAdapter (ChatDatabase list){
        this.chats=list;
        chatlist = chats.getAllChatByTime();
    }

    public Chat getItem(int position){
        return chatlist.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        layout =(RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        switch (chats.getLastMessage(chatlist.get(i)).getType()){
            case 0:
                holder.text.setText((String)chats.getLastMessage(chatlist.get(i)).getMessage());
                break;
            case 1:
                holder.text.setText("Picture");
                break;
            default:

                Log.e("CChatListAdapter", "Message type wrong / not implemented yet");
                break;

        }
        holder.name.setText(chatlist.get(i).getName());
        if(chats.getLastMessage(chatlist.get(i)).getType()==0){
            holder.text.setText(((TextMessage)chats.getLastMessage(chatlist.get(i))).getMessage());
        }else{
            holder.text.setText("Picture");
        }

        if (chats.getUnreadMessages(chatlist.get(i))>0){
            holder.counter.setText(String.valueOf(chats.getUnreadMessages(chatlist.get(i))));
        } else {
            holder.counter.setVisibility(View.GONE);
        }
        holder.timestamp.setText(convertTime(chats.getLastMessage(chatlist.get(i)).getTimestamp()));

        holder.counter.setText(chats.getUnreadMessages(chatlist.get(i))+"");
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm dd MMM yyyy");
        return format.format(date);
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
        public TextView timestamp;

        //gets view of type root in list_item.xml
        public ViewHolder(RelativeLayout layout) {
            super(layout);
            this.name=(TextView) layout.findViewById(R.id.listitem_name);
            this.text=(TextView) layout.findViewById(R.id.listitem_text);
            this.counter=(TextView) layout.findViewById(R.id.listitem_counter);
            this.image=(ImageView) layout.findViewById(R.id.listitem_image);
            this.timestamp=(TextView) layout.findViewById(R.id.listitem_time);
        }
    }

}