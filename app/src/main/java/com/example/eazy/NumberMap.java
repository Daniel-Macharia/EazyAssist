package com.example.eazy;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NumberMap {
    private String []name = {"zero",
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine"
    };
    private Context context;
    private ArrayList<Pair> numList = new ArrayList<>(10);

    public NumberMap(Context context)
    {
        this.context = context;
        int value = 0;
        for( String n : name )
        {
            numList.add( new Pair(n, value++) );
        }
    }

    public int getNumber( String num )
    {
        for( Pair pair : numList )
        {
            if( pair.name.equals( num ) )
                return pair.number;
        }

        return -1;
    }

    private Context getAppContext(){return this.context;}

    public void display()
    {
        String s = "";
        for( Pair p : numList )
        {
            s += p.name + " - " + p.number + "\n";
        }

        Toast.makeText( getAppContext(), s, Toast.LENGTH_SHORT).show();
    }
}

class Pair
{
    public String name;
    public int number;

    public Pair( String name, int number)
    {
        this.name = new String( name );
        this.number = number;
    }
    public Pair(@NonNull Pair pair)
    {
        this.name = new String( pair.name);
        this.number = pair.number;
    }
}