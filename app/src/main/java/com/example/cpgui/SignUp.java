package com.example.cpgui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    private EditText name,rollnumber,emailadd,password,cpassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name=(EditText)findViewById(R.id.Name);
        rollnumber=(EditText)findViewById((R.id.Roll));
        emailadd=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        cpassword=(EditText)findViewById(R.id.confirm_password);
        auth=FirebaseAuth.getInstance();
    }
    public void onSignUpActive(View view)
    {
        if(validDetails())
        {
          auth.createUserWithEmailAndPassword(emailadd.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful())
               {
                   Toast.makeText(SignUp.this,"You have been successfully verified",Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(SignUp.this,MainActivity.class));
               }
               else {
                   Toast.makeText(SignUp.this,"You might be not connected to internet\nOr your account already exists",Toast.LENGTH_SHORT).show();
               }
              }
          });
        }
    }
    private boolean validDetails()
    {
        Boolean a=false;
     if(name.getText().toString().isEmpty()||rollnumber.getText().toString().isEmpty()||emailadd.getText().toString().isEmpty()||password.getText().toString().isEmpty()||cpassword.getText().toString().isEmpty())
     {
         Toast.makeText(SignUp.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
     }
     else
     {
         if(!emailadd.getText().toString().contains("nirmauni.ac.in"))
         {
             Toast.makeText(SignUp.this,"We only accept nirma domain id's for security purpose",Toast.LENGTH_SHORT).show();
         }
         else if(!password.getText().toString().equals(cpassword.getText().toString()))
         {
             Toast.makeText(SignUp.this,"Password is not matching confirm password",Toast.LENGTH_SHORT).show();
         }
         else
         {
             a=true;
         }
     }
     return a;
    }
}
