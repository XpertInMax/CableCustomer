package com.goli.alla.cablecustomer.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by valla on 12/28/2017.
 */

public class CustomerContract {

    /*
    * The "Content authority" is a name for the entire content provider, similar to the
    * relationship between a domain name and its website. A convenient string to use for the
    * content authority is the package name for the app, which is guaranteed to be unique on the
    * Play Store.
    */
    public static final String CONTENT_AUTHORITY = "com.goli.alla.cablecustomer";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.gaf.android.cablevision/customer/
     *     [           BASE_CONTENT_URI         ][ PATH_CUSTOMER]
     *
     * is a valid path for looking at weather data.
     *
     *      content://com.gaf.android.sunshine/givemeroot/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
     */
    public static final String PATH_CUSTOMER = "customer";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CustomerContract(){

    }

    /* Inner class that defines the table contents */
    public static class CustomerEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CUSTOMER)
                .build();

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMER;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMER;


        /* Used internally as the name of our customer table. */
        public static final String TABLE_NAME = "customer";

        public static final String COLUMN_NAME_FIRST = "firstname";
        public static final String COLUMN_NAME_MIDDLE = "middlename";
        public static final String COLUMN_NAME_LAST = "lastname";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_ADDRESS1 = "address1";
        public static final String COLUMN_ADDRESS2 = "address2";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIPCODE = "zipcode";
        public static final String COLUMN_APT_NUM = "aptnum";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
