package com.goli.alla.cablecustomer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goli.alla.cablecustomer.MainActivity;
import com.goli.alla.cablecustomer.R;
import com.goli.alla.cablecustomer.data.CustomerContract.CustomerEntry;

/**
 * Created by Amani on 28-12-2017.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerAdapterViewHolder>{

    /** Tag for the log messages */
    public static final String LOG_TAG = CustomerAdapter.class.getSimpleName();

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    private Cursor mCursor;

    /*
    * Below, we've defined an interface to handle clicks on items within this Adapter. In the
    * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
    * said interface. We store that instance in this variable to call the onClick method whenever
    * an item is clicked in the list.
    */
    final private CustomerAdapterListItemClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface CustomerAdapterListItemClickHandler {
        void onListItemClick(int clickedCustomerId);
    }

    /**
     * Creates a ForecastAdapter.
     *
     * @param context Used to talk to the UI and app resources
     * @param clickHandler
     *
     */
    public CustomerAdapter(@NonNull Context context, CustomerAdapterListItemClickHandler clickHandler) {
        mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new CustomerAdapterViewHolder that holds the View for each list item
     */
    @Override
    public CustomerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(LOG_TAG , "onCreateViewHolder : called " );
        int customerListItemLayout = R.layout.customer_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater.inflate(customerListItemLayout, viewGroup, shouldAttachToParentImmediately);
        CustomerAdapterViewHolder customerAdapterViewHolder = new CustomerAdapterViewHolder(view);

        return customerAdapterViewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the Customer
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param customerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CustomerAdapterViewHolder customerAdapterViewHolder, int position) {
        Log.d(LOG_TAG , "onBindViewHolder : called " );
        mCursor.moveToPosition(position);

        customerAdapterViewHolder.mFirstName.setText(mCursor.getString(MainActivity.INDEX_COLUMN_NAME_FIRST));
        customerAdapterViewHolder.mLastName.setText(mCursor.getString(MainActivity.INDEX_COLUMN_NAME_LAST));
        //customerAdapterViewHolder.mMiddleName.setText(mCursor.getString(MainActivity.INDEX_COLUMN_NAME_MIDDLE));
        customerAdapterViewHolder.mAddress1.setText(mCursor.getString(MainActivity.INDEX_COLUMN_ADDRESS1));
        customerAdapterViewHolder.mAptNum.setText(mCursor.getString(MainActivity.INDEX_COLUMN_APTNUM));
        customerAdapterViewHolder.mCity.setText(mCursor.getString(MainActivity.INDEX_COLUMN_CITY));
        customerAdapterViewHolder.mState.setText(mCursor.getString(MainActivity.INDEX_COLUMN_STATE));

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        Log.d(LOG_TAG , "getItemCount : called " );
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class CustomerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView mFirstName;
        final TextView mLastName;
        //final TextView mMiddleName;
        final TextView mAddress1;
        final TextView mCity;
        final TextView mState;
        final TextView mAptNum;

        public CustomerAdapterViewHolder(View itemView) {
            super(itemView);
            Log.d(LOG_TAG , "CustomerAdapterViewHolder : called " );
            mFirstName = itemView.findViewById(R.id.tv_firstName);
            mLastName = itemView.findViewById(R.id.tv_lastName);
           // mMiddleName = itemView.findViewById(R.id.tv_middleName);
            mAddress1 = itemView.findViewById(R.id.tv_address1);
            mCity = itemView.findViewById(R.id.tv_city);
            mState = itemView.findViewById(R.id.tv_state);
            mAptNum = itemView.findViewById(R.id.tv_aptnum);

           itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int customerId = mCursor.getInt(MainActivity.INDEX_COLUMN_ID);
            mClickHandler.onListItemClick(customerId);
        }
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
