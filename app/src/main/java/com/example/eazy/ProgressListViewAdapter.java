package com.example.eazy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProgressListViewAdapter extends ArrayAdapter<String> {

    private Context context;
    public ProgressListViewAdapter( Context context, ArrayList<String> list)
    {
        super( context, 0, list);
        this.context = context;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent)
    {
        View currentView = convertView;

        try
        {
            if( currentView == null )
            {
                currentView = LayoutInflater.from( getContext() ).inflate( R.layout.message_layout, parent, false);
            }

            String current = getItem( position );

            assert current != null;
            TextView message = currentView.findViewById( R.id.message );

            message.setText(current);
        }catch( Exception e )
        {
            Toast.makeText( context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }


        return currentView;
    }

    public void update()
    {
        this.notifyDataSetChanged();
    }
}
