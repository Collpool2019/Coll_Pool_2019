package com.example.cpgui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class activity_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void onStartLogin(View view)
    {
        EditText Username=(EditText)findViewById(R.id.username);
        EditText Password=(EditText)findViewById(R.id.password);
        if((Username.getText().toString().equals("admin"))&&(Password.getText().toString().equals("1234")))
        {
            CharSequence prompt = "Welcome " +Username.getText().toString();
            int length= Toast.LENGTH_SHORT;

            Toast toast=Toast.makeText(this,prompt,length);
            toast.show();
        }
        /*else
        {
            CharSequence prompt = "Welcome" +Username.getText().toString();
            int length= Toast.LENGTH_SHORT;

            Toast toast=Toast.makeText(this,prompt,length);
            toast.show();
        }*/
    }
}
