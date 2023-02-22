package kkkb1114.sampleproject.bodytemperature.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Bodytemp_DBHelper extends SQLiteOpenHelper {

    public static Bodytemp_DBHelper mInstance;
    public static SQLiteDatabase writableDatabase;
    public static SQLiteDatabase readableDataBase;

    public Bodytemp_DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS USER_PROFILE (" +
                "name TEXT PRIMARY KEY, " +
                "gender INTEGER, " +
                "birthDate TEXT," +
                "weight TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS TEMPDATA (" +
                "name TEXT, " +
                "tempValue DOUBLE, " +
                "tempDateTime DATETIME PRIMARY KEY);");

        db.execSQL("CREATE TABLE IF NOT EXISTS TIMELINEDATA (" +
                "name TEXT, " +
                "Value TEXT, " +
                "TimelineDateTime DATETIME PRIMARY KEY);");

        db.close();
    }

    public static Bodytemp_DBHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        if (mInstance == null){
            mInstance = new Bodytemp_DBHelper(context, name, factory, version);
            writableDatabase = mInstance.getWritableDatabase();
            readableDataBase = mInstance.getReadableDatabase();
        }
        return mInstance;
    }

    /*
     * SQLiteOpenHelper의 onCreate()는 db.getWritableDatabase() 또는 db.getReadableDatabase()가
     * 실행될때 실행되기에 TABLE 생성 코드는 생성자에 넣었다.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void closeDBHelper(){
        mInstance.close();
        writableDatabase.close();
        readableDataBase.close();
    }
}
