package com.senindia.tictactoe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.senindia.tictactoe.R;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by vishal on 12/3/18.
 */

public class StartActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("sessions");

    Calendar c;

    boolean isCodeGenerated = false;
    int sessionCode = 0;

    TextView tv_share;

    ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        pd = new ProgressDialog(StartActivity.this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Generating Invite Code");
        pd.show();


        tv_share = (TextView)findViewById(R.id.tv_share);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                while (!isCodeGenerated) {

                    Random random = new Random();
                    int n;
                    n = random.nextInt(9000) + 1000;

                    if(dataSnapshot.child(String.valueOf(n)).exists()) {
                        if(dataSnapshot.child(String.valueOf(n)).child("p1").exists()) {
                            c = Calendar.getInstance();
                            if(dataSnapshot.child(String.valueOf(n)).child("start_time")
                                    .getValue(Long.class) - c.getTimeInMillis() >= 86400000) {
                                startSession(n);
                            }
                        } else {
                            startSession(n);
                        }
                    } else {
                        startSession(n);
                    }
                }

                if(isCodeGenerated) {
                    if(dataSnapshot.child(String.valueOf(sessionCode)).child("p2").exists()) {
                        Toast.makeText(getBaseContext(), "Game Started!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getBaseContext(), GameActivity.class)
                                .putExtra("session_code", String.valueOf(sessionCode))
                                .putExtra("my_player", "X"));
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isCodeGenerated) {
                    Intent shareIntent =  new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Tic-Tac-Toe Friends Code");
                    String shareMessage = "Hey! Let's play Tic-Tac-Toe. Join my game with this code: " + sessionCode;
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,shareMessage);
                    startActivity(Intent.createChooser(shareIntent,"Share via"));
                }
            }
        });

    }

    private void startSession(int n) {
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        isCodeGenerated = true;
        sessionCode = n;

        myRef.child(String.valueOf(n)).child("p1").setValue(android_id);

        c = Calendar.getInstance();
        myRef.child(String.valueOf(n)).child("start_time").setValue(c.getTimeInMillis());

        TextView textView = (TextView) findViewById(R.id.code_text);
        textView.setText(String.valueOf(n));

        pd.dismiss();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
