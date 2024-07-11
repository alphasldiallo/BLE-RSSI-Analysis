package com.example.rssi_distance.helpers;

import android.os.ParcelUuid;

import com.example.rssi_distance.EddystoneBeacon;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameters {
    public static final String LOG_TAG = "test_ble";
    public static String tracked_beacon_address = "E2:81:45:5F:AA:25";
    public static final ParcelUuid EDDYSTONE_UID_UUID = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");
    public static String NAMESPACE = "FFFFFFFFFFFFFFFFF0D3";
    public static String INSTANCE = "FFFFFFFFF0D3";
    public static String NAMESPACE2 = "FFFFFFFFFFFFFFFFF1D3";
    public static String INSTANCE2 = "FFFFFFFFF1D3";

    public static ArrayList<EddystoneBeacon> beaconsToTrack = new ArrayList<>(Arrays.asList(new EddystoneBeacon(NAMESPACE, INSTANCE), new EddystoneBeacon(NAMESPACE2, INSTANCE2)));
}