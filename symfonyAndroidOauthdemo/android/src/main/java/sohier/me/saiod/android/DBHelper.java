package sohier.me.saiod.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_DEMO = "demo";
    public static final String DEMO_ID = "_id";
    public static final String DEMO_TITLE = "title";
    public static final String DEMO_DESCRIPTION = "description";
    public static final String DEMO_SERVER_ID = "server_id";

    private static final String DATABASE_NAME = "demo.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DEMO + "(" + DEMO_ID
            + " integer primary key autoincrement, " + DEMO_TITLE
            + " text not null, " + DEMO_DESCRIPTION + " text not null, "
            + " integer " + DEMO_SERVER_ID + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEMO);
        onCreate(db);
    }
}
