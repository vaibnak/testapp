package com.example.user.testapp;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText textInputEditText;
    private Button btnup;
    private Button btnaddpic;
    private String mob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInputEditText = findViewById(R.id.inp_mb);
        btnup = findViewById(R.id.btn_up);
        btnaddpic = findViewById(R.id.btn_addpic);
    }

    public void upld(View view) {
        mob = textInputEditText.getText().toString();
        if(mob.length() != 10){
            Toast.makeText(getApplicationContext(), "mobile number not ok", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "everything alright", Toast.LENGTH_SHORT).show();
        }
    }

    public void take_pht(View view) {
        Intent intent = new Intent(getApplicationContext(), Takepic.class);
        startActivity(intent);
    }
}
