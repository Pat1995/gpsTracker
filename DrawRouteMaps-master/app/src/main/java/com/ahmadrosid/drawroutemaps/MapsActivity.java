package com.ahmadrosid.drawroutemaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog loading;
    private String idJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();

        idJourney = intent.getStringExtra("ID_JOURNEY");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
//
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);

        String url = Config.DATA_URL + idJourney;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();


                String lat1 = "";
                String lon = "";
                String time = "";
                ArrayList<String> latArray = new ArrayList<String>();
                ArrayList<String> lonArray = new ArrayList<String>();
                ArrayList<String> timeArray = new ArrayList<String>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject collegeData = result.getJSONObject(i);
                        lat1 = collegeData.getString(Config.KEY_NAME);
                        lon = collegeData.getString(Config.KEY_ADDRESS);
                        time = collegeData.getString(Config.KEY_VC);
                        latArray.add(lat1);
                        lonArray.add(lon);
                        timeArray.add(time);
                        // textViewResult.setText("Lat:\t"+lat+"\nLng:\t" +lon+ "\nTime:\t"+ time);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                float[] lat = new float[latArray.size()];
                float[] lng = new float[lonArray.size()];

                for (int i = 0; i < latArray.size(); i++) {
                    lat[i] = Float.parseFloat(latArray.get(i));
                }

                for (int i = 0; i < lonArray.size(); i++) {
                    lng[i] = Float.parseFloat(lonArray.get(i));
                }

                if (lat[0] != 0 && lng[0] != 0)
                {
                    LatLng origin1 = new LatLng(lat[0], lng[0]);
                    LatLng destination1 = new LatLng(lat[latArray.size() - 1], lng[lonArray.size() - 1]);
                    DrawRouteMaps.getInstance(getApplicationContext())
                            .draw(origin1, destination1, mMap);
                    DrawMarker.getInstance(getApplicationContext()).draw(mMap, origin1, R.drawable.marker_a, "Origin Location");
                    DrawMarker.getInstance(getApplicationContext()).draw(mMap, destination1, R.drawable.marker_b, "Destination Location");
                }

                for (int i = 0; i < lonArray.size()-1; i++) {
                    LatLng origin = new LatLng(lat[i], lng[i]);
                    LatLng destination = new LatLng(lat[i + 1], lng[i + 1]);
                    DrawRouteMaps.getInstance(getApplicationContext())
                            .draw(origin, destination, mMap);
                    // DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.marker_a, "Origin Location");
                    // DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.marker_b, "Destination Location");

                    LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(origin)
                            .include(destination).build();
                    Point displaySize = new Point();
                    getWindowManager().getDefaultDisplay().getSize(displaySize);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
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

    private void getData() {

        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);

        String url = Config.DATA_URL;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON( response);
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

    private void showJSON( String response){
        String lat="";
        String lon="";
        String time = "";
        ArrayList<String> latArray = new ArrayList<String>();
        ArrayList<String> lonArray = new ArrayList<String>();
        ArrayList<String> timeArray = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            for (int i = 0; i < result.length(); i++)
            {
                JSONObject collegeData = result.getJSONObject(i);
                lat = collegeData.getString(Config.KEY_NAME);
                lon = collegeData.getString(Config.KEY_ADDRESS);
                time = collegeData.getString(Config.KEY_VC);
                latArray.add(lat);
                lonArray.add(lon);
                timeArray.add(time);
                // textViewResult.setText("Lat:\t"+lat+"\nLng:\t" +lon+ "\nTime:\t"+ time);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
