package com.example.cpgui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class SignUpPro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_pro);
    }

    public void SignUp(View view) {
        Spinner spinner=(Spinner)findViewById(R.id.professionspinner);
        switch(spinner.getSelectedItem().toString())
        {
            case"Student": {
                startActivity(new Intent(this, SignUp.class));
            } break;
            case "Faculty/Staff":{
                startActivity(new Intent(this, SignUpFaculty.class));
            } break;
        }

    }
}
