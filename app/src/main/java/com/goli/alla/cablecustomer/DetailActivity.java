package com.goli.alla.cablecustomer;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.goli.alla.cablecustomer.data.CustomerContract;
import com.goli.alla.cablecustomer.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

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
            CustomerContract.CustomerEntry.COLUMN_ADDRESS2
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

    /*
    * This ID will be used to identify the Loader responsible for loading the weather details
    * for a particular day. In some cases, one Activity can deal with many Loaders. However, in
    * our case, there is only one. We will still use this ID to initialize the loader and create
    * the loader for best practice. Please note that 353 was chosen arbitrarily. You can use
    * whatever number you like, so long as it is unique and consistent.
    */
    private static final int ID_DETAIL_LOADER = 353;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

    /* The URI that is used to access the chosen day's weather details */
    private Uri mUri;

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    // Declare an ActivityDetailBinding field called mDetailBinding
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detail);
        //Instantiate mDetailBinding using DataBindingUtil
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        //Remove the call to setContentView
        //Remove all the findViewById calls

        mUri = getIntent().getData();
        if (mUri == null)
            throw new NullPointerException("URI for DetailActivity cannot be null");

        /* This connects our Activity into the loader lifecycle. */
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    /**
     * Creates and returns a CursorLoader that loads the data for our URI and stores it in a Cursor.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param loaderArgs Any arguments supplied by the caller
     *
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {
        switch (loaderId){
            case ID_DETAIL_LOADER:
                return new CursorLoader(this, mUri, MAIN_CUSTOMER_PROJECTION, null, null, null);
            default:
                throw new RuntimeException("Loader not implement " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
         /*
         * Before we bind the data to the UI that will display that data, we need to check the
         * cursor to make sure we have the results that we are expecting. In order to do that, we
         * check to make sure the cursor is not null and then we call moveToFirst on the cursor.
         * Although it may not seem obvious at first, moveToFirst will return true if it contains
         * a valid first row of data.
         *
         * If we have valid data, we want to continue on to bind that data to the UI. If we don't
         * have any data to bind, we just return from this method.
         */
        boolean cursorHasValidData = false;
        if (cursorData != null && cursorData.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        mDetailBinding.tvFirstName.setText(cursorData.getString(INDEX_COLUMN_NAME_FIRST));
        mDetailBinding.tvMiddleName.setText(cursorData.getString(INDEX_COLUMN_NAME_MIDDLE));
        mDetailBinding.tvLastName.setText(cursorData.getString(INDEX_COLUMN_NAME_LAST));
        mDetailBinding.tvMiddleName.setText(cursorData.getString(INDEX_COLUMN_NAME_MIDDLE));
        mDetailBinding.tvAddress1.setText(cursorData.getString(INDEX_COLUMN_ADDRESS1));
        mDetailBinding.tvAddress2.setText(cursorData.getString(INDEX_COLUMN_ADDRESS2));
        mDetailBinding.tvAptnum.setText(cursorData.getString(INDEX_COLUMN_APTNUM));
        mDetailBinding.tvCity.setText(cursorData.getString(INDEX_COLUMN_CITY));
        mDetailBinding.tvState.setText(cursorData.getString(INDEX_COLUMN_STATE));
        mDetailBinding.tvPhone.setText(cursorData.getString(INDEX_COLUMN_PHONE));
        mDetailBinding.tvCutomerId.setText(cursorData.getString(INDEX_COLUMN_ID));


    }

    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * Since we don't store any of this cursor's data, there are no references we need to remove.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
