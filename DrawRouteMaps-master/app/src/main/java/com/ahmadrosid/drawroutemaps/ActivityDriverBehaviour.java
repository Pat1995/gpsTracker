package com.ahmadrosid.drawroutemaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by pat95 on 24.08.2017.
 */

public class ActivityDriverBehaviour extends AppCompatActivity {

    private TextView driverAssesment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_behaviour);

        Intent intent = getIntent();

        String driverAsses = intent.getStringExtra("DRIVER_ASSESMENT");
        Toast.makeText(getApplicationContext(), "NAME " + driverAsses , Toast.LENGTH_SHORT).show();
        driverAssesment = (TextView) findViewById(R.id.textView1);


        styleDriving(driverAsses);

    }

    public void styleDriving(String style) {
        int styleDriving = Integer.parseInt(style);

        switch (styleDriving) {
            case 1: slowDriving(); break;
            case 2: properDriving(); break;
            case 3: fastDriving(); break;
            case 4: dangeorusDriving(); break;
            default: properDriving(); break;

        }
    }

    public void slowDriving() {
        String description = "Your driving style is calm and relaxed. Your goal of journey is to reach the destination. You do not care about high speed. You like listening to nice tunes in the car while driving.";
        driverAssesment.setText(description);

    }

    public void properDriving() {
        String description = "Your driving style is average. You look out for the well being of yours passengers as well as yourself. You do not break a few traffic laws in order to get somewhere more quickly.";
        driverAssesment.setText(description);

    }

    public void fastDriving() {
        String description = "Your driving style is aggressive and dynamic. During journey you have reached high speed. You like feeling acceleration and the curves in the road. You often break traffic laws in order to get somewhere more quickly.";
        driverAssesment.setText(description);

    }

    public void dangeorusDriving() {
        String description = "Your driving style is really dangerous. You do not look out for the well being of yours passengers as well as yourself. You often break traffic laws and the police is looking for you.";
        driverAssesment.setText(description);

    }


}
