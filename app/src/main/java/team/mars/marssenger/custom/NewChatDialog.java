package team.mars.marssenger.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import team.mars.marssenger.R;
import team.mars.marssenger.database.DatabaseWrapper;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.main.Marssenger;

/**
 * Created by Jan-Niklas on 29.01.2015.
 */
public class NewChatDialog extends DialogFragment {

    private DatabaseWrapper database;
    private EditText et_phoneNumber;
    private EditText et_name;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_newchat, null);
        et_phoneNumber = (EditText) dialogView.findViewById(R.id.et_phoneNumber);
        et_name = (EditText) dialogView.findViewById(R.id.et_name);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String phoneNumber = et_phoneNumber.getText().toString();
                        String name = et_name.getText().toString();
                        Log.d("NewChatDialog", name + phoneNumber);
                        //TODO HERE
                        database = ((Marssenger) Marssenger.getInstance()).getDatabase();
                        database.addChatToDB(name, phoneNumber, false);
                        ArrayList<Chat> chats = database.getChats();
                        for (int i = 0; i < chats.size(); i++) {
                            if (chats.get(i).getName().compareTo(name)==0 && chats.get(i).getReceiver().compareTo(phoneNumber)==0){
                                database.addMessageToDB(chats.get(i).getMessageTableId(), "Instantiation message for "+name+", not delivered!", 0,0,0);

                            }
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }


}
