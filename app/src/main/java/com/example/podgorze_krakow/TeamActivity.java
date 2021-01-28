package com.example.podgorze_krakow;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeamActivity extends ListActivity {

    private SQLiteDatabase db;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        ListView listPlayers = getListView();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
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
                        return true;

                    case R.id.action_map:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }

        });


        try {
            SQLiteOpenHelper playersDatabaseHelper = new PlayersDatabaseHelper(this, "PlayersDatabase", null, 1);
            db = playersDatabaseHelper.getReadableDatabase();
            db = playersDatabaseHelper.getReadableDatabase();
            cursor = db.query("PLAYERS",
                    new String[]{"_id","NAME"},
            null, null, null, null, null);

            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"NAME"}, new int[]{android.R.id.text1}, 0);

            listPlayers.setAdapter(listAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onListItemClick(ListView listView,
                                View itemView,
                                int position,
                                long id) {
        Intent intent = new Intent(TeamActivity.this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_PLAYERNO, (int) id);
        startActivity(intent);
    }
        }










