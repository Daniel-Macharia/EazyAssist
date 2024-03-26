package com.example.eazy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button forgot, login;
    private EditText username, password;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        HomeActivity.es = new EazySpeak(getApplicationContext());

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
        forgot = findViewById( R.id.forgotPassword );

        username = findViewById( R.id.username );
        password = findViewById( R.id.password );


        login.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String name = "", pass = "";
                name = username.getText().toString();
                pass = password.getText().toString();

                User user = new User(getApplicationContext());
                user.open();
                boolean isCorrect = user.credentialsAreCorrect( name, pass);
                user.close();

                if( isCorrect )
                {
                    Intent intent = new Intent( LoginActivity.this, HomeActivity.class);
                    startActivity( intent );
                    finish();
                }

            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, ForgotPassword.class );
                startActivity( intent );
            }
        });
    }
}
