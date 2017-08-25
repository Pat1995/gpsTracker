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
 * Created by pat95 on 17.08.2017.
 */

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private ProgressDialog loading;
    private Spinner spinner;
    public ArrayAdapter arrayAdapter;
    private Button buttonAboutApp;
    private Button buttonJourney;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseSpinner();
        initialiseButtons();

        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);

        url = Config.NAME_URL;

        fetchJsonData();
    }

    public void fetchJsonData() {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();


                String name = "";
                String email = "";

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Config.JSON_ARRAY);
                    List<UserInfo> userInfoList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject collegeData = jsonArray.getJSONObject(i);
                        name = collegeData.getString(Config.KEY_USER_NAME);
                        email = collegeData.getString(Config.KEY_USER_EMAIL);
                        userInfoList.add(new UserInfo(name, email));
                    }

                    arrayAdapter= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,userInfoList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner.setAdapter(arrayAdapter);
                    spinner.setOnItemSelectedListener(MainActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void initialiseSpinner() {
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        spinner.setOnItemSelectedListener(null);
    }

    public void initialiseButtons() {
        buttonAboutApp = (Button) findViewById(R.id.btn_about);
        buttonAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppInfo(v);
            }
        });

        buttonJourney = (Button) findViewById(R.id.btn_journey);
        buttonJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectJourney(v);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }


    public void selectJourney(View view)
    {
        UserInfo userInfo = (UserInfo) spinner.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), JourneyActivity.class);

        intent.putExtra("USER_NAME", userInfo.getName());

        startActivity(intent);
    }

    public void showUserInfo(View view)
    {
        UserInfo userInfo = (UserInfo) spinner.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);

        intent.putExtra("USER_NAME", userInfo.getName());
        intent.putExtra("USER_EMAIL", userInfo.getEmail());

        startActivity(intent);
    }

    public void showAppInfo(View view)
    {
        Intent intent = new Intent(getApplicationContext(), AppInfoActivity.class);

        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}