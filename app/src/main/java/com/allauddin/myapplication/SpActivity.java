package com.allauddin.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp);
        FirebaseAuth auth =FirebaseAuth.getInstance();
       final FirebaseUser user = auth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user==null){
                    startActivity(new Intent(SpActivity.this, FrgActivity.class));

                }
                else {
                    startActivity(new Intent(SpActivity.this, MainActivity.class));

                }
                finish();
            }
        }, 2500);

    }
}