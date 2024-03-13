package com.example.eazy;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Queue;

public class ResponseQueue {

    private static Queue<String> responseQueue = new ArrayDeque<>();

    public static void enqueue(Context context, String response)
    {
        try
        {
            responseQueue.add( response );
            HomeActivity.es.startExecutingCommands();
        }catch( Exception e )
        {
            Toast.makeText( context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public static String dequeue()
    {
        return responseQueue.remove();
    }

    public static boolean isEmpty()
    {
        return responseQueue.isEmpty();
    }
}
