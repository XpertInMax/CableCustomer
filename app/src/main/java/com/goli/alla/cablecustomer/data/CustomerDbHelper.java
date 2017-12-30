package com.goli.alla.cablecustomer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.goli.alla.cablecustomer.data.CustomerContract.CustomerEntry;
/**
 * Created by valla on 12/28/2017.
 */

public class CustomerDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    private static final String DATABASE_NAME = "customer.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     *
     */
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CustomerEntry.TABLE_NAME + " (" +
             CustomerEntry._ID + " INTEGER PRIMARY KEY, " +
             CustomerEntry.COLUMN_NAME_FIRST + " TEXT NOT NULL , " +
             CustomerEntry.COLUMN_NAME_LAST + " TEXT NOT NULL, " +
             CustomerEntry.COLUMN_NAME_MIDDLE + " TEXT, " +
             CustomerEntry.COLUMN_PHONE + " TEXT, " +
             CustomerEntry.COLUMN_ADDRESS1 + " TEXT, " +
             CustomerEntry.COLUMN_ADDRESS2 +  " TEXT, " +
             CustomerEntry.COLUMN_CITY+  " TEXT, " +
             CustomerEntry.COLUMN_STATE +  " TEXT, " +
             CustomerEntry.COLUMN_ZIPCODE +  " TEXT, " +
             CustomerEntry.COLUMN_APT_NUM +  " TEXT, " +
             CustomerEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CustomerEntry.TABLE_NAME;

    public CustomerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}
