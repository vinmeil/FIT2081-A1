package com.fit2081.a1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(KeyStore.FILE_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KeyStore.KEY_USERNAME, null);
        String password = sharedPreferences.getString(KeyStore.KEY_PASSWORD, null);

        if (username == null || password == null) {
            // User is not logged in, redirect to Sign Up activity
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        }

        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
        }, 0);
    }

    public void onCreateNewEventCategoryButtonClick(View view) {
        Intent intent = new Intent(this, NewEventCategoryActivity.class);
        startActivity(intent);
    }

    public void onAddEventButtonClick(View view) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }
}