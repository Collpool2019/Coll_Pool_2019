package com.example.cpgui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            if(firebaseUser.isEmailVerified()) {
                finish();
                startActivity(new Intent(MainActivity.this, FinalSpace.class));
            }
            else
            {
                Toast.makeText(MainActivity.this,"Please verify your email address",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startLogin(View view) {
        Intent intent = new Intent(MainActivity.this, activity_login.class);
        startActivity(intent);
    }

    public void SignUpPro(View view) {
        startActivity(new Intent(MainActivity.this,SignUpPro.class));
    }
}
