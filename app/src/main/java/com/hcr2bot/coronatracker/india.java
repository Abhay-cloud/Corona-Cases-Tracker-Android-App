package com.hcr2bot.coronatracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.JsonArray;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;

public class india extends AppCompatActivity {

    TextView sname, updatedField, totalConfirmed, discharged, deaths;
    PieChart pieChart;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_india);

        loadAd();

        sname = findViewById(R.id.sname);
        updatedField = findViewById(R.id.dateFieldState);
        totalConfirmed = findViewById(R.id.totalConfirmedState);
        discharged = findViewById(R.id.totalRecoveredState);
        deaths = findViewById(R.id.totalDeathsState);
        pieChart = findViewById(R.id.pieChart2);


        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        String stateName = intent.getStringExtra("STATE_NAME");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.rootnet.in/covid19-in/stats/", null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject obj) {

                try {

                    JSONObject jsonObject = obj.getJSONObject("data");

                    JSONArray jsonArray = jsonObject.getJSONArray("regional");

//                    Log.d("myapp", "onResponse: " + jsonArray);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject states = jsonArray.getJSONObject(i);

                        if (states.getString("loc").toLowerCase().equals(stateName)) {
//                            Log.d("myapp", "onResponse: " + states.getInt("totalConfirmed"));

                            setUpdatedField(obj.getString("lastRefreshed"));

                            Integer totalCon = states.getInt("totalConfirmed");
                            Integer totalDis = states.getInt("discharged");
                            Integer totalDea = states.getInt("deaths");

                            sname.setText(states.getString("loc"));
                            totalConfirmed.setText(NumberFormat.getInstance().format(totalCon));
                            discharged.setText(NumberFormat.getInstance().format(totalDis));
                            deaths.setText(NumberFormat.getInstance().format(totalDea));

                            pieChart.addPieSlice(new PieModel("Confirmed", totalCon, getResources().getColor(R.color.yellow)));
                            pieChart.addPieSlice(new PieModel("Discharged", totalDis, getResources().getColor(R.color.green_pie)));
                            pieChart.addPieSlice(new PieModel("Deaths", totalDea, getResources().getColor(R.color.red_pie)));

                            pieChart.startAnimation();


                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "Something went wrong");

            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpdatedField(String updated) {

        long millisFromEpoch = Instant.parse(updated).toEpochMilli();

        DateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

        long milliseconds = Long.parseLong(String.valueOf(millisFromEpoch));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        updatedField.setText("Updated at " + format.format(calendar.getTime()));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(india.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public void loadAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        // load full screen ad

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("myapp", "onInterstitialAdLoadedIndia");
//                mInterstitialAd.show(india.this);


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("myapp", loadAdError.getMessage());
                mInterstitialAd = null;

            }
        });



    }


}
