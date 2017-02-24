package co.yodo.fare.helper;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.util.Arrays;
import java.util.Locale;

import co.yodo.fare.R;
import co.yodo.fare.component.AES;

/**
 * Created by luis on 15/12/14.
 * Utilities for the App, Mainly shared preferences
 */
public class PrefUtils {
    /** DEBUG */
    @SuppressWarnings( "unused" )
    private static final String TAG = PrefUtils.class.getSimpleName();

    /**
     * A simple check to see if a string is a valid number before inserting
     * into the shared preferences.
     * @param s The number to be checked.
     * @return true  It is a number.
     *         false It is not a number.
     */
    @SuppressWarnings( "all" )
    public static Boolean isNumber( String s ) {
        try {
            Integer.parseInt( s );
        }
        catch( NumberFormatException e ) {
            return false;
        }
        return true;
    }

    /**
     * A helper class just o obtain the config file for the Shared Preferences
     * using the default values for this Shared Preferences app.
     * @param c The Context of the Android system.
     * @return Returns the shared preferences with the default values.
     */
    private static SharedPreferences getSPrefConfig( Context c ) {
        return c.getSharedPreferences( AppConfig.SHARED_PREF_FILE, Context.MODE_PRIVATE );
    }

    /**
     * Generates the mobile hardware identifier either
     * from the Phone (IMEI) or the Bluetooth (MAC)
     * @param c The Context of the Android system.
     */
    public static String generateHardwareToken( Context c ) {
        String HARDWARE_TOKEN = null;

        TelephonyManager telephonyManager  = (TelephonyManager) c.getSystemService( Context.TELEPHONY_SERVICE );
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if( telephonyManager != null ) {
            String tempMAC = telephonyManager.getDeviceId();
            if( tempMAC != null )
                HARDWARE_TOKEN = tempMAC.replace( "/", "" );
        }

        if( HARDWARE_TOKEN == null && mBluetoothAdapter != null ) {
            if( mBluetoothAdapter.isEnabled() ) {
                String tempMAC = mBluetoothAdapter.getAddress();
                HARDWARE_TOKEN = tempMAC.replaceAll( ":", "" );
            }
        }

        return HARDWARE_TOKEN;
    }

    public static Boolean saveHardwareToken( Context c, String hardwareToken ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putString( AppConfig.SPREF_HARDWARE_TOKEN, hardwareToken );
        return writer.commit();
    }

    public static String getHardwareToken( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        String token = config.getString( AppConfig.SPREF_HARDWARE_TOKEN, "" );
        return ( token.equals( "" ) ) ? null : token;
    }

