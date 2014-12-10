package team.mars.marssenger.custom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

import team.mars.marssenger.R;
import team.mars.marssenger.datatype.Message;

/**
 * Created by Nicolas on 10/12/2014.
 */
public class CChatListAdapter extends RecyclerView.Adapter<CChatListAdapter.ViewHolder> {

    private static final int TYPE_SENT=0;
    private static final int TYPE_RECEIVED=1;

    private ArrayList<Message> mData;

    private TreeSet <Integer> mSentIndex;

    public CChatListAdapter(){
        mData=new ArrayList<Message>();
        mSentIndex=new TreeSet <Integer>();
    }


    @Override
    public CChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_RECEIVED:
                /* TODO
                relativeLayout =(RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.received_message, viewGroup, false);
                        */
                break;
            case TYPE_SENT:
                /* TODO
                relativeLayout =(RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.sent_message, viewGroup, false);
                 */
                break;
            default:break;
        }

        //return new ViewHolder(relativeLayout);
        return null;
    }

    @Override
    public void onBindViewHolder(CChatListAdapter.ViewHolder holder, int position) {
        //holder=new ViewHolder(relativeLayout);
        //holder.message.setText(mData.get(position).getMessage()?);
        //holder.time.setText(mData.get(position).getTime()?);
    }

    public void addReceivedItem(Message message){
        mData.add(message);
        notifyDataSetChanged();
    }

    public void addSentItem(Message message){
        mData.add(message);
        //set position in index
        mSentIndex.add(mData.size()-1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        return mSentIndex.contains(position)?TYPE_SENT:TYPE_RECEIVED;
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
            //TODO create layout
            //this.message=(TextView) relativeLayout.findViewById(id);
            //this.time=(TextView) relativeLayout.findViewById(id);
        }
    }
}
