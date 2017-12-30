package com.goli.alla.cablecustomer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.goli.alla.cablecustomer.data.CustomerContract.CustomerEntry;


/**
 * Created by valla on 12/28/2017.
 */

public class CustomerProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = CustomerProvider.class.getSimpleName();

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    /** URI matcher code for the content URI for the Customers table */
    private static final int CUSTOMER = 100;

    /** URI matcher code for the content URI for a single Customer in the Customers table */
    private static final int CUSTOMER_ID = 101;

    /*
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of CustomerProvider and is a
     * common convention in Android programming.
     */
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private CustomerDbHelper mCustomerDbHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CUSTOMER and
     * CUSTOMER_ID constants defined above.
     * <p>
     * It's possible you might be thinking, "Why create a UriMatcher when you can use regular
     * expressions instead? After all, we really just need to match some patterns, and we can
     * use regular expressions to do that right?" Because you're not crazy, that's why.
     * <p>
     * UriMatcher does all the hard work for you. You just have to tell it which code to match
     * with which URI, and it does the rest automagically. Remember, the best programmers try
     * to never reinvent the wheel. If there is a solution for a problem that exists and has
     * been tested and proven, you should almost always use it unless there is a compelling
     * reason not to.
     *
     * @return A UriMatcher that correctly matches the constants for CUSTOMER and CUSTOMER_ID
     */
    public static UriMatcher buildUriMatcher(){
        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CustomerContract.CONTENT_AUTHORITY;

        /*
         * For each type of URI you want to add, create a corresponding code. Preferably, these are
         * constant fields in your class so that you can use them throughout the class and you no
         * they aren't going to change. In Sunshine, we use CUSTOMER or CUSTOMER_ID.
         */

        /* This URI is content://com.goli.alla.cablecustomer/customer/ */

        uriMatcher.addURI(authority, CustomerContract.PATH_CUSTOMER, CUSTOMER);
         /*
         * This URI would look something like content://com.goli.alla.cablecustomer/customer/1
         * The "/#" signifies to the UriMatcher that if PATH_WEATHER is followed by ANY number,
         * that it should return the CUSTOMER_ID code
         */
        uriMatcher.addURI(authority, CustomerContract.PATH_CUSTOMER + "/#", CUSTOMER_ID);

        return uriMatcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     *
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     *
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since CustomerDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mCustomerDbHelper = new CustomerDbHelper(getContext());
        return true;
    }

    /**
     * Handles query requests from clients. We will use this method in Sunshine to query for all
     * of our weather data as well as to query for the weather on a particular day.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        SQLiteDatabase sqLiteDatabase = mCustomerDbHelper.getReadableDatabase();

        switch (match){
             /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.goli.alla.cablecustomer/customer/
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return all of the customers in our customer table.
             *
             * In this case, we want to return a cursor that contains every row of customer data
             * in our customer table.
             */
            case CUSTOMER:
                cursor = sqLiteDatabase.query(CustomerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
             /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.goli.alla.cablecustomer/customer/1
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the weather for a particular date. The integer value is at the very end of the URI (1)
             * and can be accessed programmatically using Uri's getLastPathSegment method.
             *
             * In this case, we want to return a cursor that contains one row of weather data for
             * a particular date.
             */
            case CUSTOMER_ID:
                String customerId = uri.getLastPathSegment();
                /*
                 * The URI that matches CUSTOMER_ID contains a integer at the end
                 * of it. We extract that integer and use it with these next two lines to
                 * specify the row of customer we want returned in the cursor. We use a
                 * question mark here and then designate selectionArguments as the next
                 * argument for performance reasons. Whatever Strings are contained
                 * within the selectionArguments array will be inserted into the
                 * selection statement by SQLite under the hood.
                 */
                String mSelection = "_ID = ?";
                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] mSelectionArgs = new String[]{customerId};
                cursor = sqLiteDatabase.query(CustomerEntry.TABLE_NAME,
                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CUSTOMER:
                return CustomerEntry.CONTENT_LIST_TYPE;
            case CUSTOMER_ID:
                return CustomerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);

        long insertedRowId;

        switch (match){
            case CUSTOMER:
                // Insert the new Customer with the given values
                insertedRowId = mCustomerDbHelper.getWritableDatabase().insert(
                        CustomerEntry.TABLE_NAME,
                        null,
                        contentValues);
                break;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        // Once we know the ID of the new row in the table,
        if (insertedRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, insertedRowId);
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);

        SQLiteDatabase sqLiteDatabase = mCustomerDbHelper.getWritableDatabase();

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        switch (match){
            case CUSTOMER:
                numRowsDeleted = sqLiteDatabase.delete(CustomerEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CUSTOMER_ID:
                String customerId = uri.getLastPathSegment();
                selection = "_ID = ?";
                selectionArgs = new String[]{customerId};
                Log.e(LOG_TAG, "Delete - Customer will be deleted : " + customerId);
                numRowsDeleted = sqLiteDatabase.delete(CustomerEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            // Notify all listeners that the data has changed for the pet content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String whereClause, @Nullable String[] whereArgs) {
        int match = sUriMatcher.match(uri);

        int numRowsUpdated;

        switch (match){
            // For the CUSTOMER_ID code, extract out the ID from the URI,
            // so we know which row to update. Selection will be "_id=?" and selection
            // arguments will be a String array containing the actual ID.
            case CUSTOMER_ID:
                String customerId = uri.getLastPathSegment();
                whereClause = "_ID = ?";
                whereArgs = new String[]{customerId};
                Log.e(LOG_TAG, "Update - Customer will be updated : " + customerId);
                numRowsUpdated =mCustomerDbHelper.getWritableDatabase().update(
                        CustomerEntry.TABLE_NAME,
                        contentValues,
                        whereClause,
                        whereArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if(numRowsUpdated > 0) {
            // Notify all listeners that the data has changed for the pet content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsUpdated;
    }

    /**
     * Handles requests to insert a set of new rows. In Sunshine, we are only going to be
     * inserting multiple rows of data at a time from a weather forecast. There is no use case
     * for inserting a single row of data into our ContentProvider, and so we are only going to
     * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
     * to provide proper functionality for the insert method as well.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mCustomerDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CUSTOMER:
                db.beginTransaction();
                int rowsInserted = 0;

                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(CustomerContract.CustomerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                Log.d(LOG_TAG, "Number of rows Inserted " + rowsInserted);
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
