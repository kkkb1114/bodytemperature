package kkkb1114.sampleproject.bodytemperature.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Bodytemp_DBHelper extends SQLiteOpenHelper {

    public Bodytemp_DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS USER_PROFILE (" +
                "name TEXT PRIMARY KEY, " +
                "gender INTEGER, " +
                "birthDate TEXT," +
                "weight TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS TEMPDATA (" +
                "name TEXT, " +
                "tempValue DOUBLE, " +
                "tempDateTime DATETIME PRIMARY KEY);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
