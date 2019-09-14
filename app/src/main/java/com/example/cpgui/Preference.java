package com.example.cpgui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Preference extends AppCompatActivity {
    private EditText phoneNumber;
    private Spinner gender,institute;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StdData stdData;
    private FacultyData fdata;
    private Intent intent;
    public OneTimePass phone=new OneTimePass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance("https://collpool2019-2fe22.firebaseio.com/");
        phoneNumber=(EditText)findViewById(R.id.phonenumber);
        gender=(Spinner)findViewById(R.id.gender);
        institute=(Spinner)findViewById(R.id.Institute);
        intent=getIntent();
    }
    public void onContinue(View view)
    {
       // if(firebaseUser.isEmailVerified())

            transfer();

       // else
         //   Toast.makeText(Preference.this,"Verify Email Address",Toast.LENGTH_SHORT).show();
    }
    private void transfer()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        //PhoneAuthProvider phone=PhoneAuthProvider.getInstance();
        progressDialog.setMessage("Transferring");
        if(intent.getStringExtra("Id").equals("From Student")) {
            if(dataCollect()) {
                progressDialog.show();
                DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
                databaseReference.setValue(stdData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                          // phone.startphoneauth();
                            firebaseAuth.signOut();
                            progressDialog.dismiss();
                            Toast.makeText(Preference.this, "Process completed", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Preference.this, OneTimePass.class);
                            intent.putExtra("phoneno.",phoneNumber.getText().toString().trim());
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Preference.this, "Process not completed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if(intent.getStringExtra("Id").equals("From Faculty"))
        {
            if(fDataCollect())
            {
                progressDialog.show();
                DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
                databaseReference.setValue(fdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            // phone.verifyPhoneNumber(stdData.getPhoneNumber(),7, TimeUnit.MINUTES,Preference.this);
                            firebaseAuth.signOut();
                            progressDialog.dismiss();
                            Toast.makeText(Preference.this, "Process completed", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Preference.this, OneTimePass.class);
                            intent.putExtra("phoneno.",phoneNumber.getText().toString().trim());
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Preference.this, "Process not completed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
    private boolean dataCollect()
    {
      if(validField())
      {
          stdData=new StdData();
          stdData.setUsername(intent.getStringExtra("Username"));
          stdData.setEmailid(intent.getStringExtra("Emailid"));
          stdData.setRollnumber(intent.getStringExtra("Rollnumber"));
          stdData.setPhoneNumber(phoneNumber.getText().toString());
          stdData.setInstitute(institute.getSelectedItem().toString());
          stdData.setGender(gender.getSelectedItem().toString());
          stdData.setType("Student");
          return true;
      }
      return false;
    }
    private boolean fDataCollect()
    {

        if(validField())
        {
            fdata=new FacultyData();
            fdata.setType("Faculty");
            fdata.setUsername(intent.getStringExtra("UsernameF"));
            fdata.setEmailid(intent.getStringExtra("EmailIdF"));
            fdata.setPhonenumber(phoneNumber.getText().toString());
            fdata.setInstitute(institute.getSelectedItem().toString());
            fdata.setGender(gender.getSelectedItem().toString());
            return true;
        }
        return false;
    }
    private boolean validField()
    {
        if(phoneNumber.getText().toString().trim().isEmpty())
        {
            Toast.makeText(Preference.this,"Please fill all fields ",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
