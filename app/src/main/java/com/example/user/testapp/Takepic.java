package com.example.user.testapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;

public class Takepic extends AppCompatActivity {
    private CameraKitView cameraKitView;
    private Button phbtn;
    private File myfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepic);
        Intent intent = getIntent();
        cameraKitView = findViewById(R.id.cmr);
        phbtn = findViewById(R.id.phtbtn);
        phbtn.setOnClickListener(photoClickListener);
    }
    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnClickListener photoClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, byte[] photo) {
                    myfile = new File(getApplicationContext().getFilesDir().toString(), "photo.jpg");
                    try {
                        Log.i("file path: ", myfile.getPath());
                        FileOutputStream fileOutputStream = new FileOutputStream(myfile.getPath());
                        fileOutputStream.write(photo);
                        fileOutputStream.close();
                        sendtoact(photo);
                    }catch (Exception e){
                        Log.i("error is : ", e.toString());
                    }
                }
            });
        };
    };

    public void sendtoact(byte[] photo){
        Intent intent = new Intent(getApplicationContext(), Seepic.class);
        intent.putExtra("picture", photo);
        intent.putExtra("picfile", myfile);
        startActivity(intent);
    }
}
