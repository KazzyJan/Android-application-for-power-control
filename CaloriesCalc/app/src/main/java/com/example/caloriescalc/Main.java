package com.example.caloriescalc;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickCalculate(View view){
        Intent intent = new Intent(this, Calculate.class);
        startActivity(intent);
    }
    public void onClickAccount(View view){
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }
    public void onClickProductList(View view){
        Intent intent = new Intent(this, ProductList.class);
        startActivity(intent);
    }
}