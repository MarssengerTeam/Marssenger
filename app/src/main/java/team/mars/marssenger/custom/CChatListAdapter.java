package team.mars.marssenger.custom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;

import team.mars.marssenger.R;
import team.mars.marssenger.database.MessageDatabase;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.Message;

/**
 * Created by Nicolas on 10/12/2014.
 */
public class CChatListAdapter extends RecyclerView.Adapter<CChatListAdapter.ViewHolder> {

    private static final int TYPE_SENT=0;
    private static final int TYPE_RECEIVED=1;

    private ArrayList<Message> mData;

    private TreeSet <Integer> mSentIndex;

    private RelativeLayout relativeLayout;
    private Chat chat;

    public CChatListAdapter(MessageDatabase database, Chat chat){
        this.chat=chat;
        mData=database.getAllMessageFromChat(chat);
        mSentIndex=new TreeSet <>();

        for (int i=0;i<mData.size();i++){
            if (mData.get(i).isSender()){
                mSentIndex.add(i);
            }
        }
    }


    @Override
    public CChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_RECEIVED:
                relativeLayout =(RelativeLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.received_item, parent, false);
                break;
            case TYPE_SENT:
                relativeLayout =(RelativeLayout) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sent_item, parent, false);
                break;
            default:break;
        }

        return new ViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(CChatListAdapter.ViewHolder holder, int position) {
        holder.message.setText(mData.get(position).getMessage());
        holder.time.setText(getStringFromTime(mData.get(position).getTimestamp()));
    }

    private String getStringFromTime(long time){
        Calendar c= Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.getTime().toString();
    }

    @Override
    public int getItemViewType(int position){
        return mSentIndex.contains(position)?TYPE_SENT:TYPE_RECEIVED;
    }

    public Chat getChat() {
        return chat;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView time;


        public ViewHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            this.message=(TextView) relativeLayout.findViewById(R.id.message);
            this.time=(TextView) relativeLayout.findViewById(R.id.time);
        }
    }
}
