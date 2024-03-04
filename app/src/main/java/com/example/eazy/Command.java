package com.example.eazy;

public class Command {

    private String []statement;

    public Command( String []statement)
    {
        this.statement = statement.clone();
    }

    public String getCommand(){return statement[0];}

    public String[] getStatement(){return statement;}
    public String getArg()
    {
        String arg = "";

        for( int i = 1; i < statement.length; i++ )
        {
            arg += statement[i] + " ";
        }

        return arg;
    }
}
