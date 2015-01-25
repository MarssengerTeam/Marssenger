package team.mars.marssenger.custom;

import android.app.ActionBar;
import android.content.Context;
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
    private int positionLastChecked;
    private int typeLastChecked;

    private long oneMin;

    private RelativeLayout relativeLayout;
    private Chat chat;

    private ArrayList<ViewHolder> viewHolderList;

    private Context context;

    public CChatListAdapter(MessageDatabase database, Chat chat, Context context){
        this.context=context;
        this.chat=chat;
        mData=database.getAllMessageFromChat(chat);
        mSentIndex=new TreeSet <>();

        for (int i=0;i<mData.size();i++){
            if (mData.get(i).isSender()){
                mSentIndex.add(i);
            }
        }
        viewHolderList=new ArrayList<>();
        positionLastChecked=0;
        Calendar now=Calendar.getInstance();
        Calendar then=Calendar.getInstance();
        then.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE)+1,now.get(Calendar.SECOND));
        oneMin=then.getTimeInMillis()-now.getTimeInMillis();

    }

    //TODO method to edit margins on certain views
    private void combineMessages(int last, int next){
        RelativeLayout.LayoutParams layoutParams=
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        /*
        margins right now:
        top: message_top_margin
        bottom: <none>
         */
        //left, top, right, bottom
        layoutParams.setMargins(0,0,0,0);
        //remove top margin from next
        if (next<viewHolderList.size()) {
            viewHolderList.get(next).setMargins(layoutParams);
        }
        //remove timestamp from last
        if (last<viewHolderList.size()) {
            viewHolderList.get(last).removeTimestamp();
        }

    }

    public void updateLayout(){
        //check them messages for combining them
        typeLastChecked=getItemViewType(positionLastChecked);
        int nextCheck=positionLastChecked++;
        ArrayList<Integer> positions=new ArrayList<>();
        while (positionLastChecked<mData.size()){
            if (nextCheck<mData.size()){
                if (match(positionLastChecked,nextCheck)){
                    combineMessages(positionLastChecked,nextCheck);
                }
            }
            //increase both integers for the next check
            positionLastChecked++;
            nextCheck++;
        }
    }

    public boolean match(int last, int next){
        //check if types match
        if ((mSentIndex.contains(last) && mSentIndex.contains(next)) ||
                (!mSentIndex.contains(last) && !mSentIndex.contains(next))){
            //check if time is near enough
            if (mData.get(next).getTimestamp()-mData.get(last).getTimestamp()<=oneMin){
                return true;
            }
        }
        return false;
    }

    public void addMessage(Message message){
        this.mData.add(message);
        if (message.isSender()){
            mSentIndex.add(mData.size()-1);
        }
        notifyDataSetChanged();
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
        return new ViewHolder(relativeLayout, viewType);
    }

    @Override
    public void onBindViewHolder(CChatListAdapter.ViewHolder holder, int position) {
        holder.message.setText(mData.get(position).getMessage());
        holder.time.setText(getStringFromTime(mData.get(position).getTimestamp()));
        viewHolderList.add(holder);
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
        public RelativeLayout relativeLayout;

        public ViewHolder(RelativeLayout relativeLayout, int type) {
            super(relativeLayout);
            this.relativeLayout=(RelativeLayout) relativeLayout.findViewById(R.id.item_root);
            this.message=(TextView) relativeLayout.findViewById(R.id.message);
            this.time=(TextView) relativeLayout.findViewById(R.id.time);
            switch (type){
                case TYPE_RECEIVED:
                    //color accent
                    relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.accent));
                    break;
                /*
                case TYPE_SENT:
                    relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                    break;
                */
                default:break;
            }
        }

        public void setMargins(RelativeLayout.LayoutParams layoutParams){
            relativeLayout.setLayoutParams(layoutParams);
        }

        public void removeTimestamp(){
            time.setText("");
        }
    }
}
