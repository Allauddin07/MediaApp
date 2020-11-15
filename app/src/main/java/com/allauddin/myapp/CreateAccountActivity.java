package com.allauddin.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

public class CreateAccountActivity extends AppCompatActivity {

    private EditText name, email, password, confirm;
    private TextView logIn;
    private Button singUp;
    FirebaseAuth mAuth;
    private ProgressBar pBar;
    ProgressDialog progressDialog;
    //FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        name= findViewById(R.id.name);
        email= findViewById(R.id.email);
        password= findViewById(R.id.password);
        confirm= findViewById(R.id.confirmPass);
        logIn= findViewById(R.id.login);
        singUp=findViewById(R.id.signup);
        mAuth=FirebaseAuth.getInstance();
        //pBar=findViewById(R.id.pBar);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        progressDialog=new ProgressDialog(this);




        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
            }
        });

        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String N= name.getText().toString();
                String E= email.getText().toString();
                String C= confirm.getText().toString();
                String P= password.getText().toString();
                if(N.isEmpty()||N.equals("")){
                    name.setError("please Enter your name");

                }
                else if(E.isEmpty()){
                    email.setError("please Enter your Email");

                }

                else if(P.isEmpty()||P.length()<6){
                    password.setError("please Enter valid Password");

                }
                else if(!C.equals(C)){
                    confirm.setError("Password not matched");

                }else {

                    //pBar.setVisibility(View.VISIBLE);
                    CAccount(E, P);
                }


            }
        });
    }
    private  void  CAccount(String email, String password){

        progressDialog.setTitle("Please wiat");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword( email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(CreateAccountActivity.this, HomeActivity.class));
                    Toast.makeText(CreateAccountActivity.this, "LogIn successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccountActivity.this, "LogIn Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateAccountActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}