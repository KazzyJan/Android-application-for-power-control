package com.example.caloriescalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caloriescalc.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class ProductList extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference products;
    ArrayList<String> productNames = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    Product selectedProduct;
    View addLayout;
    ListView listProd;

    TextView calories;
    TextView fats;
    TextView carbohydrates;
    TextView proteins;
    EditText searchEditText;
    EditText weightEditText;
    Button addButtonFromList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listProd = findViewById(R.id.listProd);

        calories = findViewById(R.id.calories);
        fats = findViewById(R.id.fats);
        carbohydrates = findViewById(R.id.carbohydrates);
        proteins = findViewById(R.id.proteins);
        searchEditText = findViewById(R.id.searchEditText);
        addButtonFromList = findViewById(R.id.addButtonFromList);
        addLayout = findViewById(R.id.addLayout);
        weightEditText = findViewById(R.id.weightEditText);

        addLayout.setVisibility(View.GONE);
        listProd.setVisibility(View.VISIBLE);
        db = FirebaseDatabase.getInstance();
        products = db.getReference("Products");
        final ArrayList<Product> productList = new ArrayList<Product>();


        products.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productNames.clear();
                for (DataSnapshot productSnapshot: snapshot.getChildren()) {
                    Product prod = new Product();
                    prod.setCalories(Integer.parseInt(productSnapshot.child("calories").getValue().toString()));
                    prod.setName(productSnapshot.child("name").getValue().toString());
                    prod.setCarbohydrates(Float.parseFloat(productSnapshot.child("carbohydrates").getValue().toString()));
                    prod.setProteins(Float.parseFloat(productSnapshot.child("proteins").getValue().toString()));
                    prod.setFats(Float.parseFloat(productSnapshot.child("fats").getValue().toString()));
                    productList.add(prod);
                    productNames.add(Objects.requireNonNull(productSnapshot.child("name").getValue()).toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
            }
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames);
        listProd.setAdapter(arrayAdapter);
        listProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                calories.setText("Калории: " + productList.get(i).getCalories());
                fats.setText("Жиры: "+ productList.get(i).getFats());
                carbohydrates.setText("Углеводы: " + productList.get(i).getCarbohydrates());
                proteins.setText("Белки: " + productList.get(i).getProteins());
                selectedProduct = productList.get(i);
                hideKeyboard(ProductList.this);
                addButtonFromList.setEnabled(selectedProduct != null);
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ProductList.this.arrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        addButtonFromList.setEnabled(selectedProduct != null);
    }
    public void onClickMenu(View view){
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
    public void onClickAddFromList(View view){
        addButtonFromList.setEnabled(false);
        addLayout.setVisibility(View.VISIBLE);
        listProd.setVisibility(View.INVISIBLE);
    }
    public void onClickConfirmAdd(View view){
        if (weightEditText.getText().toString().equals("")){
            Toast.makeText(this, "Остались пустые поля", Toast.LENGTH_LONG).show();
        }
        else {
            Calculate.addFromBase(selectedProduct, weightEditText.getText().toString());
            addLayout.setVisibility(View.GONE);
            listProd.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, Calculate.class);
            startActivity(intent);
        }
    }
    public static void hideKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}