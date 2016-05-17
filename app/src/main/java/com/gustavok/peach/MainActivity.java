package com.gustavok.peach;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.gustavok.peach.tab_senator.SenatorsListFragment;
import com.gustavok.peach.tab_voting.VotingFragment;
import com.gustavok.peach.tabs.CustomTabLayout;

import java.util.Locale;

//TODO: animações pro: "Fora Dilma!", "Tchau, querida!", "+1 coxinha"
//TODO: animações contra: "Golpe não!", "Vai ter luta!", "+1 mortadela"
//TODO: animações neutra: "+1 (Nome do Fulano)", "(miniatura da foto)"
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextHolder.setContext(getApplicationContext());

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
        upVote(view, R.id.voting_count_yes, R.id.voting_animation_yes);
    }

    public void noTapped(View view) {
        upVote(view, R.id.voting_count_no, R.id.voting_animation_no);
    }

    public void abstentionTapped(View view) {
        upVote(view, R.id.voting_count_abstention, R.id.voting_animation_abstention);
    }

    public void absenceTapped(View view) {
        upVote(view, R.id.voting_count_absence, R.id.voting_animation_absence);
    }

    private void upVote(View view, int votingCountId, int votingAnimationId) {
        TextView tv = (TextView) view.findViewById(votingCountId);
        int count = Integer.parseInt(tv.getText().toString());
        tv.setText(String.format(Locale.getDefault(), "%d", ++count));

        TextView tvAnimation = (TextView) view.getRootView().findViewById(votingAnimationId);
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
