package com.hcr2bot.coronatracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {
    Button button, indiaStats;
//    ImageButton updatebtn;
    EditText enterCountry;
    ProgressDialog dialog, dialog1;
    TextView totalCovid, totalRecovered, totalDeaths;
    ImageView noInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // banner ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
//
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.loadAd(adRequest);
            }

        });


        button = findViewById(R.id.searchCountry);
        enterCountry = findViewById(R.id.enterCountryName);
        totalCovid = findViewById(R.id.totalCovid);
        totalRecovered = findViewById(R.id.totalCovidRecovered);
        totalDeaths = findViewById(R.id.totalCovidDeaths);
//        updatebtn = findViewById(R.id.updatebtn);
        indiaStats = findViewById(R.id.IndiaStats);
        noInternet = findViewById(R.id.noInternetImg);

        dialog = new ProgressDialog(this);
        dialog1 = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);


//        // connection code
//        ConnectivityManager cm =
//                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//
//        if(!isConnected){
//            FancyToast.makeText(MainActivity.this, "Check your internet connection", 8, FancyToast.WARNING, false).show();
//        }

        noInternet.setVisibility(View.GONE);
        checkConnection();




        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

       updateCounter();


       indiaStats.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, CountryList.class);
               startActivity(intent);
               finish();
           }
       });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterCountry.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    FancyToast.makeText(MainActivity.this, "Check your internet connection", FancyToast.LENGTH_LONG, FancyToast.WARNING, false).show();
                } else {

                    if (TextUtils.isEmpty(enterCountry.getText().toString())) {
//                    Toast.makeText(MainActivity.this, "Country Name field cannot be empty", Toast.LENGTH_SHORT).show();
                        FancyToast.makeText(MainActivity.this, "Country Name field cannot be empty", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                    } else {
                        dialog.show();

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                "https://corona.lmao.ninja/v2/countries/" + enterCountry.getText().toString(), null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject obj) {

                                FancyToast.makeText(MainActivity.this, "Fetching country stats...", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                                Intent intent = new Intent(MainActivity.this, CovidActivity.class);
                                intent.putExtra("COUNTRY NAME", enterCountry.getText().toString().trim());
                                startActivity(intent);
                                dialog.dismiss();

                                enterCountry.setText("");


                            }


                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
//                            Log.d("myapp", "Something went wrong "+ error.getMessage());
//                            Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                FancyToast.makeText(MainActivity.this, "Country not found", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                enterCountry.setText("");
                            }
                        });

                        requestQueue.add(jsonObjectRequest);


                    }
                }

            }

        });


        // textWatcher

        enterCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(MainActivity.this, "Before text changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (enterCountry.getText().length() == 0){
                    enterCountry.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }

                if (TextUtils.isEmpty(enterCountry.getText().toString().trim())){
                        enterCountry.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
                else {

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                            "https://corona.lmao.ninja/v2/countries/" + enterCountry.getText().toString(), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject obj) {

//                            checkCountry.setImageResource(R.drawable.check_mark);
                            enterCountry.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.check_mark,0);



                        }



                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, "NO", Toast.LENGTH_SHORT).show();

//                            checkCountry.setImageResource(R.drawable.ic_baseline_close_24);
                                enterCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close_24, 0);
                            
                        }
                    });

                    requestQueue.add(jsonObjectRequest);

                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });









//        updatebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConnectivityManager cm =
//                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//
//                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                boolean isConnected1 = activeNetwork != null &&
//                        activeNetwork.isConnectedOrConnecting();
//
//                if (!isConnected1) {
//                    FancyToast.makeText(MainActivity.this, "Check your internet connection", 8, FancyToast.WARNING, false).show();
//                } else {
//
//                    updateCounter();
//                    FancyToast.makeText(MainActivity.this, "Successfully updated the stats", 10, FancyToast.SUCCESS, false).show();
//
//                }
//            }
//        });



    }




    public void updateCounter(){
        RequestQueue requestQueue1;
        requestQueue1= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET,
                "https://corona.lmao.ninja/v3/covid-19/all", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    totalCovidAni(obj.getInt("cases"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    totalRecoveredAni(obj.getInt("recovered"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    totalDeathsAni(obj.getInt("deaths"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d("myapp", "Something went wrong");
//                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue1.add(jsonObjectRequest1);
    }




    private void totalCovidAni(int number) {
        ValueAnimator animator = ValueAnimator.ofInt(0, number);
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
               totalCovid.setText(NumberFormat.getInstance().format(Integer.parseInt(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }

    private void totalRecoveredAni(int number) {
        ValueAnimator animator = ValueAnimator.ofInt(0, number);
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
               totalRecovered.setText(NumberFormat.getInstance().format(Integer.parseInt(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }

    private void totalDeathsAni(int number) {
        ValueAnimator animator = ValueAnimator.ofInt(0, number);
        animator.setDuration(8000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
               totalDeaths.setText(NumberFormat.getInstance().format(Integer.parseInt(animation.getAnimatedValue().toString())));
            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

//            nointernet.dismiss();
//            nointernet.setCancelable(true);

//            FancyToast.makeText(MainActivity.this, "Successfully connected to the Internet",FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

            updateCounter();
            noInternet.setVisibility(View.GONE);


        }
        else {
            // when internet is disconnected
            FancyToast.makeText(MainActivity.this, "NO INTERNET CONNECTION",FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();

            noInternet.setVisibility(View.VISIBLE);
//            nointernet.show();
//            nointernet.setCancelable(false);

        }
      


    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        // display snackbar
        showSnackBar(isConnected);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // call method
//        checkConnection();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        checkConnection();
//    }


}