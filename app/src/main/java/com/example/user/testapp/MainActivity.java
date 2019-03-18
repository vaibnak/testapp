package com.example.user.testapp;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText textInputEditText;
    private Button btnup;
    private Button btnaddpic;
    private String mob;
    private File cfile,myfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            myfile = (File)intent.getExtras().get("picfile");
            if(myfile != null){
                Log.i("original file size : ", Long.toString(myfile.length()));
                compressit(myfile);
            }else{
                Log.i("your file", " is null");
            }
        }

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

    public void compressit(File actualImage){
        new Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        cfile = file;
                        Log.i("compression: ", "done");
                        Log.i("compressed file size: ", Long.toString(cfile.length()));
                        Log.i("org file size: ", Long.toString(myfile.length()));
                        Log.i("compressed file path", cfile.getPath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
