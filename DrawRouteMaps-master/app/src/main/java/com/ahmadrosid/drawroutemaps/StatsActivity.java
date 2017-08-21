package com.ahmadrosid.drawroutemaps;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by pat95 on 20.08.2017.
 */

public class StatsActivity extends AppCompatActivity {

    private String idJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Intent intent = getIntent();

        idJourney = intent.getStringExtra("ID_JOURNEY");

        String url = Config.DATA_URL + idJourney;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

//                float[] lat = new float[latArray.size()];
//                float[] lng = new float[lonArray.size()];
//
//                for (int i = 0; i < latArray.size(); i++) {
//                    lat[i] = Float.parseFloat(latArray.get(i));
//                }
//
//                for (int i = 0; i < lonArray.size(); i++) {
//                    lng[i] = Float.parseFloat(lonArray.get(i));
//                }

                calculateData(latArray,lonArray,timeArray);

                //Toast.makeText(getApplicationContext(), "LAT " + lat[0] +", LNG " + lng[0], Toast.LENGTH_SHORT).show();

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

    private void calculateData(ArrayList<String> latArray, ArrayList<String> lonArray, ArrayList<String> timeArray) {

        float[] lat = new float[latArray.size()];
        float[] lng = new float[lonArray.size()];
        float[] distance = new float[lonArray.size() - 1];
        long[] timeDifference = new long[timeArray.size()-1];
        float totalDistance = 0;
        Date startDate = null;
        Date endDate = null;

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = outputFormat.parse(timeArray.get(0));
            endDate = outputFormat.parse(timeArray.get(timeArray.size()-1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffInMs = endDate.getTime() - startDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        long hours = diffInSec / 3600;
        long minutes = (diffInSec % 3600) / 60;
        long seconds = diffInSec % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        Date[] dateArray = new Date[timeArray.size()];
        for (int i = 0; i < timeArray.size(); i++) {
            try {
                dateArray[i] = outputFormat.parse(timeArray.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < timeArray.size() -1; i++) {
            long diffInMs1 = dateArray[i+1].getTime() - dateArray[i].getTime();
            long diffInSec1 = TimeUnit.MILLISECONDS.toSeconds(diffInMs1);

            long hours1 = diffInSec1 / 3600;
            long minutes1 = (diffInSec1 % 3600) / 60;
            long seconds1 = diffInSec1 % 60;

            timeDifference[i] = diffInSec1;
            //Toast.makeText(getApplicationContext(), "D1 " + timeDifference[i], Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(getApplicationContext(), "D1 " + timeString, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < latArray.size(); i++) {
            lat[i] = Float.parseFloat(latArray.get(i));
            lng[i] = Float.parseFloat(lonArray.get(i));
        }

        for (int i = 0; i < latArray.size() - 1;i++) {
            //distance[i] = meterDistanceBetweenPoints(lat[i], lng[i],lat[i+1], lng[i+1]);
            distance[i] = getDistancBetweenTwoPoints(lat[i], lng[i],lat[i+1], lng[i+1]);
            totalDistance += distance[i];
            //Toast.makeText(getApplicationContext(), "D1 " + distance[i], Toast.LENGTH_SHORT).show();
        }
        float srednia = totalDistance/diffInSec;
        float sr = (float) (srednia * 3.6);

        Toast.makeText(getApplicationContext(), "D1 " + sr, Toast.LENGTH_SHORT).show();

        // Toast.makeText(getApplicationContext(), "D1 " + String.format("%.2f", totalDistance/1000), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "LAT " + lat[0] +", LNG " + lng[0], Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "D1 " + distance[0] +", D2 " + distance[1], Toast.LENGTH_SHORT).show();
    }

    private float getDistancBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

        return distance[0];
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        float t1 = (float) ((float)Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2));
        float t2 = (float) ((float)Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2));
        float t3 = (float) ((float)Math.sin(a1)*Math.sin(b1));
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
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

