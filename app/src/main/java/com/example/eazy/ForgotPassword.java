package com.example.eazy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;

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
        //Toast.makeText(getApplicationContext(), "Email = " + email, Toast.LENGTH_SHORT).show();
        SendMessageTask task = new SendMessageTask(getApplicationContext(), email, "" + oneTimePassword);
        //upon success
        Toast.makeText(getApplicationContext(), "An OTP code has been sent to your email", Toast.LENGTH_SHORT).show();

    }
}

class SendMessageTask implements Runnable
{
    private String code;
    private String userEmail;
    private Thread thread;
    private Context context;

    public SendMessageTask(Context context, String userEmail, String code)
    {
        this.context = context;
        this.userEmail = new String( userEmail );
        this.code = new String( code );
        thread = null;
    }

    private Context getAppContext(){return this.context; }
    public void start()
    {
        try
        {
            if( thread == null )
            {
                thread = new Thread( this );
                thread.start();
            }
            else if( !thread.isAlive() )
            {
                thread = null;
                thread = new Thread( this );
                thread.start();
            }
        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void run()
    {
        try {

            if( userEmail.equals("") )
            {
                Toast.makeText( getAppContext(), "No registered Email!", Toast.LENGTH_SHORT).show();
                return;
            }
            if( !UtilityClass.networkIsAvailable( getAppContext() ) )
            {
                Toast.makeText( getAppContext(), "Check network connection!", Toast.LENGTH_LONG).show();
            }

            String senderEmail = "sanaautoto@gmail.com";
            String senderEmailPassword = "vues ziws onvl qldq";
            String host = "smtp.gmail.com";

            //Toast.makeText(this, "Setting up properties", Toast.LENGTH_SHORT).show();

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            //properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.ssl.enable", true);
            properties.put("mail.smtp.auth", true);

            //Toast.makeText(this, "After Setting up Properties", Toast.LENGTH_SHORT).show();

            Session session = Session.getInstance( properties, new Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderEmailPassword );
                }
            });

            // Toast.makeText(this, "After Creating Session", Toast.LENGTH_SHORT).show();

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail) );
            mimeMessage.setSubject("Subject: Frats Password Recovery");
            mimeMessage.setText("Your FRATS password recovery code is: " + code);

            Transport.send(mimeMessage);

        }
        catch( MessagingException me)
        {
            Toast.makeText( getAppContext(), "Messaging Exception: " + me, Toast.LENGTH_SHORT).show();
        }
        catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
