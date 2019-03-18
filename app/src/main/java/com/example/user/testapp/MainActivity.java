package com.example.user.testapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.concurrent.TimeUnit;

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
    private Boolean ispic;
    private String mVerificationId;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ispic = false;
        Intent intent = getIntent();
        if(intent.getExtras() != null){
            myfile = (File)intent.getExtras().get("picfile");
            if(myfile != null){
                Log.i("original file size : ", Long.toString(myfile.length()));
                ispic = true;
                compressit(myfile);
            }else{
                Log.i("your file", " is null");
            }
        }
        mStorageRef = FirebaseStorage.getInstance().getReference();
        textInputEditText = findViewById(R.id.inp_mb);
        btnup = findViewById(R.id.btn_up);
        btnaddpic = findViewById(R.id.btn_addpic);
    }

    public void upld(View view) {
//        mob = textInputEditText.getText().toString();
//        if(mob.length() != 10 || (ispic == false)){
//            Toast.makeText(getApplicationContext(), "mobile number not ok", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(getApplicationContext(), "everything alright", Toast.LENGTH_SHORT).show();
//            uploadFile(cfile);
//        }
        sendotp();
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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void uploadFile(File cfile){
        Uri file = Uri.fromFile(new File(cfile.getPath()));
        StorageReference riversRef = mStorageRef.child("images/"+System.currentTimeMillis()+".jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getApplicationContext(), "file uploaded sucessfully", Toast.LENGTH_SHORT).show();
                        Log.i("myfile path: ", myfile.getPath());
                        myfile.delete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "file upload failure " , Toast.LENGTH_SHORT).show();
                        Log.i("error", exception.toString());
                    }
                });
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            Log.i("code: ", code);
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
//            if (code != null) {
//                editTextCode.setText(code);
//                //verifying the code
//                verifyVerificationCode(code);
//            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            Log.i("verification id: ", s);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    public void sendotp(){
        String mob= textInputEditText.getText().toString();
        sendVerificationCode(mob);
    }

}
