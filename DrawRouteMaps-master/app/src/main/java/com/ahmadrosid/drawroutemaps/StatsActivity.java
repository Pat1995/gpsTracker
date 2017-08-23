package com.ahmadrosid.drawroutemaps;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

    private TextView totalDistanceText;
    private TextView totalTime;
    private TextView avgSpeed;
    private TextView maxSpeedText;
    private GraphView graphView;
    LineGraphSeries<DataPoint> series;
    private GraphView graphViewTime;
    LineGraphSeries<DataPoint> seriesTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Intent intent = getIntent();
        totalDistanceText = (TextView) findViewById(R.id.textView1);
        totalTime = (TextView) findViewById(R.id.textView3);
        avgSpeed = (TextView) findViewById(R.id.textView5);
        maxSpeedText = (TextView) findViewById(R.id.textView7);

        graphView = (GraphView) findViewById(R.id.graphSpeed);
        graphView.setTitle("Speed graphs");
        graphView.setTitleColor(000000);
        graphView.getViewport().setScrollable(true);

        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        graphView.getGridLabelRenderer().setTextSize(17f);
        graphView.getGridLabelRenderer().reloadStyles();
        gridLabel.setHorizontalAxisTitle("Distance [km]");

        GridLabelRenderer gridLabel2 = graphView.getGridLabelRenderer();
        gridLabel.setVerticalAxisTitle("Speed [km/h]");

//        graphView = (GraphView) findViewById(R.id.graphSpeedTime);

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

        float[] speedArray = new float[distance.length];
        float maxSpeed = 0;
        for (int i =0; i <distance.length;i++) {
            float speed = distance[i]/timeDifference[i];
            speedArray[i] = (float) (speed * 3.6);
            if (maxSpeed < speedArray[i])
                maxSpeed = speedArray[i];
            // Toast.makeText(getApplicationContext(), "D1 " + speedArray[i], Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(getApplicationContext(), "D2 " + maxSpeed, Toast.LENGTH_SHORT).show();
        float avgSpeedMS = totalDistance/diffInSec;
        float averageSpeedKH = (float) (avgSpeedMS * 3.6);
        totalDistance = totalDistance/1000;

        String totalDistanceString = String.format("%.2f", totalDistance);
        String totalTimeString = timeString;
        String avgSpeedString = String.format("%.2f", averageSpeedKH);
        String maxSpeedTextString = String.format("%.2f", maxSpeed);

        totalDistanceString=totalDistanceString.replaceAll(",",".");
        avgSpeedString=avgSpeedString.replaceAll(",",".");
        maxSpeedTextString=maxSpeedTextString.replaceAll(",",".");

        totalDistanceText.setText(totalDistanceString + " km");
        totalTime.setText(totalTimeString);
        avgSpeed.setText(avgSpeedString + " km/h");
        maxSpeedText.setText(maxSpeedTextString+ " km/h");

        float[] distanceXAxis = new float[distance.length];
        distanceXAxis[0] = distance[0]/1000;
        for (int i = 1; i < distance.length; i++) {
            distanceXAxis[i] = distance[i]/1000 + distanceXAxis[i-1];
        }


        series = new LineGraphSeries<DataPoint>();
        for (int i = 0; i < distance.length; i++) {
            series.appendData(new DataPoint(distanceXAxis[i], speedArray[i]), true, distance.length, true);
        }

        graphView.addSeries(series);

        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(distanceXAxis[distance.length -1] + 0.5);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(maxSpeed + 10);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);



//
//        float[] timeXAxis = new float[timeDifference.length];
//        timeXAxis[0] = timeDifference[0];
//        for (int i = 1; i < timeDifference.length; i++) {
//            timeXAxis[i] = timeDifference[i] + timeXAxis[i-1];
//            Toast.makeText(getApplicationContext(), "A " + timeXAxis[i], Toast.LENGTH_SHORT).show();
//        }
//
//
//        seriesTime = new LineGraphSeries<DataPoint>();
//        for (int i = 0; i < distance.length; i++) {
//            series.appendData(new DataPoint(timeXAxis[i], speedArray[i]), true, timeDifference.length, true);
//        }
//
//        graphViewTime.addSeries(seriesTime);
//
//        graphViewTime.getViewport().setMinX(0);
//        graphViewTime.getViewport().setMaxX(diffInSec/60);
//        graphViewTime.getViewport().setMinY(0);
//        graphViewTime.getViewport().setMaxY(maxSpeed + 10);
//
//        graphViewTime.getViewport().setYAxisBoundsManual(true);
//        graphViewTime.getViewport().setXAxisBoundsManual(true);

        //Toast.makeText(getApplicationContext(), "D1 " + sr, Toast.LENGTH_SHORT).show();

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

