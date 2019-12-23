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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_login extends AppCompatActivity {
    private EditText Username,Password;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private StdData stdData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username=(EditText)findViewById(R.id.username1);
        Password=(EditText)findViewById(R.id.password1);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
    }
    public void onStartLogin(View view)
    {
        authenticate(Username.getText().toString(),Password.getText().toString());
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
    public void onForgotPassword(View view)
    {
        passwordReset();
    }
    private void authenticate(String a,String b)
    {
        if(validate())
        {
            progressDialog.setMessage("Logging in");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(a,b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        firebaseUser=firebaseAuth.getCurrentUser();
                        if(firebaseUser.isEmailVerified())
                        {
                            Toast.makeText(activity_login.this,"You have loged in successfully",Toast.LENGTH_SHORT).show();
                            /*if(dataCheck())
                            {
                                startActivity(new Intent(activity_login.this,Preference.class));
                            }*/
                            //else {
                                startActivity(new Intent(activity_login.this, FinalSpace.class));
                            //}
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(activity_login.this,"Please verify your email address",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(activity_login.this,"Password entered is not correct",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void onNewSignUp(View view)
    {
        startActivity(new Intent(activity_login.this,SignUpPro.class));
    }
    private boolean validForReset()
    {
        if(Username.getText().toString().isEmpty())
            return false;
        return true;
    }
    private void passwordReset()
    {
        if(validForReset()) {
            progressDialog.setMessage("Authenticating");
            progressDialog.show();
            firebaseAuth.sendPasswordResetEmail(Username.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(activity_login.this,"Reset Link has been sent to your email address",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(activity_login.this,"User is not registered ",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(activity_login.this,"Plese enter your register email id",Toast.LENGTH_SHORT).show();
        }
    }
    /*private boolean dataCheck()
    {
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 stdData=dataSnapshot.getValue(StdData.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              Toast.makeText(activity_login.this,"Some error has been occured",Toast.LENGTH_SHORT).show();
            }
        });
        if(stdData.getPhoneNumber().equals(null))
            return true;
        return false;
    }*/
}