    /**
     * It gets the language.
     * @param c The Context of the Android system.
     * @return String It returns the language.
     */
    public static String getLanguage( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_CURRENT_LANGUAGE, AppConfig.DEFAULT_LANGUAGE );
    }

    /**
     * It saves the merchant currency to the preferences.
     * @param c The Context of the Android system.
     * @param currency The currency of the merchant.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveMerchantCurrency( Context c, String currency ) {
        // Supported currencies
        final String[] currencies = c.getResources().getStringArray( R.array.currency_array );

        // Get editor
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();

        // Verify if currency exists in the array
        if( Arrays.asList( currencies ).contains( currency ) ) {
            writer.putString( AppConfig.SPREF_MERCHANT_CURRENCY, currency );
            return writer.commit();
        }

        return false;
    }

    /**
     * It gets the merchant currency.
     * @param c The Context of the Android system.
     * @return int It returns the currency.
     */
    public static String getMerchantCurrency( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_MERCHANT_CURRENCY, null );
    }

    /**
     * Returns if the device is advertising
     * @param c The Android application context
     * @return True if it is advertising
     *         False if it is not
     */
    public static Boolean isAdvertising( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_ADVERTISING_SERVICE, false );
    }

    /**
     * It saves the scanner array position to the preferences.
     * @param c The Context of the Android system.
     * @param n The scanner position on the array.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveScanner(Context c, int n) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putInt( AppConfig.SPREF_CURRENT_SCANNER, n );
        return writer.commit();
    }

    /**
     * It gets the scanner position.
     * @param c The Context of the Android system.
     * @return int It returns the scanner position.
     */
    public static int getScanner(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getInt( AppConfig.SPREF_CURRENT_SCANNER, AppConfig.DEFAULT_SCANNER );
    }

    /**
     * It gets the beacon name.
     * @param c The Context of the Android system.
     * @return int It returns the beacon name.
     */
    public static String getBeaconName(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_CURRENT_BEACON, "" );
    }

    /**
     * It saves the password to the preferences.
     * @param c The Context of the Android system.
     * @param s The password.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean savePassword( Context c, String s ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();

        if( s != null ) {
            try {
                String encryptPip = AES.encrypt( s );
                writer.putString( AppConfig.SPREF_CURRENT_PASSWORD, encryptPip );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            writer.remove( AppConfig.SPREF_CURRENT_PASSWORD );
        }

        return writer.commit();
    }

    /**
     * It gets the password if saved.
     * @param c The Context of the Android system.
     * @return int It returns the password or nul.
     */
    public static String getPassword(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        String password = config.getString( AppConfig.SPREF_CURRENT_PASSWORD, null );

        if( password != null ) {
            try {
                password = AES.decrypt( password );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return password;
    }

    /**
     * It saves if it is the first login.
     * @param c The Context of the Android system.
     * @param flag If it is the first login or not.
     * @return true  The flag was saved successfully.
     *         false The flag was not saved successfully.
     */
    public static Boolean saveFirstLogin(Context c, Boolean flag) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean( AppConfig.SPREF_FIRST_LOGIN, flag );
        return writer.commit();
    }

    /**
     * It gets if it is the first login.
     * @param c The Context of the Android system.
     * @return true  It is logged in.
     *         false It is not logged in.
     */
    public static Boolean isFirstLogin(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_FIRST_LOGIN, true );
    }

    /**
     * It gets the status of the advertising service.
     * @param c The Context of the Android system.
     * @return true  Advertising service is on.
     *         false Advertising service is off.
     */
    public static Boolean isAdvertisingServiceRunning(Context c) {
        SharedPreferences config = getSPrefConfig(c);
        return config.getBoolean( AppConfig.SPREF_ADVERTISING_SERVICE, false );
    }

    /**
     * It gets the status of the live scan.
     * @param c The Context of the Android system.
     * @return true  Live scan is on.
     *         false Live scan is off.
     */
    public static Boolean isLiveScan(Context c) {
        SharedPreferences config = getSPrefConfig(c);
        return config.getBoolean( AppConfig.SPREF_LIVE_SCAN, false );
    }

    /**
     * It gets the current time to dismiss the response message
     * @param c The Context of the Android system.
     * @return int It returns the time.
     */
    public static int getDismissTime(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getInt( AppConfig.SPREF_CURRENT_DISMISS_TIME, AppConfig.DEFAULT_DISMISS_TIME );
    }

    /**
     * It gets the different Zones for the old.
     *
     * @param c The Context of the Android system.
     * @param flag The choice between the three zones
     * @return String The value.
     *         null   If it was not possible to get the value.
     */
    public static String getOldFare(Context c, int flag) {
        SharedPreferences config = getSPrefConfig( c );
        String s = null;

        switch( flag ) {
            /* Zone 1 value */
            case AppConfig.ZONE_1:
                s = config.getString( AppConfig.SPREF_FEE_OLD_ZONE_1, AppConfig.DEFAULT_OLD_FEE );
                break;

		    /* Zone 2 value */
            case AppConfig.ZONE_2:
                s = config.getString( AppConfig.SPREF_FEE_OLD_ZONE_2, AppConfig.DEFAULT_OLD_FEE );
                break;

		    /* Zone 3 value */
            case AppConfig.ZONE_3:
                s = config.getString( AppConfig.SPREF_FEE_OLD_ZONE_3, AppConfig.DEFAULT_OLD_FEE );
                break;
        }

        return s;
    }

    /**
     * It gets the different Zones for the adult.
     *
     * @param c The Context of the Android system.
     * @param flag The choice between the three zones
     * @return String The value.
     *         null   If it was not possible to get the value.
     */
    public static String getAdultFare(Context c, int flag) {
        SharedPreferences config = getSPrefConfig( c );
        String s = null;

        switch( flag ) {
            /* Zone 1 value */
            case AppConfig.ZONE_1:
                s = config.getString( AppConfig.SPREF_FEE_ADULT_ZONE_1, AppConfig.DEFAULT_ADULT_FEE );
                break;

		    /* Zone 2 value */
            case AppConfig.ZONE_2:
                s = config.getString( AppConfig.SPREF_FEE_ADULT_ZONE_2, AppConfig.DEFAULT_ADULT_FEE );
                break;

		    /* Zone 3 value */
            case AppConfig.ZONE_3:
                s = config.getString( AppConfig.SPREF_FEE_ADULT_ZONE_3, AppConfig.DEFAULT_ADULT_FEE );
                break;
        }

        return s;
    }

    /**
     * It gets the different Zones for the child.
     *
     * @param c The Context of the Android system.
     * @param flag The choice between the three zones
     * @return String The value.
     *         null   If it was not possible to get the value.
     */
    public static String getChildFare(Context c, int flag) {
        SharedPreferences config = getSPrefConfig( c );
        String s = null;

        switch( flag ) {
            /* Zone 1 value */
            case AppConfig.ZONE_1:
                s = config.getString( AppConfig.SPREF_FEE_CHILD_ZONE_1, AppConfig.DEFAULT_CHILD_FEE );
                break;

		    /* Zone 2 value */
            case AppConfig.ZONE_2:
                s = config.getString( AppConfig.SPREF_FEE_CHILD_ZONE_2, AppConfig.DEFAULT_CHILD_FEE );
                break;

		    /* Zone 3 value */
            case AppConfig.ZONE_3:
                s = config.getString( AppConfig.SPREF_FEE_CHILD_ZONE_3, AppConfig.DEFAULT_CHILD_FEE );
                break;
        }

        return s;
    }

    /**
     * It gets the different Zones for the student.
     *
     * @param c The Context of the Android system.
     * @param flag The choice between the three zones
     * @return String The value.
     *         null   If it was not possible to get the value.
     */
    public static String getStudentFare(Context c, int flag) {
        SharedPreferences config = getSPrefConfig( c );
        String s = null;

        switch( flag ) {
            /* Zone 1 value */
            case AppConfig.ZONE_1:
                s = config.getString( AppConfig.SPREF_FEE_STUDENT_ZONE_1, AppConfig.DEFAULT_STUDENT_FEE );
                break;

		    /* Zone 2 value */
            case AppConfig.ZONE_2:
                s = config.getString( AppConfig.SPREF_FEE_STUDENT_ZONE_2, AppConfig.DEFAULT_STUDENT_FEE );
                break;

		    /* Zone 3 value */
            case AppConfig.ZONE_3:
                s = config.getString( AppConfig.SPREF_FEE_STUDENT_ZONE_3, AppConfig.DEFAULT_STUDENT_FEE );
                break;
        }

        return s;
    }

    /**
     * Gets the bluetooth adapter
     * @return The bluetooth adapter
     */
    private static BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Check if the device possess bluetooth
     * @return true if it possess bluetooth otherwise false
     */
    public static boolean hasBluetooth() {
        return getBluetoothAdapter() != null;
    }

    /**
     * Get the drawable based on the name
     * @param c The Context of the Android system.
     * @param name The name of the drawable
     * @return The drawable
     */
    public static Drawable getDrawableByName( Context c, String name ) {
        Resources resources = c.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", c.getPackageName());
        Drawable image = ContextCompat.getDrawable( c, resourceId );
        int h = image.getIntrinsicHeight();
        int w = image.getIntrinsicWidth();
        image.setBounds( 0, 0, w, h );
        return image;
    }

    public static void setLanguage( Context c ) {
        Locale appLoc = new Locale( getLanguage( c ) );

        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        Locale.setDefault( appLoc );
        Configuration config = new Configuration( res.getConfiguration() );
        config.locale = appLoc;

        res.updateConfiguration( config, dm );
    }
}
