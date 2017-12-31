package com.goli.alla.cablecustomer;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.goli.alla.cablecustomer.data.CustomerContract.CustomerEntry;
import com.goli.alla.cablecustomer.databinding.ActivityEditorBinding;

import static com.goli.alla.cablecustomer.utilities.CustomerUtils.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** Content URI for the existing Customer (null if it's a new Customer) */
    private Uri mCurrentCustomerUri;

    /*
    * This ID will be used to identify the Loader responsible for loading the weather details
    * for a particular day. In some cases, one Activity can deal with many Loaders. However, in
    * our case, there is only one. We will still use this ID to initialize the loader and create
    * the loader for best practice. Please note that 353 was chosen arbitrarily. You can use
    * whatever number you like, so long as it is unique and consistent.
    */
    private static final int ID_DETAIL_LOADER = 353;

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private ActivityEditorBinding mActivityEditorBinding;

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mCustomerHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mCustomerHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            mCustomerHasChanged = true;
            return false;
        }
    };

    /**
     * This method is called when the back button is pressed at the bottom.
     */

    @Override
    public void onBackPressed() {
        // If the Customer hasn't changed, continue with handling back button press at the bottom.
        Log.d(LOG_TAG, "Back button pressed");
        if(!mCustomerHasChanged) {
            super.onBackPressed();
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // User clicked "Discard" button, close the current activity.
                Log.d(LOG_TAG, "Back button pressed : So close the editor activity -- " + which);
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "Keep Editing button pressed : So closing this dialog -- " + which);
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_editor);

        //Set setDisplayHomeAsUpEnabled to true on the support ActionBar
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Instantiate mActivityEditorBinding using DataBindingUtil
        mActivityEditorBinding = DataBindingUtil.setContentView(this, R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intentThatLaunchThisActivity = getIntent();

        mCurrentCustomerUri = intentThatLaunchThisActivity.getData();

        // If the intent DOES NOT contain a customer content URI, then we know that we are
        // creating a new customer.
        if(mCurrentCustomerUri == null){
            // This is a new Customer, so change the app bar to say "Add a Customer"
            setTitle("Add a Customer");
        } else {
            // Otherwise this is an existing Customer, so change app bar to say "Edit Customer"
            Log.d(LOG_TAG, mCurrentCustomerUri.toString());
            setTitle("Edit Customer");
             /* This connects our Activity into the loader lifecycle. */
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mActivityEditorBinding.etFirstName.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etLastName.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etMiddleName.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etAddress1.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etCity.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etState.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etZipCode.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etPhoneNum.setOnTouchListener(mTouchListener);
        mActivityEditorBinding.etAptNum.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save Customer to database
                saveCustomer();
                // Exit activity
                finish();
                return true;
            case android.R.id.home:
                Log.d(LOG_TAG, "Up button pressed on the action bar");
                // If the Customer hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mCustomerHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                                //NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                return new CursorLoader(this, mCurrentCustomerUri, MAIN_CUSTOMER_PROJECTION, null, null, null);
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

        mActivityEditorBinding.etFirstName.setText(cursorData.getString(INDEX_COLUMN_NAME_FIRST));
        mActivityEditorBinding.etMiddleName.setText(cursorData.getString(INDEX_COLUMN_NAME_MIDDLE));
        mActivityEditorBinding.etLastName.setText(cursorData.getString(INDEX_COLUMN_NAME_LAST));
        mActivityEditorBinding.etZipCode.setText(cursorData.getString(INDEX_COLUMN_ZIPCODE));
        mActivityEditorBinding.etAddress1.setText(cursorData.getString(INDEX_COLUMN_ADDRESS1));
        mActivityEditorBinding.etAptNum.setText(cursorData.getString(INDEX_COLUMN_APTNUM));
        mActivityEditorBinding.etCity.setText(cursorData.getString(INDEX_COLUMN_CITY));
        mActivityEditorBinding.etState.setText(cursorData.getString(INDEX_COLUMN_STATE));
        mActivityEditorBinding.etPhoneNum.setText(cursorData.getString(INDEX_COLUMN_PHONE));

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
        // If the loader is invalidated, clear out all the data from the input fields.
        mActivityEditorBinding.etFirstName.setText("");
        mActivityEditorBinding.etMiddleName.setText("");
        mActivityEditorBinding.etLastName.setText("");
        mActivityEditorBinding.etZipCode.setText("");
        mActivityEditorBinding.etAddress1.setText("");
        mActivityEditorBinding.etAptNum.setText("");
        mActivityEditorBinding.etCity.setText("");
        mActivityEditorBinding.etState.setText("");
        mActivityEditorBinding.etPhoneNum.setText("");
    }

    private void saveCustomer(){

        String firstName = mActivityEditorBinding.etFirstName.getText().toString().trim();
        String lastName = mActivityEditorBinding.etMiddleName.getText().toString().trim();
        String middleName = mActivityEditorBinding.etLastName.getText().toString().trim();
        String zipCode = mActivityEditorBinding.etZipCode.getText().toString().trim();
        String address1 = mActivityEditorBinding.etAddress1.getText().toString().trim();
        String aptNum = mActivityEditorBinding.etAptNum.getText().toString().trim();
        String city = mActivityEditorBinding.etCity.getText().toString().trim();
        String state = mActivityEditorBinding.etState.getText().toString().trim();
        String phoneNum = mActivityEditorBinding.etPhoneNum.getText().toString().trim();

        // Check if this is supposed to be a new Customer
        // and check if all the Firs name and Last name fields in the editor are blank
        if (mCurrentCustomerUri == null &&
                TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            // Since no fields were modified, we can return early without creating a new customer.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CustomerEntry.COLUMN_NAME_FIRST, firstName);
        values.put(CustomerEntry.COLUMN_NAME_LAST, lastName);
        values.put(CustomerEntry.COLUMN_NAME_MIDDLE, middleName);
        values.put(CustomerEntry.COLUMN_ADDRESS1, address1);
        values.put(CustomerEntry.COLUMN_CITY, city);
        values.put(CustomerEntry.COLUMN_STATE, state);
        values.put(CustomerEntry.COLUMN_PHONE, phoneNum);
        values.put(CustomerEntry.COLUMN_ZIPCODE, zipCode);
        values.put(CustomerEntry.COLUMN_APT_NUM, aptNum);

        // Determine if this is a new or existing customer by checking if mCurrentCustomerUri is null or not
        if (mCurrentCustomerUri == null) {
            // This is a NEW customer, so insert a new customer into the provider,
            // returning the content URI for the new customer.
            Uri newUri = getContentResolver().insert(CustomerEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_customer_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_customer_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentCustomerUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_customer_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_customer_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
}
