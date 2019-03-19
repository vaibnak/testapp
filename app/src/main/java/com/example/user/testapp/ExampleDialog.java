package com.example.user.testapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editText;
    private TextView textView;
    private ExampleDialogListener listener;
    AlertDialog alertDialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        textView = view.findViewById(R.id.tmleft);
        final String t = "Time left: ";
        final int ti = 30;
        final CountDownTimer ct = new CountDownTimer(30500, 1000) {
            public  void onTick(long millisecondsUntilDone) {
                textView.setText(t+ millisecondsUntilDone/1000);
            }
            public void onFinish() {
                textView.setText(Integer.toString(0));
                listener.applyTexts(Integer.toString(000));
                alertDialog.dismiss();
            }
        }.start();
        builder.setView(view)
                .setTitle("Enter OTP")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       String otp = editText.getText().toString();
                       if(otp.isEmpty()){
                           otp = "000";
                       }
                       listener.applyTexts(otp);
                       ct.cancel();
                    }
                });
        editText = view.findViewById(R.id.otp);
        alertDialog = builder.create();
        return alertDialog;
    }
    public interface ExampleDialogListener{
        void applyTexts(String otp);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (ExampleDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement exampledialoglistener");
        }
    }
}
