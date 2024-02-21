package com.example.eazy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.content.Context;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int num;

    private Context context;
    public PagerAdapter(FragmentManager fm, int num, Context context)
    {
        super(fm, num);
        this.num = num;
        this.context = context;
    }
    
    public Context getContext()
    {
        return this.context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if( position == 1 )
        {
            return new AppFunctionsFragment( getContext() );
        }
        else {
            return new RecognizeVoice( getContext() );
        }
    }

    @Override
    public int getCount() {
        return this.num;
    }
}
