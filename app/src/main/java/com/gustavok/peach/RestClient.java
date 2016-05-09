package com.gustavok.peach;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public final class RestClient {
    private static final int PORT = 2002;
    private static final String BASE_URL = "http://ojur.com.br";
    private static final String TAG = "RestClient";

    private static final AsyncHttpClient CLIENT = new AsyncHttpClient(PORT);

    private static String getAbsoluteUrl(String method) {
        return BASE_URL + method;
    }

    public static void getAllVotes(final View votingView, final Context context) {
        RequestParams params = new RequestParams("info", "");
        getSenatorsList(params, votingView, context);
    }

    private static void getSenatorsList(RequestParams params, final View votingView, final Context context) {
        // SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            private static final int VOTE_POSITION_YES = 0;
            private static final int VOTE_POSITION_NO = 1;
            private static final int VOTE_POSITION_ABSTINENT = 2;
            private static final int VOTE_POSITION_ABSENCE = 3;
            private static final int VOTE_POSITION_UNKNOWN = 4;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, String.format("Received a response: %s", response));
                try {
                    JSONArray jsonArray = response.getJSONArray("senadores");
                    Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
                    updateVotes(senatorsArray, votingView, context);
                    Log.d(TAG, String.format(Locale.getDefault(), "Got %d senators as response", senatorsArray.length));
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("Failure 1. statusCode: %d; errorResponse: %s", statusCode, responseString), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("Failure 2. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }

            private void updateVotes(Senator[] senatorVotes, View votingView, Context context) {
                Log.d(TAG, "Updating votes...");
                SenatorsManager.getInstance().setSenatorVotes(senatorVotes);
                int[] votes = countVotes(senatorVotes);
                int countYes = votes[VOTE_POSITION_YES];
                int countNo = votes[VOTE_POSITION_NO];
                int countAbstention = votes[VOTE_POSITION_ABSTINENT];
                int countAbsence = votes[VOTE_POSITION_ABSENCE];
                int countUnknown = votes[VOTE_POSITION_UNKNOWN];
                int total = countYes + countNo + countAbstention + countAbsence + countUnknown;

                TextView tvYes = (TextView) votingView.findViewById(R.id.voting_count_yes);
                TextView tvNo = (TextView) votingView.findViewById(R.id.voting_count_no);
                TextView tvAbstention = (TextView) votingView.findViewById(R.id.voting_count_abstention);
                TextView tvAbsence = (TextView) votingView.findViewById(R.id.voting_count_absence);
                Log.d(TAG, String.format("Updating senatorVotes to (%d) yes=%d; no=%d; abstention=%d; absence:%d; unknown:%d",
                        total, countYes, countNo, countAbstention, countAbsence, countUnknown));
                tvYes.setText(String.format(Locale.getDefault(), "%d", countYes));
                tvNo.setText(String.format(Locale.getDefault(), "%d", countNo));
                tvAbstention.setText(String.format(Locale.getDefault(), "%d", countAbstention));
                tvAbsence.setText(String.format(Locale.getDefault(), "%d", countAbsence));

                if (total >= VotingSingleton.TOTAL_VOTES) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Votação encerrada");
                    alertDialogBuilder
                            .setCancelable(true)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    if (countYes > countNo + countAbstention + countAbsence + countUnknown) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, VotingSingleton.REMOVED_DAYS);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String dateOut = sdf.format(calendar.getTime());

                        alertDialogBuilder.setMessage("A Dilma será afastada do seu cargo e terá seu julgamento final até o dia " + dateOut);
                    } else {
                        alertDialogBuilder.setMessage("Este processo será arquivado.");
                    }
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            private int[] countVotes(Senator[] senators) {
                int[] count = new int[5];
                for (Senator s : senators) {
                    if ("0".equals(s.getVoto())) {
                        ++count[VOTE_POSITION_YES];
                    } else if ("1".equals(s.getVoto())) {
                        ++count[VOTE_POSITION_NO];
                    } else if ("2".equals(s.getVoto())) {
                        ++count[VOTE_POSITION_ABSTINENT];
                    } else if ("3".equals(s.getVoto())) {
                        ++count[VOTE_POSITION_ABSENCE];
                    } else if ("4".equals(s.getVoto())) {
                        ++count[VOTE_POSITION_UNKNOWN];
                    }
                }
                return count;
            }
        };
        //jsonHttpResponseHandler.setUseSynchronousMode(true);

        String method = "/senadores";
        CLIENT.get(getAbsoluteUrl(method), params, jsonHttpResponseHandler);
    }
}
