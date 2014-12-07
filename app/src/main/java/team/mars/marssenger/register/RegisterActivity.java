package team.mars.marssenger.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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


public class RegisterActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "1234";
    private static final String TWITTER_SECRET = "4567";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_register);
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


    }
}
