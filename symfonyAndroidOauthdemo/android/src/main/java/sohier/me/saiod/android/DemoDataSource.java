package sohier.me.saiod.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DemoDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = { DBHelper.DEMO_ID, DBHelper.DEMO_TITLE, DBHelper.DEMO_TITLE };

    public DemoDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open()  {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Demo createDemo(String title, String description) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.DEMO_TITLE, title);
        values.put(DBHelper.DEMO_DESCRIPTION, description);

        Log.e("test", DBHelper.TABLE_DEMO + " " + title + " " + description);

        long insertId = database.insert(DBHelper.TABLE_DEMO, null,
                values);
        Cursor cursor = database.query(DBHelper.TABLE_DEMO,
                allColumns, DBHelper.DEMO_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Demo newDemo = cursorToDemo(cursor);
        cursor.close();
        return newDemo;
    }

    public void deleteDemo(Demo demo) {
        long id = demo.getId();
        System.out.println("Demo deleted with id: " + id);
        database.delete(DBHelper.TABLE_DEMO, DBHelper.DEMO_ID
                + " = " + id, null);
    }

    public List<Demo> getAllDemos() {
        List<Demo> demos = new ArrayList<Demo>();

        Cursor cursor = database.query(DBHelper.TABLE_DEMO,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Demo demo = cursorToDemo(cursor);
            demos.add(demo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return demos;
    }

    private Demo cursorToDemo(Cursor cursor) {
        Demo demo = new Demo();
        demo.setId(cursor.getLong(0));
        demo.setTitle(cursor.getString(1));
        demo.setDescription(cursor.getString(2));
        return demo;
    }
}
