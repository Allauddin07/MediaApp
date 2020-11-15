package com.allauddin.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.allauddin.myapp.Adapter.P_adepter;
import com.allauddin.myapp.Model.P_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth ;
    RecyclerView recyclerView;
    P_adepter postAdapter;
    List<P_model> postModelList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true); // this will load and show latest post

        recyclerView.setLayoutManager(layoutManager);

        postModelList = new ArrayList<>();

        //getting data from firebase
        getPosts();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mian_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.action_logout){
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
        if(item.getItemId() == R.id.action_add_post){
            startActivity(new Intent(HomeActivity.this , PostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // when user is on main screen and press tge back button then shut downd the application
    }

    private void getPosts() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postModelList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    P_model postModel = ds.getValue(P_model.class);
                    postModelList.add(postModel);
                    postAdapter = new P_adepter(HomeActivity.this , postModelList);
                    recyclerView.setAdapter(postAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }



}