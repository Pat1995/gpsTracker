package com.ahmadrosid.drawroutemaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pat95 on 21.08.2017.
 */

public class UserInfoActivity extends AppCompatActivity {

    private TextView textName;
    private TextView textEmail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();

        String name = intent.getStringExtra("USER_NAME");
        String email = intent.getStringExtra("USER_EMAIL");
        Toast.makeText(getApplicationContext(), "NAME " + name + "EMAIL " + email, Toast.LENGTH_SHORT).show();
        textName = (TextView) findViewById(R.id.textView1);
        textEmail = (TextView) findViewById(R.id.textView3);
        textName.setText(name);
        textEmail.setText(email);
    }

}
