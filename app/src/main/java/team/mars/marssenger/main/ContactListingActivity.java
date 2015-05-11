package team.mars.marssenger.main;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import team.mars.marssenger.R;

public class ContactListingActivity extends ListActivity {
    private final int PICK_CONTACT = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_listing);

        Cursor mCursor = this.getContentResolver().query(Contacts.People.CONTENT_URI, null, null, null, null);

        if(mCursor.moveToFirst()){

        }

        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.contacts_item, mCursor, new String[]{Contacts.People.NAME, Contacts.People.NUMBER},  new int[] {R.id.text1, R.id.text2});
        setListAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_listing, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
