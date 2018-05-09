package com.senindia.tictactoe.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.senindia.tictactoe.R;
import com.senindia.tictactoe.comman.CommanClass;

import java.util.Calendar;

/**
 * Created by vishal on 12/3/18.
 */

public class HomeActivity extends AppCompatActivity {

    CommanClass cc;

    TextView tv_play,tv_join,tv_rate;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("sessions");
    DataSnapshot dataSnapshot;
    Calendar c;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cc = new CommanClass(this);

        init();
    }

    private void init() {

        tv_play = (TextView)findViewById(R.id.tv_play);
        tv_join = (TextView)findViewById(R.id.tv_join);
        tv_rate = (TextView)findViewById(R.id.tv_rate);

        clickListner();
    }

    private void clickListner() {

        tv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentStart = new Intent(HomeActivity.this,StartActivity.class);
                startActivity(intentStart);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intentStart = new Intent(HomeActivity.this,JoinActivity.class);
                startActivity(intentStart);*/

                popUpCodeEnterDialog();

            }
        });

        tv_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void popUpCodeEnterDialog() {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.joining_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);

        final String[] code = {"0000"};

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataSnapshot = snapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });


        TextView tv_submit_code = (TextView) dialog.findViewById(R.id.tv_submit_code);

        tv_submit_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText code_editText = (EditText)dialog.findViewById(R.id.code_edittext);

                String code_main = code_editText.getText().toString();

                if (!cc.isConnectingToInternet()) {

                    cc.showToast("No Internet Connection");

                } else if (code_main.equals("")){

                    cc.showToast("Please enter code");
                }else {
                }

                    if(code_editText.getText().length() == 4) {
                    code[0] = String.valueOf(code_editText.getText());
                    if(dataSnapshot.child(code[0]).exists()) {
                        if(dataSnapshot.child(code[0]).child("p2").exists()) {
                            Toast.makeText(getBaseContext(),
                                    "The Game has already started. Please generate a new code.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            c = Calendar.getInstance();
                            if(dataSnapshot.child(code[0]).child("start_time")
                                    .getValue(Long.class) - c.getTimeInMillis() >= 86400000) {
                                Toast.makeText(getBaseContext(),
                                        "The code has expired. Please generate a new code.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                String android_id = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID);
                                if(android_id.equals(dataSnapshot.child(code[0]).child("p1")
                                        .getValue(String.class))) {
                                    Toast.makeText(getBaseContext(),
                                            "You can't join a game started by you.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    myRef.child(String.valueOf(code[0])).child("p2")
                                            .setValue(android_id);
                                    Toast.makeText(getBaseContext(), "Game Started!",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getBaseContext(), GameActivity.class)
                                            .putExtra("session_code", code[0]).putExtra("my_player", "O"));
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                                    dialog.dismiss();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Invalid Code!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        dialog.show();

    }
}
