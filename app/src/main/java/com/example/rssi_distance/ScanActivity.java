package com.example.rssi_distance;

import static com.example.rssi_distance.helpers.Parameters.tracked_beacon_address;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rssi_distance.helpers.DataFileWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends AppCompatActivity {

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private final BluetoothLeScanner bluetoothLeScanner = adapter.getBluetoothLeScanner();

    private DataFileWriter dataFileWriter;

    private final List<String[]> rssiData = new ArrayList<>();
    private final String[] headerScan = {"distance", "rssi"};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        infiniteScan();

        TextView remaining_time = (TextView) findViewById(R.id.remaining_time);
        TextView scan_distance = (TextView) findViewById(R.id.scan_distance_value);
        Button stop_scan = (Button) findViewById(R.id.stop_scan);

        // Updating the UI
        int duration = Integer.parseInt(getIntent().getStringExtra("duration"));
        int distance = Integer.parseInt(getIntent().getStringExtra("distance"));

        remaining_time.setText(String.valueOf(duration));
        scan_distance.setText(String.valueOf(distance));

        dataFileWriter = new DataFileWriter();

       // Create a countdown timer and display on remaining_time
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            int count = duration * 60;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @SuppressLint({"MissingPermission", "SetTextI18n"})
                    @Override
                    public void run() {
                        if (count == 0){
                            t.cancel();
                            bluetoothLeScanner.stopScan(scanCallback);
                            WriteInFile("distance@"+distance+"m.csv");
                            stop_scan.setText("Scan finished");
                            stop_scan.setEnabled(false);
                        }
                        remaining_time.setText(String.valueOf(count));
                        count--;
                    }
                });
            }
        }, 0, 1000);

        stop_scan.setOnClickListener(v -> {
            WriteInFile("distance@"+distance+"m.csv");
            stop_scan.setText("Scan finished");
            stop_scan.setEnabled(false);
        });
    }

    private void WriteInFile(String filename){
        dataFileWriter.writeToFiles(filename, "RSSI_Analysis", this.headerScan, this.rssiData);
    }

    private List<ScanFilter> getFilters(){

        ScanFilter rssiFilter = new ScanFilter.Builder()
                .setDeviceAddress(tracked_beacon_address)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(rssiFilter);
        return filters;
    }

    @SuppressLint("MissingPermission")
    private void infiniteScan(){
        Log.d("ble_test", "Infinite scanning started");
        bluetoothLeScanner.startScan(getFilters(), getScanSettings(false), scanCallback);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d("ble_test", "Signal found: "+ result.getRssi());
            TextView signals_found = (TextView) findViewById(R.id.signals);
            TextView rssi = (TextView) findViewById(R.id.rssi);

            int number_of_signal_found = Integer.parseInt(String.valueOf(signals_found.getText()));
            signals_found.setText(String.valueOf(number_of_signal_found+1));
            rssi.setText(String.valueOf(result.getRssi()));

            rssiData.add(new String[]{getIntent().getStringExtra("distance"), String.valueOf(result.getRssi())});

        }

    };

    public static ScanSettings getScanSettings(Boolean legacy){
        return new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setLegacy(legacy)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();
    }
}