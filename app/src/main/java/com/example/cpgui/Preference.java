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

import com.example.cpgui.R;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Preference extends AppCompatActivity {
    private EditText phoneNumber;
    private Spinner gender,institute;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Intent intent;
    private Map<String,String> dataCollect;

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
                databaseReference.child("User").child("Details").setValue(dataCollect).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification();
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
                databaseReference.child("User").child("Details").setValue(dataCollect).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification();
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
            dataCollect=new HashMap<String, String>();
            dataCollect.put("Username",intent.getStringExtra("Username"));
            dataCollect.put("EmailId",intent.getStringExtra("Emailid"));
            dataCollect.put("RollNumber",intent.getStringExtra("Rollnumber"));
            dataCollect.put("PhoneNumber",phoneNumber.getText().toString());
            dataCollect.put("Institute",institute.getSelectedItem().toString());
            dataCollect.put("Gender",gender.getSelectedItem().toString());
            dataCollect.put("Type","Student");
            //Toast.makeText(Preference.this,intent.getStringExtra("Emailid"),Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    private boolean fDataCollect()
    {

        if(validField())
        {
            dataCollect=new HashMap<String, String>();
            dataCollect.put("Username",intent.getStringExtra("Username"));
            dataCollect.put("EmailId",intent.getStringExtra("Emailid"));
            dataCollect.put("PhoneNumber",phoneNumber.getText().toString());
            dataCollect.put("Institute",institute.getSelectedItem().toString());
            dataCollect.put("Gender",gender.getSelectedItem().toString());
            dataCollect.put("Type","Faculty");
            //Toast.makeText(Preference.this,intent.getStringExtra("Username"),Toast.LENGTH_SHORT).show();
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
