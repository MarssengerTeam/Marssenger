package team.mars.marssenger.chat;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import javax.xml.transform.Result;

import team.mars.marssenger.R;
import team.mars.marssenger.communication.HttpsBackgroundService;
import team.mars.marssenger.custom.CChatListAdapter;
import team.mars.marssenger.datatype.Chat;
import team.mars.marssenger.datatype.Message;
import team.mars.marssenger.main.MainActivity;
import team.mars.marssenger.main.MainInteractor;
import team.mars.marssenger.main.Marssenger;

public class ChatActivity extends ActionBarActivity implements
        ChatPresenter,
        Toolbar.OnMenuItemClickListener
{

    //mvc
    public static CChatListAdapter cChatListAdapter;
    private MainInteractor mainInteractor;

    private Chat chat;
    private Toolbar toolbar;

    //BackgroundService
    private HttpsBackgroundService mService;
    private boolean isBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            HttpsBackgroundService.myBinder binder = (HttpsBackgroundService.myBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.chat= (Chat) getIntent().getSerializableExtra(Chat.CHAT);
        this.mainInteractor= MainActivity.MAIN_INTERACTOR;

        setContentView(R.layout.activity_chat);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){
            toolbar.setTitle(chat.getName());
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            //menu
            toolbar.inflateMenu(R.menu.menu_chat);
            toolbar.setOnMenuItemClickListener(this);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        mainInteractor.getMessageDataBase().setAllMessagesRead(chat);

        //TODO SEND TO SERVER MESSAGES READ



        //create fragment
        ChatFragment chatFragment=ChatFragment.getInstance(this); //ChatPresenter
        replaceContainer(chatFragment);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, HttpsBackgroundService.class);
        bindService(intent, mConnection, getBaseContext().BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            unbindService(mConnection);
            isBound=false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_picture:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                return true;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,android.content.Intent data)
{
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK && requestCode == 1)
    {
        Date a= new Date();
        String timestamp = String.valueOf(a.getTime());
        android.net.Uri selectedImageUri = data.getData();
        String sourcePath = getRealPathFromURI(selectedImageUri);
        if(sourcePath!=null){
        Log.e("ChatActivity", selectedImageUri.getPath());
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data2 = Environment.getDataDirectory();
            if (sd.canWrite()) {

                String destinationImagePath = Environment.getExternalStorageDirectory() + "/Marssenger/Pictures/" + timestamp.hashCode() + ".jpg";
                Log.e("ChatActivity", destinationImagePath);
                File source = new File(data2, sourcePath);
                File destination = new File(sd, destinationImagePath);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }

        } catch (Exception e) {}
    }}}
    private void test(CharSequence charSequence){
        Toast.makeText(getApplicationContext(),charSequence,Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void replaceContainer(Fragment fragment) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.chat_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public String getRealPathFromURI(Uri uri) {
        try{

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);


            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA) + 1;
            Log.e("ChatActivity","idx:"+idx);//TODO
            return cursor.getString(idx);
        }catch(Exception e){
            e.printStackTrace();
        }
       return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));}

    //ChatView methods
    @Override
    public void chatButtonSendPressed(String message) {
        mainInteractor.getMessageDataBase().createMessage(message,1,chat.getId()-1,1,0);
        ArrayList<Message> messages = mainInteractor.getMessageDataBase().getAllMessageFromChat(chat);
        cChatListAdapter.addMessage(messages.get(messages.size()-1));
                                                       //TODO generate messageId
        try {
            if(mService == null){
                Log.e ("Help me!", "No Connection");
            }else {
                mService.sendMessage(this.chat.getReceiver(), message, String.valueOf((int)(Math.random()*1000)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getBottomPosition() {
        return cChatListAdapter.getItemCount()-1;
    }

    @Override
    public CChatListAdapter getChatAdapter() {
        cChatListAdapter=new CChatListAdapter(mainInteractor.getMessageDataBase(),this.chat, getApplicationContext());
        return cChatListAdapter;
    }

    @Override
    public void updateLayout() {
        cChatListAdapter.updateLayout();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.action_search:

                return true;
            default:break;
        }
        return onOptionsItemSelected(menuItem);
    }
}
