package com.gustavok.peach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.gustavok.peach.notification.RegistrationIntentService;
import com.gustavok.peach.tabs.CustomTabLayout;
import com.gustavok.peach.tabs.senators.SenatorsListFragment;
import com.gustavok.peach.tabs.voting.VotingFragment;

//TODO: animações pro: "Fora Dilma!", "Tchau, querida!", "+1 coxinha"
//TODO: animações contra: "Golpe não!", "Vai ter luta!", "+1 mortadela"
//TODO: animações neutra: "+1 (Nome do Fulano)", "(miniatura da foto)"
public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "*****************************************************************************");
        Log.d(TAG, "********************************* PEACH init ********************************");
        Log.d(TAG, "*****************************************************************************");
        setContentView(R.layout.activity_main);

        if (!checkConnectivity()) {
            Toast.makeText(this, R.string.noInternetMessage, Toast.LENGTH_LONG).show();
            //TODO Snackbar.make(this, "", Toast.LENGTH_LONG).show();
        }

        SenatorsManager.getInstance().setContext(getApplicationContext());
        SenatorsManager.getInstance().init();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        CustomTabLayout tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        // Notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.d(TAG, "Device registered successfully in GCM");
                } else {
                    Log.d(TAG, "Failed registering in GCM");
                }
            }
        };
        registerReceiver();

        boolean playServices = checkPlayServices();
        Log.d(TAG, "GCM playServices = " + playServices);
        if (playServices) {
            Log.d(TAG, "GCM calling RegistrationIntentService");
            // Start IntentService to register this application with GCM
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        Log.d(TAG, "GCM isReceiverRegistered? " + isReceiverRegistered);
        if (!isReceiverRegistered) {
            Log.d(TAG, "GCM registering...");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "GCM This device is supported.");
            } else {
                Log.i(TAG, "GCM This device is NOT supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void shareIt(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private static final int TOTAL_PAGES_COUNT = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return new VotingFragment();
            } else {
                return new SenatorsListFragment();
            }
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.page_title_voting);
            } else {
                return getString(R.string.page_title_senators);
            }
        }
    }
}
