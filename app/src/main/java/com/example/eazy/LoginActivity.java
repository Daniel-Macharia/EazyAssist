package com.example.eazy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button forgot, login;//, signup;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        User user = new User( getApplicationContext() );
        user.open();
        boolean exists = user.userExists();
        user.close();

        if( ! exists )
        {
            Intent intent = new Intent( LoginActivity.this, SignUpActivity.class );
            startActivity( intent );
            finish();
        }

        setContentView(R.layout.activity_login);


        login = findViewById( R.id.login );
        //signup = findViewById( R.id.signUp );
        forgot = findViewById( R.id.forgotPassword );


        login.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String name = "";
                String pass = "";

                User user = new User(getApplicationContext());
                user.open();
                if( user.credentialsAreCorrect( name, pass) )
                    Toast.makeText(getApplicationContext(), "User Exists!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "User does not exist!", Toast.LENGTH_SHORT).show();
                user.close();
                Intent intent = new Intent( LoginActivity.this, HomeActivity.class);
                startActivity( intent );
                finish();
            }
        });

        /*signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, SignUpActivity.class);
                startActivity( intent );
            }
        });*/

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, ForgotPassword.class );
                startActivity( intent );
            }
        });
    }
}
