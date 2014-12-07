package team.mars.marssenger.util;

import android.util.Log;

import java.util.ArrayList;

import team.mars.marssenger.datatype.Message;

/**
 * Created by Kern on 07.12.2014.
 */
public class QuicksortMessages {
    ArrayList<Message> mlist;
    public ArrayList<Message> sortMessages(ArrayList<Message> mlist){
        this.mlist=mlist;
        qSort(0,mlist.size()-1);
        return mlist;

    }
    public QuicksortMessages(){


    }
    private void qSort(int links, int rechts) {
        if (links < rechts) {
            int i = partition(links,rechts);
            qSort(links,i-1);
            qSort(i+1,rechts);
        }
    }
   private int partition( int links, int rechts) {
        long pivot;int i, j;Message help;
        pivot = mlist.get(rechts).getTimestamp();
        i     = links;
        j     = rechts-1;
        while(i<=j) {
            if (mlist.get(i).getTimestamp() > pivot) {

                help = mlist.get(i);

                mlist.set(i,mlist.get(j));
                mlist.set(j,help);
                j--;
            } else i++;
        }
        // tausche x[i] und x[rechts]
        help = mlist.get(i);
        mlist.set(i,mlist.get(rechts));
        mlist.set(rechts,help);

        return i;
    }

}
