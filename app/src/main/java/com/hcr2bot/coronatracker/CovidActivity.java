package com.hcr2bot.coronatracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CovidActivity extends AppCompatActivity {

    TextView countryName, updatedField,totalConfirmed, totalActive, totalRecovered, totalDeaths, totalCases;
    TextView todayConfirmed, todayRecovered, todayDeaths;
    PieChart pieChart;
    ProgressDialog dialog;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid);


        AdRequest adRequest = new AdRequest.Builder().build();
        // load full screen ad


        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("myapp", "onInterstitialAdLoaded");
                mInterstitialAd.show(CovidActivity.this);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("myapp", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });





        countryName = findViewById(R.id.cname);
        updatedField = findViewById(R.id.dateField);
        todayConfirmed = findViewById(R.id.todayConfirmed);
        totalConfirmed = findViewById(R.id.totalConfirmed);
        totalActive = findViewById(R.id.totalActive);
        totalRecovered = findViewById(R.id.totalRecovered);
        todayRecovered = findViewById(R.id.todayRecovered);
        totalDeaths = findViewById(R.id.totalDeaths);
        todayDeaths = findViewById(R.id.todayDeaths);
        totalCases = findViewById(R.id.totalTests);
        pieChart = findViewById(R.id.pieChart);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Intent intent = getIntent();
        String CNAME = intent.getStringExtra("COUNTRY NAME");


        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://corona.lmao.ninja/v2/countries/" + CNAME , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {

                try {

                    dialog.dismiss();

                    Integer confirmed = obj.getInt("cases");
                    Integer active = obj.getInt("active");
                    Integer recovered = obj.getInt("recovered");
                    Integer deaths = obj.getInt("deaths");

                    setUpdatedField(obj.getString("updated"));

                    countryName.setText(obj.getString("country"));

                    totalConfirmed.setText(String.valueOf(NumberFormat.getInstance().format(confirmed)));
                    totalActive.setText(String.valueOf(NumberFormat.getInstance().format(active)));
                    totalRecovered.setText(String.valueOf(NumberFormat.getInstance().format(recovered)));
                    totalDeaths.setText(String.valueOf(NumberFormat.getInstance().format(deaths)));
                    totalCases.setText(NumberFormat.getInstance().format(obj.getInt("cases")));
                    todayConfirmed.setText(NumberFormat.getInstance().format(obj.getInt("todayCases")));
                    todayRecovered.setText(NumberFormat.getInstance().format(obj.getInt("todayRecovered")));
                    todayDeaths.setText(NumberFormat.getInstance().format(obj.getInt("todayDeaths")));

                    pieChart.addPieSlice(new PieModel("Confirmed", confirmed, getResources().getColor(R.color.yellow)));
                    pieChart.addPieSlice(new PieModel("Recovered", recovered, getResources().getColor(R.color.green_pie)));
                    pieChart.addPieSlice(new PieModel("Active", active, getResources().getColor(R.color.blue_pie)));
                    pieChart.addPieSlice(new PieModel("Deaths", deaths, getResources().getColor(R.color.red_pie)));

                    pieChart.startAnimation();


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
    private void setUpdatedField(String updated) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        updatedField.setText("Updated at " + format.format(calendar.getTime()));

    }


    }
    
