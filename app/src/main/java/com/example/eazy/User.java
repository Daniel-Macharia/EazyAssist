package com.example.eazy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
import android.widget.Toast;

public class User {

    private static final String tableName = "user";
    private static final String userName = "user_name";
    private static final String userPassword = "user_password";
    private static final String userId = "user_id";
    private static final String email = "email_address";

    private static final String dbName = "user_account";

    private static int dbVersion = 1;

    private Context context;
    private DBHelper helper;
    private SQLiteDatabase database;

    private class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper( Context context)
        {
            super( context, dbName, null, dbVersion );
        }

        @Override
        public void onCreate( SQLiteDatabase db )
        {
            db.execSQL("CREATE TABLE " + tableName
                    + " (" + userId + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + userName + " TEXT NOT NULL, "
                    + email + " TEXT NOT NULL,"
                    + userPassword + " TEXT NOT NULL )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
        {

        }

    }

    public User( Context context )
    {
        this.context = context;
    }

    private Context getContext(){return this.context;}

    public User open() throws SQLiteException
    {
        this.helper = new DBHelper( getContext() );
        database = helper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        this.helper.close();
    }

    public long addUser(String username, String mail, String password )
    {
        ContentValues cv = new ContentValues();
        cv.put( userName, username);
        cv.put( email, mail);
        cv.put(userPassword, password);

        return database.insert( tableName, null, cv);
    }

    public String[] getUserDetails()
    {
        String []userData = new String[]{"","",""};

        try {
            String []cols = new String[]{userName, userPassword, email};

            Cursor c = database.query(tableName, cols, null, null, null, null, null);

            int userNameIndex = c.getColumnIndex( userName );
            int userPasswordIndex = c.getColumnIndex( userPassword );
            int userEmailIndex = c.getColumnIndex( email );

            if( c.moveToNext() )
            {
                userData[0] = new String( c.getString( userNameIndex ) );
                userData[1] = new String( c.getString( userPasswordIndex ) );
                userData[2] = new String( c.getString( userEmailIndex ) );
            }

            c.close();
        }catch( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return userData;
    }

    public boolean userExists()
    {
        try
        {
            String []userData = getUserDetails();

            if( !userData[0].equals("") || !userData[1].equals("") )
                return true;
        }catch (Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public boolean credentialsAreCorrect(String username, String password)
    {
        try
        {
            String []userData = getUserDetails();

            if( username.equals( userData[0] ) && password.equals( userData[1] ) )
                return true;
        }catch( Exception e )
        {
            Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public String getEmail()
    {
        String []userDetails = getUserDetails();

        return userDetails[2];
    }

}
