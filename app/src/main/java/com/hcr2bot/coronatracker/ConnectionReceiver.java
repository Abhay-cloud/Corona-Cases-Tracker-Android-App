package com.hcr2bot.coronatracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.recyclerview.widget.RecyclerView;

public class ConnectionReceiver extends BroadcastReceiver {

    public static ReceiverListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // initialize connectivity manager
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // initialize network info
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //check connection
        if (listener != null){
            // when connectivity listener not null
            // get connection status
            boolean isConnected = networkInfo != null &&
                networkInfo.isConnectedOrConnecting();
            // call listener method
            listener.onNetworkChange(isConnected);

        }
    }

    public interface  ReceiverListener{
        // create method
        void onNetworkChange(boolean isConnected);
    }
}
