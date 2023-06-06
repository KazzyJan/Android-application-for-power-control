package com.example.caloriescalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caloriescalc.Models.CaloriesStatistic;
import com.example.caloriescalc.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Account extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Loging loging = new Loging();
    FirebaseDatabase db;
    DatabaseReference users;

    Button enterBoimetric;
    View enterBiometricLayout;
    View biometricLayout;
    TextView showWeight;
    TextView showAge;
    TextView showHeight;
    TextView showGender;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String discrAge;

        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        enterBiometricLayout = findViewById(R.id.enterBiometricLayout);
        biometricLayout = findViewById(R.id.biometricLayout);
        enterBiometricLayout.setVisibility(View.INVISIBLE);
        enterBoimetric = findViewById(R.id.enterBoimetric);

        showWeight = findViewById(R.id.showWeight);
        showAge = findViewById(R.id.showAge);
        showHeight = findViewById(R.id.showHeight);
        showGender = findViewById(R.id.showGender);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        users.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String discrAge = snapshot.child("age").getValue().toString();
                    showAge.setText(discrAge);
                    String discrWeight = snapshot.child("weight").getValue().toString();
                    showWeight.setText(discrWeight);
                    String discrGender = snapshot.child("gender").getValue().toString();
                    showGender.setText(discrGender);
                    String discrHeight = snapshot.child("height").getValue().toString();
                    showHeight.setText(discrHeight);
                }
                else {
                    showWeight.setText("Нет данных");
                    showAge.setText("Нет данных");
                    showGender.setText("Нет данных");
                    showHeight.setText("Нет данных");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onClickMain(View view){
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
    public void onClickSignOut(View view){
        loging.singOut();
        Intent intent = new Intent(this, Loging.class);
        startActivity(intent);
    }
    public void onClickEnterBio(View view){
        enterBoimetric.setVisibility(View.INVISIBLE);
        enterBiometricLayout.setVisibility(View.VISIBLE);
        biometricLayout.setVisibility(View.INVISIBLE);
    }
    public void onClickDone(View view){
        EditText age = findViewById(R.id.editAge);
        EditText weight = findViewById(R.id.editWeight);
        EditText height = findViewById(R.id.editHeight);
        Spinner gender = findViewById(R.id.spinnerGender);

        boolean check = addBiometricToBase(age, weight, height, gender);

        if (check) {
            enterBoimetric.setVisibility(View.VISIBLE);
            enterBiometricLayout.setVisibility(View.INVISIBLE);
            biometricLayout.setVisibility(View.VISIBLE);
        }
    }
    private boolean addBiometricToBase(EditText age, EditText weight, EditText height, Spinner gender){
        User user = new User();
        if ((age.getText().toString().equals("")) || (height.getText().toString().equals(""))
                || (weight.getText().toString().equals("")) ){
            Toast.makeText(this, "Остались пустые поля", Toast.LENGTH_LONG).show();
            return false;
        }
        ArrayList<CaloriesStatistic> statList = new ArrayList<>();
        user.setAge(age.getText().toString());
        user.setGender(gender.getSelectedItem().toString());
        user.setHeight(height.getText().toString());
        user.setWeight(weight.getText().toString());
        user.setUid(Loging.signedInAccountId);
        user.setStatList(statList);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        users.child(currentUser.getUid())
                .setValue(user);
        return true;
    }

}