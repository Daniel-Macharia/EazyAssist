package com.example.eazy;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.widget.ActionBarContextView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class SendNotification {
    private String message;
    private Context context;

    public SendNotification( Context context, String message)
    {
        this.context = context;
        this.message = message;
    }


    private Context getAppContext(){return this.context;}

    public void sentNotification()
    {
        try
        {


            int notificationId = 444;
            String channelId = "EazyChannel";
            String name = "Eazy";

            NotificationManager manager = (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
            {
                NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getAppContext(), channelId)
                    .setContentTitle("Eazy")
                    .setContentText("Listening...")
                    .setSmallIcon( R.drawable.ic_launcher_foreground);

            if(ActivityCompat.checkSelfPermission( getAppContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED )
            {
                Toast.makeText(getAppContext(), "Denied Permission to post notifications", Toast.LENGTH_SHORT).show();
            }

            manager.notify( notificationId, builder.build());


        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
