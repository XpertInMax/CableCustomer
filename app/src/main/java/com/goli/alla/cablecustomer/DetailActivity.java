package com.goli.alla.cablecustomer;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.goli.alla.cablecustomer.data.CustomerContract;
import com.goli.alla.cablecustomer.databinding.ActivityDetailBinding;

import static com.goli.alla.cablecustomer.utilities.CustomerUtils.*;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = DetailActivity.class.getSimpleName();


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

        Log.d(LOG_TAG , "onCreate : called " );

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

        // Setup FAB to open EditorActivity to Edit Customer
        FloatingActionButton editCustomerFAB = (FloatingActionButton)findViewById(R.id.editFab);
        editCustomerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorActivityIntent = new Intent(DetailActivity.this, EditorActivity.class);
                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Log.d(LOG_TAG, "Customer Id " + mUri.getLastPathSegment());
                Uri currentCustomerUri = ContentUris.withAppendedId(CustomerContract.CustomerEntry.CONTENT_URI, Long.parseLong(mUri.getLastPathSegment()));
                //editorActivityIntent.putExtra("CustomerId", mUri.getLastPathSegment());
                // Set the URI on the data field of the intent
                editorActivityIntent.setData(currentCustomerUri);

                // Launch the {@link EditorActivity} to display the data for the current customer.
                startActivity(editorActivityIntent);
            }
        });
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

    /**
     * Runs on the main thread when a load is complete. If initLoader is called (we call it from
     * onCreate in DetailActivity) and the LoaderManager already has completed a previous load
     * for this Loader, onLoadFinished will be called immediately. Within onLoadFinished, we bind
     * the data to our views so the user can see the details of the weather on the date they
     * selected from the forecast.
     *
     * @param loader The cursor loader that finished.
     * @param cursorData   The cursor that is being returned.
     */
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
