package com.gustavok.peach.tabs.voting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.gustavok.peach.R;
import com.gustavok.peach.SenatorsManager;

public class VotingFragment extends Fragment {
    private static final String TAG = "VotingFragment";
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View votingView = inflater.inflate(R.layout.voting_layout, container, false);
        SenatorsManager.getInstance().setVotingView(votingView);
        SenatorsManager.getInstance().updateVotes();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "Sharing on Facebook succeeded");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Sharing on Facebook canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Error sharing on Facebook");
            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "The 'Hello Facebook' sample  showcases simple Facebook integration")
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();

            shareDialog.show(linkContent);
        }
        return votingView;
    }
}
