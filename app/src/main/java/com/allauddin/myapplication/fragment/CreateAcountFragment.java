package com.allauddin.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allauddin.myapplication.FrgActivity;
import com.allauddin.myapplication.MainActivity;
import com.allauddin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class CreateAcountFragment extends Fragment {
    private EditText name, email, password, confirm;
    private TextView logIn;
    private Button singUp;
    private FirebaseAuth auth;
    private ProgressBar pBar;
    public static  final  String EMAIL_REGEX="^(.+)@(.+)$";
    //"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"

    public CreateAcountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_acount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        clickList();
    }

    private  void  init(View view){
        name= view.findViewById(R.id.name);
        email= view.findViewById(R.id.email);
        password= view.findViewById(R.id.password);
        confirm= view.findViewById(R.id.confirmPass);
        logIn= view.findViewById(R.id.login);
        singUp= view.findViewById(R.id.signup);
        auth=FirebaseAuth.getInstance();
        pBar=view.findViewById(R.id.pBar);


    }
    private  void  clickList(){
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FrgActivity) getActivity()).setFragment(new LoginFragment());
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
                    return;
                }
                if(E.isEmpty()|| !E.matches(EMAIL_REGEX)){
                    email.setError("please Enter your Email");
                    return;
                }

                if(P.isEmpty()||P.length()<6){
                    password.setError("please Enter valid Password");
                    return;
                }
                if(!P.equals(C)){
                    confirm.setError("Password not matched");
                    return;
                }
                pBar.setVisibility(View.VISIBLE);
                CAccount(N, E, P);
            }
        });

    }

    private  void  CAccount(final String  name, final String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user =auth.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    uploadUser(user, name, email);

                }
                else {
                    pBar.setVisibility(View.GONE);
                    String exception =task.getException().getMessage();
                    Toast.makeText(getContext(),"Error:"+exception, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void uploadUser(FirebaseUser user, String name, String email){
        Map<String, Object> map =new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("profile", "");
        map.put("userID", user.getUid());
        FirebaseFirestore.getInstance().collection("User").document(user.getUid()).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            assert  getActivity()!=null;
                            startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
                        }
                        else {
                            pBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}