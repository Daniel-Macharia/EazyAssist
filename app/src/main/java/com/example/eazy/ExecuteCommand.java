package com.example.eazy;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ExecuteCommand implements Runnable {

    private Thread thread;

    private Context context;
    private Handler handler;

    public ExecuteCommand( Context context )
    {
        this.context = context;
        handler = new Handler( Looper.getMainLooper());
    }

    private void postToUI( String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startExecutingCommands()
    {
        try
        {
            if( thread == null )
            {
                thread = new Thread( this );
                thread.start();
            }
            else
            {
                if( !thread.isAlive() )
                {
                    thread = null;

                    thread = new Thread( this );
                    thread.start();
                }
            }

        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }
    }

    private Context getAppContext(){return this.context;}

    @Override
    public void run()
    {
        try
        {
            while( !CommandQueue.isEmpty() )
            {
                postToUI(CommandQueue.dequeue().getArg());
            }

        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }
    }
}
