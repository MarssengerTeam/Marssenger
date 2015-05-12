package team.mars.marssenger.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import java.util.ArrayList;

import team.mars.marssenger.R;
import team.mars.marssenger.communication.HttpsBackgroundService;
import team.mars.marssenger.database.DatabaseWrapper;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.main.MainActivity;
import team.mars.marssenger.main.Marssenger;

/**
 * Created by Jan-Niklas on 29.01.2015.
 */
public class NewChatDialog extends DialogFragment {

    private DatabaseWrapper database;
    private EditText et_phoneNumber;
    private EditText et_name;
    private Switch sw_groupChat;
    private ImageButton ib_plus;

    private HttpsBackgroundService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HttpsBackgroundService.myBinder binder = (HttpsBackgroundService.myBinder) service;
            mService = binder.getService();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();


        Intent intent2 = new Intent(getActivity(), HttpsBackgroundService.class);
        getActivity().bindService(intent2, mConnection, getActivity().BIND_AUTO_CREATE);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_newchat, null);
        et_phoneNumber = (EditText) dialogView.findViewById(R.id.newchatdialog_et_phoneNumber);
        et_name = (EditText) dialogView.findViewById(R.id.newchatdialog_et_name);
        sw_groupChat = (Switch) dialogView.findViewById(R.id.newchatdialog_sw_groupChat);
        ib_plus = (ImageButton) dialogView.findViewById(R.id.newchatdialog_ib_plus);
        ib_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = et_name.getText().toString();
                        if (!sw_groupChat.isChecked()) {
                            String phoneNumber = et_phoneNumber.getText().toString();
                            Log.d("NewChatDialog", name + phoneNumber);
                            //TODO HERE
                            database = ((Marssenger) Marssenger.getInstance()).getDatabase();
                            database.addChatToDB(name, phoneNumber, false);
                            ArrayList<Chat> chats = database.getChats();
                            for (int i = 0; i < chats.size(); i++) {
                                if (chats.get(i).getName().compareTo(name) == 0 && chats.get(i).getReceiver().compareTo(phoneNumber) == 0) {
                                    database.addMessageToDB(chats.get(i).getMessageTableId(), "Instantiation message for " + name + ", not delivered!", 0, 0, 0);

                                }
                            }
                        } else {
                            String phoneNumbers = et_phoneNumber.getText().toString();
                            String[] numbers;

                            numbers = phoneNumbers.split(",");
                            Log.d("NewChat", numbers.toString());



                            mService.createGroup(name, numbers);




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
