package com.example.rssi_distance;

import static com.example.rssi_distance.helpers.Parameters.tracked_beacon_address;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AtomicInteger distance = new AtomicInteger();
        AtomicInteger interval = new AtomicInteger();
        AtomicInteger duration = new AtomicInteger();

        Spinner spinner_distance = (Spinner) findViewById(R.id.distance_spinner);
        Spinner spinner_interval = (Spinner) findViewById(R.id.interval);
        Spinner spinner_duration = (Spinner) findViewById(R.id.duration);
        Button scan = (Button) findViewById(R.id.start_scan);
        EditText beacon_address = (EditText) findViewById(R.id.beacon_address);

        spinner_distance.setAdapter(ArrayAdapter.createFromResource(this, R.array.distance, android.R.layout.simple_spinner_dropdown_item));
        spinner_interval.setAdapter(ArrayAdapter.createFromResource(this, R.array.refresh_interval, android.R.layout.simple_spinner_dropdown_item));
        spinner_duration.setAdapter(ArrayAdapter.createFromResource(this, R.array.scan_duration, android.R.layout.simple_spinner_dropdown_item));


        beacon_address.setText(tracked_beacon_address);

        // add a listener to the button
        scan.setOnClickListener(v -> {
            // get the values from the spinners
            String distanceValue = spinner_distance.getSelectedItem().toString();
            String intervalValue = spinner_interval.getSelectedItem().toString();
            String durationValue = spinner_duration.getSelectedItem().toString();

            String[] distanceValueSplit = distanceValue.split(" ");
            distance.set(Integer.parseInt(distanceValueSplit[0]));

            String[] intervalValueSplit = intervalValue.split(" ");
            if (Objects.equals(intervalValueSplit[1], "s")){
                interval.set(Integer.parseInt(intervalValueSplit[0]) * 1000);
            }
            else{
                interval.set(Integer.parseInt(intervalValueSplit[0]));
            }

            String[] durationValueSplit = durationValue.split(" ");
            duration.set(Integer.parseInt(durationValueSplit[0]));


            // create a new intent
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);

            // add the values to the intent
            intent.putExtra("distance", String.valueOf(distance));
            intent.putExtra("interval", String.valueOf(interval));
            intent.putExtra("duration", String.valueOf(duration));

            // start the activity
            startActivity(intent);
        });


    }
}