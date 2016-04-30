package pervasivecomputing.locationreminder;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    public static final String DB_NAME = "test.db";
    public static final int DB_VERS = 1;
    public static final String TABLE = "test_table";
    public static final boolean Debug = false;
    public Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME , null, 1);
    }

    public Cursor query(SQLiteDatabase db, String query) {
        Cursor cursor = db.rawQuery(query, null);
        if (Debug) {
            Log.d(TAG, "Executing Query: "+ query);
        }
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE test_table " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT,longitude REAL,latitude REAL, locationname TEXT, radius INTEGER, timestamp INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS test_table");
        onCreate(db);
    }

    public boolean createTask(String task, double longitude, double latitude, int radius, String locationName,long timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", task);
        contentValues.put("longitude", longitude);
        contentValues.put("latitude", latitude);
        contentValues.put("locationname", locationName);
        contentValues.put("radius", radius);
        contentValues.put("timestamp", timestamp);

        db.insert("test_table",null,contentValues);
        return true;
    }

    public Integer deleteTask (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("test_table",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllTasks()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from test_table", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("task")));
            res.moveToNext();
        }
        return array_list;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "test_table");
        return numRows;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from test_table where id="+id+"", null );
        return res;
    }
}