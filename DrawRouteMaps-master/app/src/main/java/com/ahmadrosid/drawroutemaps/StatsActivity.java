
package com.ahmadrosid.drawroutemaps;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    private int driverAssesment;
    private String url;
    private Button buttonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initialiseGraphicElements();
        setGraphViewSetting();
        getIntentActivity();
        fetchJsonData();
//        graphView = (GraphView) findViewById(R.id.graphSpeedTime);

    }

    public void fetchJsonData() {
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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                calculateData(latArray,lonArray,timeArray);
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

    public void getIntentActivity() {
        Intent intent = getIntent();
        idJourney = intent.getStringExtra("ID_JOURNEY");

        url = Config.DATA_URL + idJourney;
    }

    public void initialiseGraphicElements() {
        totalDistanceText = (TextView) findViewById(R.id.textView1);
        totalTime = (TextView) findViewById(R.id.textView3);
        avgSpeed = (TextView) findViewById(R.id.textView5);
        maxSpeedText = (TextView) findViewById(R.id.textView7);
        buttonMap = (Button) findViewById(R.id.btn_analysys);

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBehaviour(v);
            }
        });
    }

    public void setGraphViewSetting() {

        graphView = (GraphView) findViewById(R.id.graphSpeed);

        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        graphView.getGridLabelRenderer().setTextSize(17f);
        graphView.getGridLabelRenderer().reloadStyles();
        graphView.setTitleTextSize(30f);
        graphView.setTitle("Journey speed graph");
        graphView.getViewport().setScrollable(true);
        gridLabel.setHorizontalAxisTitle("Distance [km]");

        GridLabelRenderer gridLabel2 = graphView.getGridLabelRenderer();
        gridLabel.setVerticalAxisTitle("Speed [km/h]");
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

            timeDifference[i] = diffInSec1;
        }

        for (int i = 0; i < latArray.size(); i++) {
            lat[i] = Float.parseFloat(latArray.get(i));
            lng[i] = Float.parseFloat(lonArray.get(i));
        }

        for (int i = 0; i < latArray.size() - 1;i++) {
            distance[i] = getDistancBetweenTwoPoints(lat[i], lng[i],lat[i+1], lng[i+1]);
            totalDistance += distance[i];
        }

        float[] speedArray = new float[distance.length];
        float maxSpeed = 0;
        for (int i =0; i <distance.length;i++) {
            float speed = distance[i]/timeDifference[i];
            speedArray[i] = (float) (speed * 3.6);
            if (maxSpeed < speedArray[i])
                maxSpeed = speedArray[i];
        }

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

        if (maxSpeed > 120)
            driverAssesment = 4;
        else if (maxSpeed > 90)
            driverAssesment = 3;
        else if (maxSpeed > 70)
            driverAssesment = 2;
        else
            driverAssesment = 1;
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

    public void showBehaviour(View view)
    {
        Intent intent = new Intent(getApplicationContext(), ActivityDriverBehaviour.class);

        intent.putExtra("DRIVER_ASSESMENT", String.valueOf(driverAssesment));

        startActivity(intent);
    }

}