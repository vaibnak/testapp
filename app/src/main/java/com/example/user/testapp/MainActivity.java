package com.example.user.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    private TextInputEditText textInputEditText;
    private Button btnup;
    private Button btnaddpic;
    private String mob;
    private File cfile, myfile;
    private Boolean ispic;
    private String mVerificationId;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String otpCode;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        //instantiating all variables
        relativeLayout = findViewById(R.id.relativelayout);
        textInputEditText = findViewById(R.id.inp_mb);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnup = findViewById(R.id.btn_up);
        btnaddpic = findViewById(R.id.btn_addpic);
        btnaddpic.setVisibility(View.GONE);
        progressBar = findViewById(R.id.pgbar);
        progressBar.setVisibility(View.GONE);

        //using sharedpreferences to store the no. so that it can be retrieved back after coming from another activity
        sharedPreferences = getSharedPreferences(mob, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("mobile")) {
            mob = (String) sharedPreferences.getString("mobile", "123");
            textInputEditText.setText(mob);
        }


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            myfile = (File) intent.getExtras().get("picfile");
            if (myfile != null) {
                compressit(myfile);
            }
        }
    }

    public void showpg(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stoppg(){
        progressBar.setVisibility(View.GONE);
    }

    public void showsnackbar(String msg){
        Snackbar snackbar = Snackbar.make(relativeLayout, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void signout(){
        FirebaseAuth.getInstance().signOut();
        textInputEditText.setText("");
        sharedPreferences.edit().clear().commit();
    }

    //function that runs on visit button clicking
    public void upld(View view) {
        mob = textInputEditText.getText().toString();
        if (mob.length() != 10) {
            showsnackbar("Check the mobile number entered");
        } else {
            sharedPreferences.edit().putString("mobile", mob).apply();
            showpg();
            Query query = database.getReference("visitors").orderByChild("mob").equalTo(mob);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    stoppg();
                    if (dataSnapshot.exists()) {
                        //If user already exists
                        preUser();
                    } else {
                        //If user is new
                        newUser();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    stoppg();
                    showsnackbar("Error in database connection");
                }
            });
        }
    }

    //function for new user
    public void newUser() {
        btnaddpic.setVisibility(View.VISIBLE);
        btnup.setVisibility(View.GONE);
    }

    //function for existing user
    public void preUser() {
        //update the visitcount of that user
        showpg();
        Query query = database.getReference("visitors").orderByChild("mob").equalTo(mob);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    stoppg();
                    User u = singleSnapshot.getValue(User.class);
                    int val = u.visitCount + 1;
                    singleSnapshot.getRef().child("visitCount").setValue(val);
                    showsnackbar("welcome for the "+val+" time");
                    //signout on workdone
                    signout();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                showsnackbar("Error in database");
                signout();
            }
        });
    }

    //function to take otp back from dialog box
    @Override
    public void applyTexts(String otp) {
        verifyVerificationCode(otp);
    }


    //function that runs on addpic button
    public void take_pht(View view) {
        Intent intent = new Intent(getApplicationContext(), Takepic.class);
        startActivity(intent);
    }

    //compressing function
    public void compressit(File actualImage) {
        new Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        cfile = file;
                        onCompressComplete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    //function to run on compression process completion
    public void onCompressComplete() {
        showpg();
        sendotp();
    }


   //function to upload image in firebase database
    public void uploadFile(File cfile, final String mob, final String ref) {
        showpg();
        myRef = database.getReference(ref);
        Uri file = Uri.fromFile(new File(cfile.getPath()));
        StorageReference riversRef = mStorageRef.child("images/" + System.currentTimeMillis() + ".jpg");
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri picuri = taskSnapshot.getDownloadUrl();
                        String id = myRef.push().getKey();
                        stoppg();
                        if (ref == "visitors") {
                            showsnackbar("New user added");
                            User u = new User(mob, picuri.toString());
                            myRef.child(id).setValue(u);
                        } else {
                            showsnackbar("You have entered wrong OTP");
                            SuspUser us = new SuspUser(mob, picuri.toString());
                            myRef.child(id).setValue(us);
                        }

                        //signing user out once his work has been done
                        signout();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        stoppg();
                        showsnackbar("Firebase storage failure ");
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

    public void sendotp() {
        sendVerificationCode(mob);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            otpCode = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            stoppg();
            showsnackbar("Verificaion process error");
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            stoppg();
            mVerificationId = s;
            ExampleDialog exampleDialog = new ExampleDialog();
            exampleDialog.show(getSupportFragmentManager(), "example dialog");
        }
    };

    public void verifyVerificationCode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showpg();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        stoppg();
                        if (task.isSuccessful()) {
                            uploadFile(cfile, mob, "visitors");
                            Log.i("authentication ", "Succesful");
                        } else {
                            uploadFile(cfile, mob, "suspicious_users");
                        }
                    }
                });
    }
}
