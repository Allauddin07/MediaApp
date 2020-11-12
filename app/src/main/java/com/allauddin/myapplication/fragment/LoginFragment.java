package com.allauddin.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.allauddin.myapplication.fragment.CreateAcountFragment.EMAIL_REGEX;
//import static com.allauddin.myapplication.fragment.CreateAcountFragment.Email_R;

public class LoginFragment extends Fragment {
    private EditText email, password;
    private TextView signup, forgot;
    private Button logi, googlesignin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private  static  final int RC_SIGN_IN=1;
    GoogleSignInClient mGoogleSignInClient;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        clickList();
    }
    private  void init(View view){
        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        logi=view.findViewById(R.id.login);
        googlesignin=view.findViewById(R.id.googleSignIn);
        signup=view.findViewById(R.id.SignUpTV);
        progressBar=view.findViewById(R.id.pBar);
        forgot=view.findViewById(R.id.forgot);

        auth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(getActivity(), gso);
    }
    private  void clickList(){
        logi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String E=email.getText().toString();
                String P=password.getText().toString();
                if(E.isEmpty()|| !E.matches(EMAIL_REGEX)){
                    email.setError("Enter valid email");
                    return;
                }
                if(P.isEmpty()|| P.length()<6) {
                    password.setError("enter 6 digit valid password");
                    return;

                }
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(E, P).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user=auth.getCurrentUser();
                            if(user.isEmailVerified()){
                                Toast.makeText(getContext(), "Please varify Email", Toast.LENGTH_SHORT).show();
                            }
                            sendUserToMainActivity();
                        }
                        else {
                            String exception=task.getException().getMessage();
                            Toast.makeText(getContext(), "Error:"+exception, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FrgActivity)getActivity()).setFragment(new CreateAcountFragment());
            }
        });
    }
    private void sendUserToMainActivity(){
        if(getActivity()==null)
            return;
        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(getContext().getApplicationContext(), MainActivity.class));
        getActivity().finish();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert  account!=null;
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
               e.printStackTrace();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                           // sendUserToMainActivity();
                            updateUi(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

    private  void updateUi(FirebaseUser user){
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getActivity());
        Map<String, Object> map =new HashMap<>();
        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profile", String.valueOf(account.getPhotoUrl()));
        map.put("userID", user.getUid());
        FirebaseFirestore.getInstance().collection("User").document(user.getUid()).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            assert  getActivity()!=null;
                            progressBar.setVisibility(View.GONE);
                            sendUserToMainActivity();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}