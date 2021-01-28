package com.example.podgorze_krakow;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity {

    public static final String EXTRA_PLAYERNO = "playerNo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        //pobieramy identyfikator zawodnika z intencji
        int playerNo = (Integer) getIntent().getExtras().get(EXTRA_PLAYERNO);

        //Tworzymy kursor
        try {
            SQLiteOpenHelper playersDatabaseHelper = new PlayersDatabaseHelper(this, "PlayersDatabase", null, 1 );
            SQLiteDatabase db = playersDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PLAYERS", new String[]{"NAME", "DESCRIPTION", "IDIMAGE", "NUMBER", "CLUB"},
                    "_id=?",
                    new String[]{Integer.toString(playerNo)},
                    null, null, null);

            if (cursor.moveToFirst()){
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);
                String numberText = cursor.getString(3);
                String clubText = cursor.getString(4);

                // Wyświetlamy nazwę zawodnika
                 TextView name = (TextView)findViewById(R.id.name);
                name.setText(nameText);
                // Wyświetlamy opis zawodnika
                TextView description = (TextView)findViewById(R.id.description);
                description.setText(descriptionText);
                // Wyświetlamy zdjęcie zawodnika
                ImageView photo = (ImageView)findViewById(R.id.idimage);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);

                // Wyświetlamy klub poprzedni
                TextView club = (TextView)findViewById(R.id.club);
                club.setText(clubText);

                // Wyświetlamy numer
                TextView number = (TextView)findViewById(R.id.number);
                number.setText(numberText);
            }

            cursor.close();
            db.close();
        } catch (SQLException e){
            Toast toast = Toast.makeText(this, "Baza danych nie jest dostępna", Toast.LENGTH_SHORT);
            toast.show();
        }


    }
}
