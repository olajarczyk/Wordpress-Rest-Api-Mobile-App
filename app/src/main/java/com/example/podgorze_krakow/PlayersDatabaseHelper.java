package com.example.podgorze_krakow;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.podgorze_krakow.R.drawable.macias_przod;

class PlayersDatabaseHelper extends SQLiteOpenHelper {
    public static final String database_name = "PlayersDatabase";
    private Context c;

    public PlayersDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE PLAYERS (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
           + "NAME TEXT, "
           + "DESCRIPTION TEXT, "
           + "IDIMAGE INTEGER, "
                + "NUMBER TEXT, "
                + "CLUB TEXT);");


        insertPlayers(db, "Kinga Będkowska", "Pomocnik", R.drawable.bedkowska_przod, "17", "Poprzedni klub: Wanda Kraków");
        insertPlayers(db, "Małgorzata Borgosz", "Pomocnik", R.drawable.borgosz_przod, "13", "Poprzedni klub: Wychowanek");
        insertPlayers(db, "Marlena Brzeszczyńska", "Pomocnik", R.drawable.brzeszczynska_przod, "19", "Poprzedni klub: Prądniczanka Kraków");
        insertPlayers(db, "Magdalena Buczek", "Napastnik", R.drawable.buczek_przod, "2", "Poprzedni klub: Prądniczanka Kraków");
        insertPlayers(db,"Anna Bulanda", "Obrońca", R.drawable.bulanda_przod, "7", "Poprzedni klub: LKS Szaflary");
        insertPlayers(db, "Aleksandra Chmura", "Napastnik", R.drawable.chmura_przod, "20", "Poprzedni klub: Victoria Gaj");
        insertPlayers(db, "Magdalena Cieślik", "Obrońca", R.drawable.cieslik_przod, "11", "Poprzedni klub: Wychowanek");
        insertPlayers(db, "Urszula Cop", "Pomocnik", R.drawable.cop_przod, "4", "Poprzedni klub: Wychowanek");
        insertPlayers(db, "Karolina Gryc", "Pomocnik", R.drawable.gryc_przod, "33", "Poprzedni klub: PKS Piastovia Piastów");
        insertPlayers(db, "Joanna Haracz", "Bramkarz", R.drawable.haracz_przod, "12", "Poprzedni klub: Sokół Kolbuszowa Dolna");
        insertPlayers(db, "Aleksandra Jarczyk", "Pomocnik", R.drawable.jarczyka_przod, "3", "Poprzedni klub: Wychowanek");
        insertPlayers(db, "Karolina Jarczyk", "Pomocnik", R.drawable.jarczykk_przod, "21", "Poprzedni klub: Wychowanek ");
        insertPlayers(db, "Kinga Wilk", "Bramkarz", R.drawable.kinga_przod, "1", "Poprzedni klub: Wychowanek");
        insertPlayers(db, "Iwona Klimkiewicz", "Obrońca", R.drawable.klimkiewicz_przod, "5", "Poprzedni klub: Wychowanek ");
        insertPlayers(db, "Daria Kokoszka", "Pomocnik", R.drawable.kokoszka_przod, "10", "Poprzedni klub: KKS Czarni Sosnowiec");
        insertPlayers(db, "Karolina Łach", "Napastnik", R.drawable.lach_przod, "44", "Poprzedni klub: FSA Kraków");
        insertPlayers(db, "Aleksandra Łagowska", "Obrońca", R.drawable.lagowska_przod, "15", "Poprzedni klub: Rysy Bukowina Tatrzańska");
        insertPlayers(db, "Anna Maciaś", "Napastnik", macias_przod, "8", "Poprzedni klub: Bronowianka Kraków");
        insertPlayers(db, "Monika Markiewicz", "Napastnik", R.drawable.markiewicz_przod, "9", "Poprzedni klub: Piast Łapanów");
        insertPlayers(db, "Karolina Nowakowska", "Obrońca", R.drawable.nowakowska_przod, "6", "Poprzedni klub: Gol Częstochowa");
        insertPlayers(db, "Katarzyna Urbaniak", "Obrońca", R.drawable.urbaniak_przod, "22", "Poprzedni klub: Victoria Gaj");
        insertPlayers(db, "Paulina Włodarz", "Pomocnik", R.drawable.wlodarz_przod, "23", "Poprzedni klub: AZS UJ");
        insertPlayers(db, "Amelia Żuwała", "Obrońca", R.drawable.zuwala_przod, "55", "Poprzedni klub: FSA Kraków");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* db.execSQL(" DROP TABLE IF EXISTS PLAYERS " );
        onCreate(db);*/
        switch(oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE PLAYERS  ADD COLUMN  UPGRADE TEXT ");
        }
        }
    private static void insertPlayers(SQLiteDatabase db, String name, String description, int idimage, String number, String club) {
        ContentValues playerValues = new ContentValues();
        playerValues.put("NAME", name);
        playerValues.put("DESCRIPTION", description);
        playerValues.put("IDIMAGE", idimage);
        playerValues.put("NUMBER", number);
        playerValues.put("CLUB", club);
        db.insert("PLAYERS", null, playerValues);
    }

}
