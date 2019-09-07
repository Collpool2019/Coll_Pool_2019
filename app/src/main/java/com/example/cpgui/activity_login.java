package com.example.cpgui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_login extends AppCompatActivity {
    private EditText Username,Password;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username=(EditText)findViewById(R.id.username);
        Password=(EditText)findViewById(R.id.password);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            if(firebaseUser.isEmailVerified()) {
                finish();
                startActivity(new Intent(activity_login.this, FinalSpace.class));
            }
            else
            {
                Toast.makeText(activity_login.this,"Please verify your email address",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onStartLogin(View view)
    {
        authenticate(Username.getText().toString(),Password.getText().toString());
        counter++;
    }
    private boolean validate()
    {
        Boolean a=false;
        if(Username.getText().toString().isEmpty()||Password.getText().toString().isEmpty())
        {
            Toast.makeText(activity_login.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(!Username.getText().toString().contains("nirmauni.ac.in"))
            {
                Toast.makeText(activity_login.this,"We only accept Nirma domain id's for security purpose",Toast.LENGTH_SHORT).show();
            }
            else
            {
                a=true;
            }
        }
        return a;
    }
    private void authenticate(String a,String b)
    {
        progressDialog.setMessage("Loging in");
        progressDialog.show();
        if(validate())
        {
            firebaseAuth.signInWithEmailAndPassword(a,b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                        if(firebaseUser.isEmailVerified())
                        {
                            Toast.makeText(activity_login.this,"You have loged in successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(activity_login.this,FinalSpace.class));
                        }
                        else if(counter>0&&!(firebaseUser.isEmailVerified()))
                        {
                            Toast.makeText(activity_login.this,"Please verify your email address",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(activity_login.this,"You are signed in",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
