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

public class SignUpFaculty extends AppCompatActivity {
    private EditText name,emailadd,password,cpassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_faculty);
        name=(EditText)findViewById(R.id.Name1);
        emailadd=(EditText)findViewById(R.id.username1);
        password=(EditText)findViewById(R.id.password1);
        cpassword=(EditText)findViewById(R.id.confirm_password1);
        auth=FirebaseAuth.getInstance();
    }
    public void onSignUpFaculty(View view)
    {
        if(validDetails())
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Signing You Up");
            progressDialog.show();
            auth.createUserWithEmailAndPassword(emailadd.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(SignUpFaculty.this,"You have signed up,Verify your email address",Toast.LENGTH_SHORT).show();
                                    auth.signOut();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(SignUpFaculty.this,MainActivity.class));
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpFaculty.this,"You might not be connected to internet or email address is not valid",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpFaculty.this,"You might be not connected to internet\nOr your account already exists",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private boolean validDetails()
    {
        Boolean a=false;
        if(name.getText().toString().isEmpty()||emailadd.getText().toString().isEmpty()||password.getText().toString().isEmpty()||cpassword.getText().toString().isEmpty())
        {
            Toast.makeText(SignUpFaculty.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(!emailadd.getText().toString().contains("@nirmauni.ac.in"))
            {
                Toast.makeText(SignUpFaculty.this,"Enter your nirma email id only",Toast.LENGTH_SHORT).show();
            }
            else if(password.getText().toString().length()<8)//||!(password.getText().toString().contains("\\d")))
            {
                Toast.makeText(SignUpFaculty.this,"Password must be 8 characters long and have digits in it",Toast.LENGTH_SHORT).show();
            }
            else if(!password.getText().toString().equals(cpassword.getText().toString()))
            {
                Toast.makeText(SignUpFaculty.this,"Password is not matching confirm password",Toast.LENGTH_SHORT).show();
            }
            else
            {
                a=true;
            }
        }
        return a;
    }
}
