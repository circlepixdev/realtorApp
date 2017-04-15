package com.circlepix.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.circlepix.android.helpers.Globals;
import com.circlepix.android.helpers.LoginHelper;

public class AccountActivity extends Activity {

    private Button mLogoutButton;
    private EditText mUsernameField;
    private EditText mPasswordField;

    public static final String PREFS_NAME = "CirclePixPrefsFile";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account);

        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mUsernameField = (EditText) findViewById(R.id.account_username);
        mPasswordField = (EditText) findViewById(R.id.account_password);

        if (Globals.USERNAME != null) {
            mUsernameField.setText(Globals.USERNAME);
        }
        if (Globals.PASSWORD != null) {
            mPasswordField.setText(Globals.PASSWORD);
        }

        mLogoutButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                LoginHelper.handleLogout(getSharedPreferences(PREFS_NAME, 0), AccountActivity.this.getApplicationContext());

                Intent i = new Intent(AccountActivity.this, LoginActivity.class);
                startActivityForResult(i, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
    }

    protected void onStop() {
        setResult(2);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        setResult(2);
        super.onDestroy();
    }
}