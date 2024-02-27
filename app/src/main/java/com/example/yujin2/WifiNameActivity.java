package com.example.yujin2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.widget.TextView;

import java.util.List;

public class WifiNameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiname);
        requestPermission(this);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List<ScanResult> result = wifiManager.getScanResults();
        int rssi = wifiManager.getConnectionInfo().getRssi();
        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            TextView textView = findViewById(R.id.showpanel);
            for (ScanResult scanResult : result) {
                textView.append("WIFI_RESULT" + "SSID : " + scanResult.SSID + " BSSID : " + scanResult.BSSID + "\n");
            }
            textView.append("RSSI: " + rssi + "\n");
        });

        Button btnToHome = findViewById(R.id.tohome);
        btnToHome.setOnClickListener(v -> {
            Intent intent = new Intent(WifiNameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void requestPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
    }
}