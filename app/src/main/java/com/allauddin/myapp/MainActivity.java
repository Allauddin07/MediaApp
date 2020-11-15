package com.allauddin.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView signup, forgot;
    private Button logi, googlesignin;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        logi=findViewById(R.id.login);
        googlesignin=findViewById(R.id.googleSignIn);
        signup=findViewById(R.id.SignUpTV);
        progressBar=findViewById(R.id.pBar);
        forgot=findViewById(R.id.forgot);
        auth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , ForgotPActivity.class));
            }
        });


        logi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String E=email.getText().toString().trim();
                String P=password.getText().toString().trim();
                if(E.isEmpty()){
                    email.setError("Enter valid email");

                }
                else if(P.isEmpty()|| P.length()<6) {
                    password.setError("enter 6 digit valid password");


                }
                else {
                    Login(E, P);
                }

            }
        });
    }

    private void Login(String e, String p) {
        progressDialog.setTitle("Please wiat");
        progressDialog.show();
        auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "login successfull", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }
                else {
                    Toast.makeText(MainActivity.this, "login failed", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if(user !=null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
    }
}