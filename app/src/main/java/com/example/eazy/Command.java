package com.example.eazy;

public class Command {

    private String command;
    private String arg;

    public Command( String command, String arg)
    {
        this.command = command;
        this.arg = arg;
    }

    public String getCommand(){return this.command;}
    public String getArg(){return this.arg;}
}
