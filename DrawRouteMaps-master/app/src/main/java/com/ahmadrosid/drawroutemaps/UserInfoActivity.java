package com.ahmadrosid.drawroutemaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by pat95 on 21.08.2017.
 */

public class UserInfoActivity extends AppCompatActivity {

    private TextView textName;
    private TextView textEmail;
    private String name;
    private String email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setTextInfo();
    }

    private void setTextInfo() {
        Intent intent = getIntent();

        name = intent.getStringExtra("USER_NAME");
        email = intent.getStringExtra("USER_EMAIL");
        textName = (TextView) findViewById(R.id.textView1);
        textEmail = (TextView) findViewById(R.id.textView3);
        textName.setText(name);
        textEmail.setText(email);
    }

}
