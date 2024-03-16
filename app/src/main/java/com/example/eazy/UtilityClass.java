package com.example.eazy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

public class UtilityClass {

    public static boolean networkIsAvailable(Handler handler, Context context)
    {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );

            Network net = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                net = cm.getActiveNetwork();
            }

            if( net != null )
            {
                NetworkCapabilities netCaps = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    netCaps = cm.getNetworkCapabilities( net );
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if( netCaps.hasCapability( NetworkCapabilities.NET_CAPABILITY_VALIDATED ) )
                        return true;
                }

            }
            else
            {
                NetworkInfo info = cm.getActiveNetworkInfo();

                if( info != null )
                {
                    if( info.isAvailable() )
                        return true;
                }
            }
        }catch( Exception e )
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }

        return false;
    }

}
