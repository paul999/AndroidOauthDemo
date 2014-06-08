package sohier.me.saiod.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DemoDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = { DBHelper.DEMO_LOCAL_ID, DBHelper.DEMO_TITLE, DBHelper.DEMO_DESCRIPTION, DBHelper.DEMO_SERVER_ID };

    public DemoDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open()  {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Update or create a new demo in the database.
     *
     * @param demo demo to update or create
     * @return the created or updated demo
     */
    public Demo createOrUpdateDemo(Demo demo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.DEMO_TITLE, demo.getTitle());
        values.put(DBHelper.DEMO_DESCRIPTION, demo.getDescription());
        values.put(DBHelper.DEMO_SERVER_ID, demo.getId());

        long ID = 0;

        if (demo.getLocalId() == 0)
        {

            ID = database.insert(DBHelper.TABLE_DEMO, null,
                    values);
            Log.d("saiod", "created " + ID);
        }
        else
        {
            ID = demo.getLocalId();
            database.update(DBHelper.TABLE_DEMO, values, DBHelper.DEMO_LOCAL_ID + " = ?", new String[] { String.valueOf(demo.getLocalId()) });
            Log.d("saiod", "updated " + ID);
        }

        Cursor cursor = database.query(DBHelper.TABLE_DEMO,
                allColumns, DBHelper.DEMO_LOCAL_ID + " = " + ID, null,
                null, null, null);
        cursor.moveToFirst();
        Demo newDemo = cursorToDemo(cursor);
        cursor.close();
        return newDemo;
    }

    public void deleteDemo(Demo demo) {
        deleteDemo(demo.getId());
    }
    public void deleteDemo(long id)
    {
        System.out.println("Demo deleted with id: " + id);
        database.delete(DBHelper.TABLE_DEMO, DBHelper.DEMO_SERVER_ID
                + " = " + id, null);
    }

    public Demo getDemo(long id)
    {
        Cursor cursor = database.query(DBHelper.TABLE_DEMO, allColumns, DBHelper.DEMO_SERVER_ID + " = ?", new String[] { String.valueOf(id) }, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            return cursorToDemo(cursor);
        }
        Log.d("saiod", "No item found");
        return null;
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

        cursor.getColumnIndex(DBHelper.DEMO_DESCRIPTION);

        Demo demo = new Demo();
        demo.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.DEMO_SERVER_ID)));
        demo.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.DEMO_TITLE)));
        demo.setDescription(cursor.getString(cursor.getColumnIndex(DBHelper.DEMO_DESCRIPTION)));
        demo.setLocalId(cursor.getLong(cursor.getColumnIndex(DBHelper.DEMO_LOCAL_ID)));

        Log.d("saiod", String.format("id: %s title %s desc %s local %s", demo.getId(), demo.getTitle(), demo.getDescription(), demo.getLocalId()));


        return demo;
    }
}
