package team.mars.marssenger.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import team.mars.marssenger.R;

/**
 * Created by Kern on 07.12.2014.
 */


public class RegisterActivity extends ActionBarActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private final String TWITTER_KEY = getResources().getString(R.string.twitter_key);
    private final String TWITTER_SECRET = getResources().getString(R.string.twitter_secret);

    //layout-attr
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.title_activity_register);
        } else {
            test("toolbar reg null");
        }

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        Fabric.with(this, new Twitter(authConfig));

        Button skip = (Button)findViewById(R.id.btn_skip);
        final Intent resultData = new Intent();
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK, resultData);
                resultData.putExtra("number", "490001011000");
                finish();
            }
        });
        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);

        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                setResult(Activity.RESULT_OK, resultData);
                resultData.putExtra("AuthToken", session.getAuthToken().toString());
                resultData.putExtra("number", phoneNumber);
                finish();
            }

            @Override
            public void failure(DigitsException exception) {
                setResult(Activity.RESULT_CANCELED, resultData);
                finish();
            }
        });

    } //end of OnCreate

    private void test(CharSequence charSequence){
        Toast.makeText(this,charSequence,Toast.LENGTH_SHORT).show();
    }
}
