package team.mars.marssenger.chat;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import team.mars.marssenger.R;

/**
 * Created by Kern on 09.12.2014.
 */
public class ChatFragment extends Fragment implements ChatView {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;

    public ChatFragment (){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        linearLayout=(LinearLayout) inflater.inflate(R.layout.fragment_chat, container, false);
        return linearLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //do stuff with the layout such as fill recyclerview with messages and such
        if (linearLayout!=null) {
            recyclerView = (RecyclerView) linearLayout.findViewById(R.id.chat_listview);
            if (recyclerView!=null){
                //TODO set adapter and layoutmanager
            } else {
                test("recyclerview null");
            }
        } else {
            test("linearlayout null");
        }
    }

    private void test(CharSequence charSequence){
        Toast.makeText(getActivity().getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }
}

