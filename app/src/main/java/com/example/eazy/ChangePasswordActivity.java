package com.example.eazy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {


    private EditText newPassword, confirmNewPassword;
    private Button changePassword;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.change_password_activity);

        newPassword = findViewById( R.id.newPassword );
        confirmNewPassword = findViewById( R.id.confirmNewPassword );
        changePassword = findViewById( R.id.changePassword );

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass, conf;

                pass = newPassword.getText().toString();
                conf = confirmNewPassword.getText().toString();

                Toast.makeText( getApplicationContext(), "New pass = " + pass
                + "\nCofirmed = " + conf, Toast.LENGTH_SHORT).show();

                if( isValidPassword( pass ) && pass.equals( conf ) )
                {
                    User user = new User( getApplicationContext() );
                    user.open();
                    user.changePassword( pass );
                    user.close();
                }
            }
        });

    }

    private boolean isValidPassword( String password )
    {
        String regex = "";

        //if( password.matches( regex ) )
        //    return true;

        return true;
    }
}
