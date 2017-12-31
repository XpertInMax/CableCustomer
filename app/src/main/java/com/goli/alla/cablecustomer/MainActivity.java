package com.goli.alla.cablecustomer;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.goli.alla.cablecustomer.adapter.CustomerAdapter;
import com.goli.alla.cablecustomer.data.CustomerContract.CustomerEntry;
import com.goli.alla.cablecustomer.utilities.FakeDataUtils;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        CustomerAdapter.CustomerAdapterListItemClickHandler {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private CustomerAdapter mCustomerAdapter;

    private RecyclerView mRecyclerViewCustomer;

    private ProgressBar mLoadingIndicator;

    private int mPosition = RecyclerView.NO_POSITION;

    private Toast mToast;


    //  Create a String array containing the names of the desired data columns from our ContentProvider
    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] MAIN_CUSTOMER_PROJECTION = {
            CustomerEntry.COLUMN_NAME_FIRST,
            CustomerEntry.COLUMN_NAME_LAST,
            CustomerEntry.COLUMN_NAME_MIDDLE,
            CustomerEntry._ID,
            CustomerEntry.COLUMN_TIMESTAMP,
            CustomerEntry.COLUMN_ADDRESS1,
            CustomerEntry.COLUMN_APT_NUM,
            CustomerEntry.COLUMN_CITY,
            CustomerEntry.COLUMN_STATE
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

    /*
    * This ID will be used to identify the Loader responsible for loading our weather forecast. In
    * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
    * We will still use this ID to initialize the loader and create the loader for best practice.
    * Please note that 44 was chosen arbitrarily. You can use whatever number you like, so long as
    * it is unique and consistent.
    */
    private static final int ID_CUSTOMER_LOADER = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG , "onCreate : called " );

        // Setup FAB to open EditorActivity to Add Customer
        FloatingActionButton onAddCustomerFab = (FloatingActionButton) findViewById(R.id.fab);
        onAddCustomerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorActivityIntent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(editorActivityIntent);
            }
        });
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerViewCustomer = findViewById(R.id.rv_customers);
        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);
        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * LinearLayoutManager class for vertical lists, LinearLayoutManager.VERTICAL.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         *
         * The third parameter (shouldReverseLayout) should be true if you want to reverse your
         * layout. Generally, this is only true with horizontal lists that need to support a
         * right-to-left layout.
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerViewCustomer.setLayoutManager(linearLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        //mRecyclerViewCustomer.setHasFixedSize(true);

        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         *
         * Although passing in "this" twice may seem strange, it is actually a sign of separation
         * of concerns, which is best programming practice. The ForecastAdapter requires an
         * Android Context (which all Activities are) as well as an onClickHandler. Since our
         * MainActivity implements the ForecastAdapter ForecastOnClickHandler interface, "this"
         * is also an instance of that type of handler.
         */
        mCustomerAdapter = new CustomerAdapter(this, this);

         /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerViewCustomer.setAdapter(mCustomerAdapter);

        showLoading();

         /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_CUSTOMER_LOADER, null, this);

        /* Use this only at firt time for loading intial data for testing
        Uri uri = CustomerEntry.CONTENT_URI;
        getContentResolver().delete(uri, null, null);

        FakeDataUtils.insertFakeData(this);*/
    }

    /**
     * Called by the {@link android.support.v4.app.LoaderManagerImpl} when a new Loader needs to be
     * created. This Activity only uses one loader, so we don't necessarily NEED to check the
     * loaderId, but this is certainly best practice.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {
            // If the loader requested is our customer loader, return the appropriate CursorLoader
            case ID_CUSTOMER_LOADER:
                /* URI for all rows of Customer data in our weather table */
                Uri customerQueryUri = CustomerEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = CustomerEntry.COLUMN_TIMESTAMP + " DESC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                //String selection = CustomerEntry.getSqlSelectForTodayOnwards();
                CursorLoader cursor =  new CursorLoader(this,
                        customerQueryUri,
                        MAIN_CUSTOMER_PROJECTION,
                        null,
                        null,
                        sortOrder);

                Log.d(LOG_TAG , "onCreateLoader : No of rows fetched " + cursor.getUri());
                return cursor;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
     * initial load being performed with no access to internet, the loading indicator will show
     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
     * future version of the course.
     *
     * @param loader The Loader that has finished.
     * @param cursorData   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        mCustomerAdapter.swapCursor(cursorData);
        //If mPosition equals RecyclerView.NO_POSITION, set it to 0
        Log.d(LOG_TAG , "onLoadFinished : No of rows fetched " + cursorData.getCount());
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        // Smooth scroll the RecyclerView to mPosition
        mRecyclerViewCustomer.smoothScrollToPosition(mPosition);

        // If the Cursor's size is not equal to 0, call showWeatherDataView
        if (cursorData.getCount() != 0)
            showCustomerDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mCustomerAdapter.swapCursor(null);
    }

    /**
     * This method will make the View for the customer data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check customer
     * each view is currently visible or invisible.
     */
    private void showCustomerDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the Customer data is visible */
        mRecyclerViewCustomer.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the customer View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check customer
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        /* Then, hide the Customer data */
        mRecyclerViewCustomer.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * This method is for responding to clicks from our list. Implemented from CustomerAdapterListItemClickHandler
     * interface in CustomerAdapter Class
     *
     * @param clickedCustomerId String describing weather details for a particular day
     */
    @Override
    public void onListItemClick(int clickedCustomerId) {
        if(mToast != null)
            mToast.cancel();

        String toastMessage = "Clicked # " + clickedCustomerId + " Customer ";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        mToast.show();

        Intent launchCustomerDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        // Refactor our custom onListItemClick to pass the URI for the clicked customer Id with the Intent
        //Uri uriForDateClicked = CustomerEntry.CONTENT_URI;
        //uriForDateClicked.buildUpon().appendPath(Integer.toString(clickedCustomerId)).build();
        // Above Url formed :content://com.goli.alla.cablecustomer/customer and it was always going to first row

        //Use below
        // Form the content URI that represents the specific pet that was clicked on,
        // by appending the "id" (passed as input to this method) onto the
        // {@link PetEntry#CONTENT_URI}.
        // For example, the URI would be "content://com.goli.alla.cablecustomer/customer/2"
        // if the pet with ID 2 was clicked on.
        Uri uriForDateClicked = ContentUris.withAppendedId(CustomerEntry.CONTENT_URI, clickedCustomerId);
        Log.d(LOG_TAG, "Uri formed :" + uriForDateClicked.toString());
        launchCustomerDetailIntent.setData(uriForDateClicked);
        startActivity(launchCustomerDetailIntent);

    }
}
