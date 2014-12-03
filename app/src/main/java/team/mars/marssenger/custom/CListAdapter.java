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
import team.mars.marssenger.datatype.Chat;

/**
 * Created by root on 03.12.14.
 */
public class CListAdapter extends RecyclerView.Adapter<CListAdapter.ViewHolder> {

    private ArrayList<Chat> chats;


    public CListAdapter (ArrayList<Chat> list){
        this.chats=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        RelativeLayout r =(RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mars_list_item, viewGroup, false);

        return new ViewHolder(r);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        //TODO set text and image and counter
        holder.name.setText(chats.get(i).getName());
        holder.text.setText(String.valueOf(chats.get(i).getId()));

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
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView text;
        public ImageView image;
        public TextView counter;

        public ViewHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);

        }
    }

}
