package com.example.eazy;

public class Command {

    private String command;
    private String arg;

    public Command( String command, String arg)
    {
        this.command = new String(command);
        this.arg = new String(arg);
    }

    public String getCommand(){return this.command;}
    public String getArg(){return this.arg;}
}
