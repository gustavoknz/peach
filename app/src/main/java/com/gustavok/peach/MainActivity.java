package com.gustavok.peach;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.gustavok.peach.notification.FirebaseMessageHandler;
import com.gustavok.peach.tabs.CustomTabLayout;
import com.gustavok.peach.tabs.senators.SenatorsListFragment;
import com.gustavok.peach.tabs.voting.VotingFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "*****************************************************************************");
        Log.d(TAG, "********************************* PEACH init ********************************");
        Log.d(TAG, "*****************************************************************************");
        setContentView(R.layout.activity_main);

        RelativeLayout loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        if (!checkConnectivity()) {
            loadingLayout.setVisibility(View.GONE);
            Toast.makeText(this, R.string.noInternetMessage, Toast.LENGTH_LONG).show();
            //TODO Snackbar.make(this, "", Toast.LENGTH_LONG).show();
        }

        SenatorsManager.getInstance().setContext(this);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        SenatorsManager.getInstance().setLoadingViews(loadingLayout, progressBar);
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
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d(TAG, "Subscribed to news topic");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Toast.makeText(this, "MainActivity.extras", Toast.LENGTH_SHORT).show();
            boolean notify = extras.getBoolean("notify");
            if (notify) {
                String body = extras.getString("body");
                FirebaseMessageHandler.handleNotification(this, body);
            } else {
                int id = extras.getInt("id");
                int vote = extras.getInt("vote");
                FirebaseMessageHandler.handleVote(id, vote);
            }
        }
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null) && (netInfo.isConnectedOrConnecting());
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
