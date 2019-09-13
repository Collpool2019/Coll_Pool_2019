package com.example.cpgui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private EditText name,rollnumber,emailadd,password,cpassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = findViewById(R.id.Name1);
        rollnumber = findViewById((R.id.Roll));
        emailadd = findViewById(R.id.username1);
        password = findViewById(R.id.password1);
        cpassword = findViewById(R.id.confirm_password1);
        auth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance("https://collpool2019-2fe22.firebaseio.com/");
    }
    public void onSignUpActive(View view)
    {
        authenticate();
    }
    private void authenticate()
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
                        //auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                           // @Override
                           // public void onComplete(@NonNull Task<Void> task) {
                             //   if(task.isSuccessful())
                        //{
                                   // connect();
                        //  progressDialog.dismiss();
                        //Toast.makeText(SignUp.this,"You have signed up",Toast.LENGTH_SHORT).show();
                        // intentData();
                        //}*/
                                //else
                                //{
                                    //progressDialog.dismiss();
                                    //Toast.makeText(SignUp.this,"You might not be connected to internet or email address is not valid",Toast.LENGTH_SHORT).show();
                                //}
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this,"You have signed up",Toast.LENGTH_SHORT).show();
                        intentData();
                    }
                    else {
                        progressDialog.dismiss();
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
         if(!emailadd.getText().toString().equals(rollnumber.getText().toString().trim()+"@nirmauni.ac.in"))
         {
             Toast.makeText(SignUp.this,"Enter your nirma email id only",Toast.LENGTH_SHORT).show();
         }
         else if(password.getText().toString().length()<8)//||!(password.getText().toString().contains("\\d")))
         {
             Toast.makeText(SignUp.this,"Password must be 8 characters long and have digits in it",Toast.LENGTH_SHORT).show();
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
   /* private void connect()
    {
        DatabaseReference databaseReference=firebaseDatabase.getReference(auth.getCurrentUser().getUid());
        StdData stdData=new StdData();
        stdData.setName1(name.getText().toString());
        stdData.setEmailid(emailadd.getText().toString().trim());
        stdData.setRollnumber(rollnumber.getText().toString());
        Toast.makeText(SignUp.this,stdData.getPhoneNumber(),Toast.LENGTH_SHORT).show();
        databaseReference.setValue(stdData);
    }*/
   private void intentData()
   {
       Intent intent=new Intent(SignUp.this,Preference.class);
       intent.putExtra("Username",name.getText().toString());
       intent.putExtra("Emailid",emailadd.getText().toString());
       intent.putExtra("Rollnumber",rollnumber.getText().toString());
       intent.putExtra("Id","From Student");
       startActivity(intent);
   }
}
