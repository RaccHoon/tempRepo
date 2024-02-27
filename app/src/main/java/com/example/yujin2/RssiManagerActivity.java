package com.example.yujin2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RssiManagerActivity extends AppCompatActivity {

    private TextView scanResultText;
    private WifiManager wifiManager;
    private EditText mEditTextLocation;
    private List<ScanResult> scanResultList;
    boolean isPermitted = false;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Map<String, Integer> wifiList = new TreeMap<>();

    int mRSSICount = 0;

    // BroadcastReceiver 정의
    // 여기서는 이전 예제에서처럼 별도의 Java class 파일로 만들지 않았는데, 어떻게 하든 상관 없음
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                ++mRSSICount; /*if(mRSSICount == 5) { getWifiInfo(); getWifiInfoAvg();} else*/
                getWifiInfo();
            }
        }
    };

    // wifi scan 결과를 얻어서 UI의 TextView에 표시하는 기능 수행
    private void getWifiInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        scanResultList = wifiManager.getScanResults();
        for (int i = 0; i < scanResultList.size(); i++) {
            ScanResult result = scanResultList.get(i);
        }
        scanResultText.append(mEditTextLocation.getText().toString() + "번 위치에서 " + mRSSICount + "번째로 측정한 값" + "===========\n");
        for (int i = 0; i < scanResultList.size(); i++) {
            ScanResult result = scanResultList.get(i);

            if (mRSSICount == 1) {
                wifiList.put(result.BSSID, result.level);
            }
            else{
                if (wifiList.containsKey(result.BSSID)) {
                    wifiList.put(result.BSSID, (wifiList.get(result.BSSID) + result.level) / 2);
                }
                else wifiList.remove(result.BSSID);
            }

        }

        //데이터 다 넣고난 후
        if (mRSSICount % 3 == 0) {
            ValueComparator vc = new ValueComparator(wifiList);
            TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
            sortedMap.putAll(wifiList);
            Iterator<Map.Entry<String, Integer>> it = sortedMap.entrySet().iterator();
            for (int k = 0; k < 5; k++) {
                String mBssidKey = it.next().getKey();
                scanResultText.append((k + 1) + "\t\t BSSID nn:  " + mBssidKey + "\t RSSI: " + wifiList.get(mBssidKey) + " dBm\n");
            }
            mRSSICount = 0;
        }

        scanResultText.append("===================================\n");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssi);

        requestRuntimePermission();

        scanResultText = (TextView)findViewById(R.id.result);
        mEditTextLocation = (EditText)findViewById(R.id.activit_main_location_edittext);
        scanResultText.setMovementMethod(new ScrollingMovementMethod());
        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

        // wifi가 활성화되어있는지 확인 후 꺼져 있으면 켠다
        if(wifiManager.isWifiEnabled() == false)
            wifiManager.setWifiEnabled(true);

        Button backToHome = findViewById(R.id.backToHome);
        backToHome.setOnClickListener(v -> {
            Intent intent = new Intent(RssiManagerActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // wifi scan 결과가 나왔을 때 전송되는 broadcast를 받기 위해
        // IntentFilter 객체를 생성하고 이를 이용하여 BroadcastReceiver 객체를 등록한다
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.start) {
            Toast.makeText(this, "WiFi scan start!!", Toast.LENGTH_LONG).show();

            if(isPermitted) {
                // wifi 스캔 시작
                wifiManager.startScan();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Location access 권한이 없습니다..", Toast.LENGTH_LONG).show();
            }
        }
    }

    //허용하시겠습니까? 퍼미션 창 뜨게하는 것!
    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(RssiManagerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RssiManagerActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(RssiManagerActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
        //*********************************************************************
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // read_external_storage-related task you need to do.

                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // 권한을 얻지 못 하였으므로 location 요청 작업을 수행할 수 없다
                    // 적절히 대처한다
                    isPermitted = false;

                }
                return;
            }
        }
    }
}
