package com.example.podgorze_krakow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        findViewById(R.id.post_one).setOnClickListener(
                new android.view.View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                MainActivity.this, com.example.podgorze_krakow.Post_1.class);
                        startActivityForResult(intent, 1);
                    }
                });

        findViewById(R.id.post_two).setOnClickListener(
                new android.view.View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                MainActivity.this, com.example.podgorze_krakow.DrugiPost.class);
                        startActivityForResult(intent, 1);
                    }
                });

        findViewById(R.id.post_three).setOnClickListener(
                new android.view.View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                MainActivity.this, com.example.podgorze_krakow.TrzeciPost.class);
                        startActivityForResult(intent, 1);
                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        return true;

                    case R.id.action_table:
                        startActivity(new Intent(getApplicationContext(), TableActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.action_events:
                        startActivity(new Intent(getApplicationContext(), EventsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.action_team:
                        startActivity(new Intent(getApplicationContext(), TeamActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.action_map:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }

        });
    }
}
















