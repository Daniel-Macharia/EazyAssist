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
            Command command;
            String []statement;

            while( !CommandQueue.isEmpty() )
            {
                command = CommandQueue.dequeue();
                postToUI("Command = " + command.getCommand() + "\nArg = " + command.getArg());

                if( command.getCommand().equals("") )//if empty command, no need of processing the statement
                    continue;

                statement = command.getStatement();
                String cmd = new String( statement[0] );

                switch( cmd )
                {
                    case "call" :
                        makePhoneCall(statement);
                        break;
                    case "switch", "turn":
                        switch( statement[2] )
                        {
                            case "flashlight", "torch":
                            switchFlashLight(statement);
                            default:
                                wrongCommand(statement);
                        }
                        break;
                    case "create":
                        createContact(statement);
                        break;
                    case "save":
                        createContact(statement);
                        break;
                    case "text":
                        sendSMS(statement);
                        break;
                    case "sms":
                        sendSMS(statement);
                        break;
                    case "message":
                        sendSMS(statement);
                        break;
                    default:
                        wrongCommand(statement);
                }

            }

        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }
    }

    private void makePhoneCall(String []statement)
    {
        try
        {
            postToUI("Making phone call");
            if( statement.length > 1 )
            {
                CallPhone cp = new CallPhone( handler, getAppContext(), statement[1], "");
                cp.callPhone();
            }
            else
            {
                postToUI("Please specify a contact name or number to call!");
            }
        }catch( Exception e )
        {
            postToUI("Error: " + e );
        }
    }
    private void switchFlashLight(String []statement)
    {
        try
        {
            postToUI("Switching flashlight");
            if( statement.length > 1 )
            {
                SwitchFlashLight sf = new SwitchFlashLight(handler, getAppContext());
                sf.switchFlashlight();
            }
            else
            {
                postToUI("Wrong arguements to switch or turn command!");
            }
        }catch( Exception e )
        {
            postToUI("Error: " + e );
        }
    }

    private void sendSMS(String []statement)
    {
        postToUI("sending sms");
    }
    private void createContact(String []statement)
    {
        postToUI("Creating contact");
    }

    private void wrongCommand( String []statement)
    {
        postToUI("Wrong command!");
    }
}
