package com.example.yujin2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button wifiSearchButton = findViewById(R.id.serchWifi);

        wifiSearchButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WifiNameActivity.class);
            startActivity(intent);
            finish();
        });
    }
}