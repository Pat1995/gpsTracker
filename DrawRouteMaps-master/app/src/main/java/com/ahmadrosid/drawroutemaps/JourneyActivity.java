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
 * Created by pat95 on 20.08.2017.
 */

public class JourneyActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private ProgressDialog loading;
    private Spinner spinner2;
    public ArrayAdapter arrayAdapter;
    private Button buttonMap;
    private Button buttonStats;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner2.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

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

        Intent intent = getIntent();

        String name = intent.getStringExtra("USER_NAME");

        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);
       // Toast.makeText(getApplicationContext(), ", name " + name, Toast.LENGTH_SHORT).show();
        //String url = Config.JOURNEY_URL;
        String url = Config.JOURNEY_URL + name;
        Toast.makeText(getApplicationContext(), ", URL " + url, Toast.LENGTH_SHORT).show();

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
                        // textViewResult.setText("Lat:\t"+lat+"\nLng:\t" +lon+ "\nTime:\t"+ time);
                    }

                    arrayAdapter= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,journey);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(arrayAdapter);
                    Toast.makeText(getApplicationContext(), "ID " + id +", name " + name + "DATE " + dateJourney, Toast.LENGTH_SHORT).show();
                    //spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) getApplicationContext());
                    spinner2.setOnItemSelectedListener(JourneyActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Journey journey = (Journey) spinner2.getSelectedItem();

        Toast.makeText(getApplicationContext(), "ID " + journey.getId() +", name " + journey.getName() + "DATE " + journey.getDateJourney(), Toast.LENGTH_SHORT).show();
        //mStateSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, country.getStates()));
//        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//
//        intent.putExtra("ID_JOURNEY",journey.getId());
//        startActivity(intent);
    }

    public void showMap(View view)
    {
        Journey journey = (Journey) spinner2.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        intent.putExtra("ID_JOURNEY",journey.getId());
        startActivity(intent);
    }


    public void showStat(View view)
    {
        Toast.makeText(this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
        Journey journey = (Journey) spinner2.getSelectedItem();
        Intent intent = new Intent(getApplicationContext(), StatsActivity.class);

        intent.putExtra("ID_JOURNEY",journey.getId());
        startActivity(intent);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(this, "You Must Buy-In To Play", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}