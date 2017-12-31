package com.goli.alla.cablecustomer.utilities;

import com.goli.alla.cablecustomer.data.CustomerContract;

/**
 * Created by Amani on 30-12-2017.
 */

public class CustomerUtils {
    //  Create a String array containing the names of the desired data columns from our ContentProvider
    /*
     * The columns of data that we are interested in displaying within our DetailActivity's list of
     * Customer data.
     */
    public static final String[] MAIN_CUSTOMER_PROJECTION = {
            CustomerContract.CustomerEntry.COLUMN_NAME_FIRST,
            CustomerContract.CustomerEntry.COLUMN_NAME_LAST,
            CustomerContract.CustomerEntry.COLUMN_NAME_MIDDLE,
            CustomerContract.CustomerEntry._ID,
            CustomerContract.CustomerEntry.COLUMN_TIMESTAMP,
            CustomerContract.CustomerEntry.COLUMN_ADDRESS1,
            CustomerContract.CustomerEntry.COLUMN_APT_NUM,
            CustomerContract.CustomerEntry.COLUMN_CITY,
            CustomerContract.CustomerEntry.COLUMN_STATE,
            CustomerContract.CustomerEntry.COLUMN_PHONE,
            CustomerContract.CustomerEntry.COLUMN_ADDRESS2,
            CustomerContract.CustomerEntry.COLUMN_ZIPCODE
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_COLUMN_NAME_FIRST = 0;
    public static final int INDEX_COLUMN_NAME_LAST = 1;
    public static final int INDEX_COLUMN_NAME_MIDDLE = 2;
    public static final int INDEX_COLUMN_ID = 3;
    public static final int INDEX_COLUMN_TIMESTAMP = 4;
    public static final int INDEX_COLUMN_ADDRESS1 = 5;
    public static final int INDEX_COLUMN_APTNUM = 6;
    public static final int INDEX_COLUMN_CITY = 7;
    public static final int INDEX_COLUMN_STATE = 8;
    public static final int INDEX_COLUMN_PHONE = 9;
    public static final int INDEX_COLUMN_ADDRESS2 = 10;
    public static final int INDEX_COLUMN_ZIPCODE = 11;

}
