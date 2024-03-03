package com.example.eazy;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Queue;

public class CommandQueue {

    private static Queue<Command> commandQueue = new ArrayDeque<>();

    public static void enqueue(Context context, Command command )
    {
        try
        {
            commandQueue.add( command );
            MainActivity.ec.startExecutingCommands();
        }catch ( Exception e )
        {
            Toast.makeText( context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public static Command dequeue()
    {
        Command head = commandQueue.remove();

        return head;
    }

    public static boolean isEmpty()
    {
        return commandQueue.isEmpty();
    }
}
