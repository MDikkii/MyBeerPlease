package com.example.mikoaj.mybeerplease;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbControler  {
    private static final String DEBUG_TAG = "SqLiteManager";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_BEER_TABLE = "beer";

    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_NAME = "name";
    public static final String NAME_OPTIONS = "TEXT NOT NULL";
    public static final int NAME_COLUMN = 1;
    public static final String KEY_DESCRIPTION = "description";
    public static final String DESCRIPTION_OPTIONS = "TEXT NOT NULL";
    public static final int DESCRIPTION_COLUMN = 2;
    public static final String KEY_PHOTO = "photo";
    public static final String PHOTO_OPTIONS = "BLOB";
    public static final int PHOTO_COLUMN = 3;
    public static final String KEY_TYPE = "type";
    public static final String TYPE_OPTIONS = "TEXT NOT NULL";
    public static final int TYPE_COLUMN = 4;
    public static final String KEY_PRICE = "price";
    public static final String PRICE_OPTIONS = "REAL DEFAULT 3.50";
    public static final int PRICE_COLUMN = 5;



    private static final String DB_CREATE_BEER_TABLE =
            "CREATE TABLE " + DB_BEER_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_NAME + " " + NAME_OPTIONS + ", " +
                    KEY_DESCRIPTION + " " + DESCRIPTION_OPTIONS + ", " +
                    KEY_PHOTO + " " + PHOTO_OPTIONS + ", " +
                    KEY_TYPE + " " + TYPE_OPTIONS + ", " +
                    KEY_PRICE + " " + PRICE_OPTIONS +
                    ");";
    private static final String DROP_BEER_TABLE =
            "DROP TABLE IF EXISTS " + DB_BEER_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_BEER_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_BEER_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_BEER_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_BEER_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public DbControler(Context context) {
        this.context = context;
    }

    public DbControler open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertBravery(String name, String description, byte[] photo, String type, double price) {
        ContentValues newBraveryValues = new ContentValues();
        newBraveryValues.put(KEY_NAME, name);
        newBraveryValues.put(KEY_DESCRIPTION, description);
        newBraveryValues.put(KEY_PHOTO, photo);
        newBraveryValues.put(KEY_TYPE, type);
        newBraveryValues.put(KEY_PRICE, price);
        return db.insert(DB_BEER_TABLE, null, newBraveryValues);
    }

    public boolean updateBravery(Beer beer) {
        long id = beer.getId();
        String name = beer.getName();
        String description = beer.getDescription();
        byte[] photo = beer.getImageArray();
        String type = beer.getType();
        double price = beer.getPrice();
        return updateBravery(id, name, description, photo, type, price);
    }

    public boolean updateBravery(long id, String name, String description, byte[] photo, String type, double price) {
        String where = KEY_ID + "=" + id;
       // int photoTask = photo ? 1 : 0;
        ContentValues updateBraveryValues = new ContentValues();
        updateBraveryValues.put(KEY_NAME, name);
        updateBraveryValues.put(KEY_DESCRIPTION, description);
        updateBraveryValues.put(KEY_PHOTO, photo);
        updateBraveryValues.put(KEY_TYPE, type);
        updateBraveryValues.put(KEY_PRICE, price);
        return db.update(DB_BEER_TABLE, updateBraveryValues, where, null) > 0;
    }

    public boolean deleteBravery(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(DB_BEER_TABLE, where, null) > 0;
    }

    public Cursor getAllBraveries(String orderbyColumn, String orderbyWay) {
        String[] columns = {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_PHOTO, KEY_TYPE, KEY_PRICE};
        if(orderbyColumn == null || orderbyWay == null)
            return db.query(DB_BEER_TABLE, columns, null, null, null, null, null);
        return db.query(DB_BEER_TABLE, columns, null, null, null, null, orderbyColumn + " " + orderbyWay);
    }

    public Beer getBravery(long id) {
        String[] columns = {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_PHOTO, KEY_TYPE, KEY_PRICE};
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(DB_BEER_TABLE, columns, where, null, null, null, null);
        Beer beer = null;
        if(cursor != null && cursor.moveToFirst()) {
            String name =  cursor.getString(NAME_COLUMN);
            String description = cursor.getString(DESCRIPTION_COLUMN);
            byte[] photo = cursor.getBlob(PHOTO_COLUMN);
            String type = cursor.getString(TYPE_COLUMN);
            double price = cursor.getDouble(PRICE_COLUMN);
            beer = new Beer(id,name, description, photo, type, price);
        }
        return beer;
    }
}
