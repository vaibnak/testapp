package com.example.user.testapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class Seepic extends AppCompatActivity {
    private ImageView imageView;
    private Button ok_btn;
    private Button g_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seepic);
        Intent intent = getIntent();
        imageView = findViewById(R.id.img);
        ok_btn = findViewById(R.id.upld);
        g_back = findViewById(R.id.gback);
        Bundle extras = intent.getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        File myfile = (File)extras.get("picfile");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bmp);
    }

    public void takePic(View view) {
    }

    public void okPic(View view) {
    }
}
