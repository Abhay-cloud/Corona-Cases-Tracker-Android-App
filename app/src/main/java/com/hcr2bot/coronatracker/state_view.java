package com.hcr2bot.coronatracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class state_view extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    List<StateList> stateLists;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_view);


        recyclerView = (RecyclerView) findViewById(R.id.recycleViewContainer);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        stateLists = new ArrayList<>();

        dialog = new ProgressDialog(this);

        dialog.setMessage("Fetching states data...");
        dialog.setCancelable(false);
        dialog.show();

        sendRequest();

    }


    public void sendRequest() {
        RequestQueue rq;
        rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://api.rootnet.in/covid19-in/stats/", null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject obj) {


                JSONObject jsonObject = null;
                try {
                    jsonObject = obj.getJSONObject("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("regional");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                    Log.d("myapp", "onResponse: " + jsonArray);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        StateList stateList = new StateList();

                        dialog.dismiss();

                        JSONObject states = null;
                        try {
                            states = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            stateList.setStateName(states.getString("loc"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            stateList.setConfirmedCases(states.getInt("totalConfirmed"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        stateLists.add(stateList);

                    }

                mAdapter = new CustomRecyclerAdapter(state_view.this, stateLists);

                recyclerView.setAdapter(mAdapter);


            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("myapp", "Something went wrong");
                FancyToast.makeText(state_view.this, "Something went wrong", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });

        rq.add(jsonObjectRequest);
    }
}


