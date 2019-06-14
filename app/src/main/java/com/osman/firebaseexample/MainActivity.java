package com.osman.firebaseexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUser();
    }

    private void checkUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        initViews();
        initActions();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        add = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recycler);
        setSupportActionBar(toolbar);
        mReference = FirebaseDatabase.getInstance().getReference().child("Data");
    }

    private void initActions() {
        adapter = new DataAdapter(this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 20));
        recyclerView.setAdapter(adapter);

        mReference.addChildEventListener(dataListener);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int random = new Random().nextInt(10);
                Data item = new Data();
                item.setTitle("Example Title " + random);
                item.setDesc("Example Description " + random);
                mReference.push().setValue(item);
            }
        });
    }

    private ChildEventListener dataListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Data item = dataSnapshot.getValue(Data.class);
            item.setId(dataSnapshot.getKey());
            adapter.addData(item);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Data item = dataSnapshot.getValue(Data.class);
            item.setId(dataSnapshot.getKey());
            adapter.updateData(item);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Data item = dataSnapshot.getValue(Data.class);
            item.setId(dataSnapshot.getKey());
            adapter.removeData(item);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mReference.removeEventListener(dataListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            checkUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
