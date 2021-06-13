package com.hcr2bot.coronatracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class indiaHome extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    EditText getState;
    Button search, indiaStates, worldStats;
//    ImageButton updateCounter;
    ProgressDialog dialog;
    TextView confirmed, discharged, deaths;
    ImageView noInternet;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_india_home);
       AdRequest adRequest = new AdRequest.Builder().build();

        // load full screen ad


        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("myapp", "onInterstitialAdLoaded");
                mInterstitialAd.show(indiaHome.this);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("myapp", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });







        AdView adView = findViewById(R.id.adViewIn);
//        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.loadAd(adRequest);
            }

        });

        getState = findViewById(R.id.getStatesIn);
        search = findViewById(R.id.searchStates);
        indiaStates = findViewById(R.id.indiaStates);
        worldStats = findViewById(R.id.WorldStats);
        confirmed = findViewById(R.id.allIndiaCovid);
        discharged = findViewById(R.id.allIndiaRecovered);
        deaths = findViewById(R.id.allIndiaDeaths);
//        updateCounter = findViewById(R.id.updatebtnIn);
        noInternet = findViewById(R.id.noInternetImg1);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        updateCounter();

        checkConnection();

        noInternet.setVisibility(View.GONE);

        indiaStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    FancyToast.makeText(indiaHome.this, "Check your internet connection", FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show();
                } else {

                    Intent intent = new Intent(indiaHome.this, state_view.class);
                    startActivity(intent);
                }
            }
        });

        worldStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(indiaHome.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        updateCounter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateCounter();
//                FancyToast.makeText(indiaHome.this, "Successfully updated the stats", 10, FancyToast.SUCCESS, false).show();
//            }
//        });

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    FancyToast.makeText(indiaHome.this, "Check your internet connection", FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show();
                } else {

                    if (TextUtils.isEmpty(getState.getText().toString())) {
//                    Toast.makeText(MainActivity.this, "Country Name field cannot be empty", Toast.LENGTH_SHORT).show();
                        FancyToast.makeText(indiaHome.this, "State Name field cannot be empty", 5, FancyToast.ERROR, false).show();

                    } else {

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


                                        if (states.getString("loc").toLowerCase().equals(getState.getText().toString().toLowerCase().trim())) {
                                            FancyToast.makeText(indiaHome.this, "Fetching state stats...", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                                            dialog.dismiss();
                                            Intent intent = new Intent(indiaHome.this, india.class);
                                            intent.putExtra("STATE_NAME", getState.getText().toString().toLowerCase());
                                            startActivity(intent);
                                            getState.setText("");
                                            break;
//                                        Log.d("myapp", "onResponse: " + states.getString("loc"));


                                        }


                                    }

                                } catch (JSONException e) {
                                    Log.d("myapp", "onResponse: " + e);
                                    e.printStackTrace();
                                }

                            }


                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                FancyToast.makeText(indiaHome.this, "Something went wrong...", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                getState.setText("");

                            }
                        });

                        requestQueue.add(jsonObjectRequest);
                    }


                }
            }
        });


        // textwatcher

       getState.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

               if (getState.getText().length() == 0){
                   getState.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
               }

               if (TextUtils.isEmpty(getState.getText().toString().trim())){
                   getState.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
               }
               else {
               JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                       "https://api.rootnet.in/covid19-in/stats/", null, new Response.Listener<JSONObject>() {
                   @RequiresApi(api = Build.VERSION_CODES.O)
                   @Override
                   public void onResponse(JSONObject obj) {

                       try {

                           JSONObject jsonObject = obj.getJSONObject("data");

                           JSONArray jsonArray = jsonObject.getJSONArray("regional");

                           for (int i = 0; i < jsonArray.length(); i++) {
                               JSONObject states = jsonArray.getJSONObject(i);


                               if (states.getString("loc").toLowerCase().equals(getState.getText().toString().toLowerCase().trim())) {
                                   getState.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.check_mark, 0);
                               }



                           }

                       } catch (JSONException e) {
                           Log.d("myapp", "onResponse: " + e);
                           e.printStackTrace();
                       }

                   }


               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       getState.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_baseline_close_24, 0);
                   }
               });

               requestQueue.add(jsonObjectRequest);

           }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });





    }



    private void updateCounter() {

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.rootnet.in/covid19-in/stats/", null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject obj) {

                try {

                    JSONObject jsonObject = obj.getJSONObject("data");
                    JSONObject allIndia = jsonObject.getJSONObject("summary");

                    totalConfirmedIn(allIndia.getInt("total"));
                    totalRecoveredIn(allIndia.getInt("discharged"));
                    totalDeathsIn(allIndia.getInt("deaths"));



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                FancyToast.makeText(indiaHome.this, "Something went wrong while updating live counter...", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });

        requestQueue.add(jsonObjectRequest);



    }

    private void totalConfirmedIn(int number) {
        ValueAnimator animator = ValueAnimator.ofInt(0, number);
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }

    private void totalRecoveredIn(int number) {
        ValueAnimator animator = ValueAnimator.ofInt(0, number);
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                discharged.setText(NumberFormat.getInstance().format(Integer.parseInt(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }

    private void totalDeathsIn(int number) {
        ValueAnimator animator = ValueAnimator.ofInt(0, number);
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                deaths.setText(NumberFormat.getInstance().format(Integer.parseInt(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }



    private void checkConnection() {
        // initialize intent filter
        IntentFilter intentFilter =  new IntentFilter();
        // add action
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        // register receiver
        registerReceiver(new ConnectionReceiver(), intentFilter);
        // initialize listener
        ConnectionReceiver.listener = this;

        // initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // get connection status
        boolean isConnected = networkInfo != null &&
                networkInfo.isConnectedOrConnecting();
        //display snack bar
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {
        // initialize color and message;
        String message;
//        Dialog nointernet = new Dialog(this);
//        nointernet.setContentView(R.layout.no_internet);

        ProgressDialog nointernet = new ProgressDialog(this);
        nointernet.setMessage("NO INTERNET");




        // check condition
        if (isConnected) {
            // when internet is connected
            // set message

            updateCounter();
            noInternet.setVisibility(View.GONE);

        }
        else {
            // when internet is disconnected
            FancyToast.makeText(indiaHome.this, "NO INTERNET CONNECTION",FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
            noInternet.setVisibility(View.VISIBLE);

        }



    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        // display snackbar
        showSnackBar(isConnected);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(indiaHome.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}