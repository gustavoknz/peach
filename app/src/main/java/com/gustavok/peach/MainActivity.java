package com.gustavok.peach;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

//TODO: animações pro: "Fora Dilma!", "Tchau, querida!", "+1 coxinha"
//TODO: animações contra: "Golpe não!", "Vai ter luta!", "+1 mortadela"
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    public void yesTapped(View view) {
        TextView tvYes = (TextView) view.findViewById(R.id.voting_count_yes);
        int yesCount = Integer.parseInt(tvYes.getText().toString());
        tvYes.setText(String.format(Locale.getDefault(), "%d", ++yesCount));

        TextView tvAnimation = (TextView) view.getRootView().findViewById(R.id.voting_animation_yes);
        Animation alphaAnimation = new AlphaAnimation(1, 0.5F);
        alphaAnimation.setFillAfter(false);
        tvAnimation.setAnimation(alphaAnimation);
        alphaAnimation.setDuration(3000);
        tvAnimation.startAnimation(alphaAnimation);
    }

    public void noTapped(View view) {
        TextView tvNo = (TextView) view.findViewById(R.id.voting_count_no);
        int noCount = Integer.parseInt(tvNo.getText().toString());
        tvNo.setText(String.format(Locale.getDefault(), "%d", ++noCount));

        TextView tvAnimation = (TextView) view.getRootView().findViewById(R.id.voting_animation_no);
        Animation translateAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, -0.5f);
        translateAnimation.setFillAfter(false);
        tvAnimation.setAnimation(translateAnimation);
        translateAnimation.setDuration(2000);
        tvAnimation.startAnimation(translateAnimation);
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
