package com.example.rssi_distance;

import static com.example.rssi_distance.helpers.Parameters.EDDYSTONE_UID_UUID;
import static com.example.rssi_distance.helpers.Parameters.LOG_TAG;
import static com.example.rssi_distance.helpers.Parameters.beaconsToTrack;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends AppCompatActivity {

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private final BluetoothLeScanner bluetoothLeScanner = adapter.getBluetoothLeScanner();

    private DataFileWriter dataFileWriter;

    private final List<String[]> rssiData = new ArrayList<>();
    private final String[] headerScan = {"distance", "rssi"};
    public TextView signals_found;
    public TextView rssi;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        infiniteScan();
        TextView remaining_time = findViewById(R.id.remaining_time);
        TextView scan_distance = findViewById(R.id.scan_distance_value);
        Button stop_scan = findViewById(R.id.stop_scan);

         signals_found = findViewById(R.id.signals);
         rssi = findViewById(R.id.rssi);

        // Updating the UI
        int duration = Integer.parseInt(getIntent().getStringExtra("duration"));
        int distance = Integer.parseInt(getIntent().getStringExtra("distance"));

        remaining_time.setText(String.valueOf(duration));
        scan_distance.setText(String.valueOf(distance));

        dataFileWriter = new DataFileWriter();

       // Create a countdown timer and display on remaining_time
        Timer t = new Timer();
        t.schedule(new TimerTask() {
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

    /**
     * @return This function is used to filter the scan results to only show Eddystone beacons
     */
    private List<ScanFilter> getEddystoneFilters(){
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(EDDYSTONE_UID_UUID)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter);
        return filters;
    }

    @SuppressLint("MissingPermission")
    private void infiniteScan(){
        Log.d("ble_test", "Infinite scanning started");


        bluetoothLeScanner.startScan(getEddystoneFilters(), getScanSettings(false), scanCallback);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d("ble_test", "Signal found: "+ result.getRssi());
            Log.d(LOG_TAG, "Scan result: " + result.toString());

            byte[] serviceData = result.getScanRecord().getServiceData(EDDYSTONE_UID_UUID);
            if (serviceData != null && serviceData.length > 18) {
                // Convert the byte array range (2, 18) to a string
                byte[] namespaceBytes = Arrays.copyOfRange(serviceData, 2, 12);
                byte[] instanceBytes = Arrays.copyOfRange(serviceData, 12, 18);
                String namespace = bytesToHex(namespaceBytes);
                String instance = bytesToHex(instanceBytes);
                EddystoneBeacon foundBeacon = new EddystoneBeacon(namespace, instance);
                for (EddystoneBeacon beacon : beaconsToTrack) {
                    if (beacon.equals(foundBeacon)) {
                        Log.d(LOG_TAG, "Beacon found: " + beacon.getNamespace() + " " + beacon.getInstance());

                        int number_of_signal_found = Integer.parseInt(String.valueOf(signals_found.getText()));
                        signals_found.setText(String.valueOf(number_of_signal_found+1));
                        rssi.setText(String.valueOf(result.getRssi()));

                        rssiData.add(new String[]{getIntent().getStringExtra("distance"), String.valueOf(result.getRssi())});
                    }
                }
            }


        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Handle batch scan results
            for (ScanResult result : results) {
                Log.d(LOG_TAG, "Batch scan result: " + result.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Handle scan failure
            Log.e(LOG_TAG, "Scan failed with error: " + errorCode);
        }

    };

    public static ScanSettings getScanSettings(Boolean legacy){
        return new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setLegacy(true)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}