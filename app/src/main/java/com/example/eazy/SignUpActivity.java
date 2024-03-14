package com.example.eazy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText username, email, password, confirm;
    private Button signup;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup );


        signup = findViewById( R.id.signup );

        username = findViewById( R.id.username );
        email = findViewById( R.id.email );
        password = findViewById( R.id.password );
        confirm = findViewById( R.id.confirm );

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, mail, pass, conf;
                name = username.getText().toString();
                mail = email.getText().toString();
                pass = password.getText().toString();
                conf = confirm.getText().toString();

                Toast.makeText(getApplicationContext(), "Name = " + name
                        + "\nEmail = " + mail
                        + "\nPassword = " + pass
                        + "\nConfirm Password = " + conf,
                        Toast.LENGTH_LONG).show();
                if( validCredentials(name, mail, pass, conf) )
                {
                    User user = new User(getApplicationContext());
                    user.open();
                    user.addUser(name, mail, pass);
                    user.close();

                    Toast.makeText(getApplicationContext(), "Created account successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent( SignUpActivity.this, LoginActivity.class );
                    startActivity( intent );
                    finish();
                }

            }
        });
    }

    private boolean validCredentials( String username, String email, String password, String confirmPassword)
    {
        if( username.equals("") )
        {
            Toast.makeText(getApplicationContext(), "Please enter a valid name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if( email.equals("") )
        {
            Toast.makeText(getApplicationContext(), "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }


        if( password.equals( confirmPassword ) )
        {
            return true;
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Password and confirm password do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
