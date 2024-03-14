package com.example.eazy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    private EditText entered_otp;
    private Button resend_otp, confirm_otp;
    private int oneTimePassword;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_forgot_password );

        entered_otp = findViewById( R.id.entered_otp );
        resend_otp = findViewById( R.id.resend_otp );
        confirm_otp = findViewById( R.id.confirm_otp );

        sendOTP();

        Toast.makeText(getApplicationContext(), "Generated OTP = " + oneTimePassword, Toast.LENGTH_SHORT).show();

        confirm_otp.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View view )
            {
                int otp = Integer.parseInt(entered_otp.getText().toString());

                Toast.makeText(getApplicationContext(), "Entered OTP = " + otp, Toast.LENGTH_SHORT).show();

                if( otp == oneTimePassword )
                {
                    Intent intent = new Intent( ForgotPassword.this, ChangePasswordActivity.class );
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You have entered a wrong OTP!\n" +
                            "click 'resend' to get a new one", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendOTP();

                Toast.makeText(getApplicationContext(), "New OTP = " + oneTimePassword, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private int generateOTP()
    {
        int i = (int) ( 1000 + Math.random() * 10000 );

        if( i == 0 )
            return 2312;//return this default value

        return i;
    }

    private void sendOTP()
    {
        User user = new User( getApplicationContext() );
        user.open();
        String email = user.getEmail();
        user.close();

        //send otp to this email;
        oneTimePassword = generateOTP();
        Toast.makeText(getApplicationContext(), "Email = " + email, Toast.LENGTH_SHORT).show();

        //upon success
        Toast.makeText(getApplicationContext(), "An OTP code has been sent to your email", Toast.LENGTH_SHORT).show();

    }
}
