package com.ahmadrosid.drawroutemaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by pat95 on 20.08.2017.
 */

public class AppInfoActivity extends AppCompatActivity {

    private TextView aboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        aboutText = (TextView)findViewById(R.id.textView1);

    }
}
