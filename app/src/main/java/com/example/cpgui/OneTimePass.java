package com.example.cpgui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;


import java.util.concurrent.TimeUnit;


import java.util.concurrent.TimeUnit;

public class OneTimePass extends AppCompatActivity {
    private String phoneno;
    private EditText editTextCode;
    private FirebaseAuth mAuth;
    private String verificationId;
    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
   // public StdData stdData = new StdData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_time_pass);
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editText);
        Intent intentph = getIntent();
        phoneno = intentph.getStringExtra("phoneno.");
        startphoneauth(phoneno);

        findViewById(R.id.submitotp).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }
                verifyVerificationCode(code);
            }

        });
        findViewById(R.id.resendotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startphoneauth(phoneno);
            }
        });
    }

    private void startphoneauth(String phoneno) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneno, 120, TimeUnit.SECONDS, this, mCallbacks);
    }
private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


    @Override
    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(s, forceResendingToken);
        verificationId=s;
    }

    @Override
    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
         String code =phoneAuthCredential.getSmsCode();
         if(code!=null){
             verifyVerificationCode(code);
         }
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        Toast.makeText(OneTimePass.this,e.getMessage(),Toast.LENGTH_LONG).show();

    }
};

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OneTimePass.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            mAuth.signOut();
                            Intent intent = new Intent(OneTimePass.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

}


