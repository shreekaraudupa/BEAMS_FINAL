package com.example.shree.beams_final;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText etOTP,etPhoneNumber;
    Button btnSkip,btnVerifyOtp,btnSendOtp;
    String ReceivedNumber,verification_code;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        auth=FirebaseAuth.getInstance();
        etPhoneNumber=findViewById(R.id.etPhoneNumber);
       // etOTP=findViewById(R.id.etOTP);
        btnVerifyOtp=findViewById(R.id.btnVerifyOtp);
        btnSendOtp=findViewById(R.id.btnSendOtp);
        btnSkip=findViewById(R.id.btnSkip);
        btnVerifyOtp.setEnabled(false);

        int orientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(orientation);


        mCallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                btnVerifyOtp.setEnabled(true);
                Toast.makeText(getApplicationContext(),"NUMBER VERIFIED "+"\n"+"Click next ",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //btnVerifyOtp.setEnabled(true);
                verification_code=s;
                Toast.makeText(getApplicationContext(),"Code sent to this number",Toast.LENGTH_LONG).show();
            }
        };

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent RegIntent=new Intent(OtpActivity.this,RegisterActivity.class);
                RegIntent.putExtra("number",ReceivedNumber);
                startActivity(RegIntent);
                finish();

            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent skipIntent=new Intent(OtpActivity.this,RegisterActivity.class);
                startActivity(skipIntent);
            }
        });

    }

    public void send_Otp(View v){
        ReceivedNumber=etPhoneNumber.getText().toString();
        if(!ReceivedNumber.isEmpty() && ReceivedNumber.length()==10) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    ReceivedNumber,
                    60,
                    TimeUnit.SECONDS,
                    this, mCallback);
        }
        else {
            Toast.makeText(getApplicationContext(),"Enter a valid number",Toast.LENGTH_LONG).show();
        }
    }
/*
    public void verify(View v){
        String enteredOtp=etOTP.getText().toString();
        //Toast.makeText(getApplicationContext(),"Sent-"+verification_code+"Entered -"+enteredOtp,Toast.LENGTH_LONG).show();
        if(verification_code!=null) {
            if(verification_code.equals(enteredOtp)){
                Toast.makeText(getApplicationContext(),"Verified",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(),"Not Verified",Toast.LENGTH_LONG).show();

            }
        }
    }
*/
}
