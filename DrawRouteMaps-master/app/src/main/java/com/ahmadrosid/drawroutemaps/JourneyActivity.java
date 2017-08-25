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
 * Created by pat95 on 20.08.2017.
 */

public class JourneyActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private ProgressDialog loading;
    private Spinner journeySpinner;
    public ArrayAdapter arrayAdapter;
    private Button buttonMap;
    private Button buttonStats;
    private String name;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        initialiseSpinner();
        initialiseButtons();

        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);

        getIntentActivity();
        fetchJsonData();
    }

    public void getIntentActivity() {
        Intent intent = getIntent();

        name = intent.getStringExtra("USER_NAME");
        url = Config.JOURNEY_URL + name;
    }

    public void fetchJsonData() {

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();


                String id = "";
                String name = "";
                String dateJourney = "";

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Config.JSON_ARRAY);
                    List<Journey> journey = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject collegeData = jsonArray.getJSONObject(i);
                        id = collegeData.getString(Config.KEY_ID_JOURNEY);
                        name = collegeData.getString(Config.KEY_USER_NAME_JOURNEY);
                        dateJourney = collegeData.getString(Config.KEY_DATA_OF_JOURNEY);
                        journey.add(new Journey(id,name,dateJourney));
                    }

                    arrayAdapter= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,journey);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    journeySpinner.setAdapter(arrayAdapter);
                    journeySpinner.setOnItemSelectedListener(JourneyActivity.this);

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
        journeySpinner = (Spinner)findViewById(R.id.spinner2);
        journeySpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

    }

    public void initialiseButtons() {
        buttonMap = (Button) findViewById(R.id.btn_map);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap(v);
            }
        });


        buttonStats = (Button) findViewById(R.id.btn_stat);
        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStat(v);
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    public void showMap(View view)
    {
        Journey journey = (Journey) journeySpinner.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        intent.putExtra("ID_JOURNEY",journey.getId());

        startActivity(intent);
    }


    public void showStat(View view)
    {
        Journey journey = (Journey) journeySpinner.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);

        intent.putExtra("ID_JOURNEY", journey.getId());

        startActivity(intent);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}