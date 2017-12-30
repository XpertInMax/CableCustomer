package com.goli.alla.cablecustomer.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.goli.alla.cablecustomer.data.CustomerContract;
import com.goli.alla.cablecustomer.data.CustomerContract.CustomerEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Amani on 28-12-2017.
 */

public class FakeDataUtils {

    private static final String CHAR_LIST =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 10;

    private static String [] firstName = {"Venkat", "Amani", "Avni", "Suryanarayana", "Veera Veni", "Venkat Rao"
    , "Papa Rao", "Subash", "Sunitha", "Viswa Sampreeth", "Sarayu", "Lishika","Vimala", "Anvitha", "Subba Rao"};

    private static String [] lastName = {"Alla", "Alla", "Alla", "Alla", "Alla", "Goli", "Ganta","Alla", "Meka",
            "Goli", "Goli", "Alla", "Goli", "Ganta", "Alla"};
    private static final String PHONE_NUM =
            "01234567889";


    /**
     * Creates a single ContentValues object with random Customer data.
     * @return ContentValues object filled with random weather data
     */
    private static ContentValues createTestCustomerContentValues(int i) {
        ContentValues testCustomerValues = new ContentValues();
        testCustomerValues.put(CustomerEntry.COLUMN_NAME_FIRST, firstName[i]);
        testCustomerValues.put(CustomerEntry.COLUMN_NAME_LAST, lastName[i]);
        testCustomerValues.put(CustomerEntry.COLUMN_NAME_MIDDLE, generateRandomString());
        testCustomerValues.put(CustomerEntry.COLUMN_ADDRESS1, "95 OakWood Vlg");
        testCustomerValues.put(CustomerEntry.COLUMN_ADDRESS2, generateRandomString());
        testCustomerValues.put(CustomerEntry.COLUMN_APT_NUM, "6");
        testCustomerValues.put(CustomerEntry.COLUMN_STATE, "New Jersey");
        testCustomerValues.put(CustomerEntry.COLUMN_ZIPCODE, "Flanders");
        testCustomerValues.put(CustomerEntry.COLUMN_CITY, "Flanders");
        testCustomerValues.put(CustomerEntry.COLUMN_PHONE, generateRandomNumber(10));
        return testCustomerValues;
    }

    /**
     * Creates random weather data for 7 days starting today
     * @param context
     */
    public static void insertFakeData(Context context) {
        List<ContentValues> fakeValues = new ArrayList<ContentValues>();
        //loop over 7 days starting today onwards
        for(int i=0; i< 15; i++) {
            fakeValues.add(FakeDataUtils.createTestCustomerContentValues(i));
        }
        // Bulk Insert our new weather data into Sunshine's Database
        context.getContentResolver().bulkInsert(
                CustomerContract.CustomerEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[7]));
    }

    /**
     * This method generates random string
     * @return
     */
    public static String generateRandomString(){

        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    /**
     * This method generates random string
     * @return
     */
    public static String generateRandomNumber(int length){

        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<length; i++){
            int number = getRandomPhoneNumber();
            char ch = PHONE_NUM.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    /**
     * This method generates random numbers
     * @return int
     */
    private static int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    /**
     * This method generates random numbers
     * @return int
     */
    private static int getRandomPhoneNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(PHONE_NUM.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }
}
