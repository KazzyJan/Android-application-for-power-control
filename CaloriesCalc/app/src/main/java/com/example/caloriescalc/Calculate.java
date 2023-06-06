package com.example.caloriescalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caloriescalc.Models.CaloriesStatistic;
import com.example.caloriescalc.Models.Product;
import com.example.caloriescalc.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Calculate extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference users;
    User thisUser = new User();
    private static ArrayAdapter<String> arrayAdapter;
    View nestedScrollView;
    ProgressBar progressBar;
    View enterDataLayout;
    TextView head1;
    TextView dailyCaloriesNorm;
    TextView calInDayTextView;
    static ListView calcListProd;
    Button openProdList;
    Button enterUserData;
    ArrayList<CaloriesStatistic> statList = new ArrayList<>();
    static ArrayList<Product> selectedProducts = new ArrayList<>();
    static ArrayList<String> productNames = new ArrayList<>();
    static ArrayList<Float> weightList = new ArrayList<>();
    static float caloriesInThisDay;
    static double dailyCalories;
    boolean endDataRead = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        thisUser.setUid(uid);
        thisUser.setStatList(statList);
        dailyCaloriesNorm = findViewById(R.id.dailyCaloriesNorm);
        calcListProd = findViewById(R.id.calcListProd);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        enterDataLayout = findViewById(R.id.enterDataLayout);
        enterUserData = findViewById(R.id.enterUserData);
        openProdList = findViewById(R.id.openProdList);
        progressBar = findViewById(R.id.progressBar);
        calInDayTextView = findViewById(R.id.calInDayTextView);
        head1 = findViewById(R.id.head1);

        enterUserData.setVisibility(View.GONE);
        openProdList.setVisibility(View.GONE);
        enterDataLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);

        users.child(uid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String discrAge = snapshot.child("age").getValue().toString();
                    thisUser.setAge(discrAge);
                    String discrWeight = snapshot.child("weight").getValue().toString();
                    thisUser.setWeight(discrWeight);
                    String discrGender = snapshot.child("gender").getValue().toString();
                    thisUser.setGender(discrGender);
                    String discrHeight = snapshot.child("height").getValue().toString();
                    thisUser.setHeight(discrHeight);



                    for (DataSnapshot statSnapshot: snapshot.child("statList").getChildren()) {
                        CaloriesStatistic thisStat = new CaloriesStatistic();
                        thisStat.setCaloriesInThisDay(Float.parseFloat(statSnapshot
                                .child("caloriesInThisDay").getValue().toString()));
                        thisStat.setDate(statSnapshot.child("date").getValue().toString());
                        thisUser.addStatList(thisStat);
                    }


                    endDataRead = true;
                    calculateNorm(thisUser);
                    ViewGroup.LayoutParams paramsPrim = head1.getLayoutParams();
                    dailyCaloriesNorm.setTextSize(28);
                    ViewGroup.LayoutParams params = dailyCaloriesNorm.getLayoutParams();
                    params.width = paramsPrim.width/2;
                    dailyCaloriesNorm.setLayoutParams(params);
                    dailyCaloriesNorm.setText("\n"+"Из " + String.format("%.0f", dailyCalories));



                    SimpleDateFormat simpledateFormat = new SimpleDateFormat("dd+MM+yyyy");
                    String date = simpledateFormat.format(Calendar.getInstance().getTime());
                    if(thisUser.getStatThisDay(date) != null){
                        caloriesInThisDay = thisUser.getStatThisDay(date).getCaloriesInThisDay();
                        //Toast.makeText(Calculate.this, Float.toString(thisUser.getStatThisDay(date)
                        //        .getCaloriesInThisDay()), Toast.LENGTH_LONG).show();
                    }
                    else{
                        //Toast.makeText(Calculate.this, "Запись не найдена", Toast.LENGTH_LONG).show();
                    }
                    calInDayTextView.setText("\n" + String.format("%.0f", caloriesInThisDay));
                    progressBar.setProgress((int) (caloriesInThisDay/dailyCalories*100));
                    if (caloriesInThisDay > dailyCalories){
                        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
                        progressDrawable.setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                        progressBar.setProgressDrawable(progressDrawable);
                    }
                }
                else {
                    thisUser.setUid(null);
                    ViewGroup.LayoutParams paramsPrim = head1.getLayoutParams();
                    dailyCaloriesNorm.setTextSize(20);
                    ViewGroup.LayoutParams params = dailyCaloriesNorm.getLayoutParams();
                    params.width = paramsPrim.width;
                    dailyCaloriesNorm.setLayoutParams(params);
                    dailyCaloriesNorm.setGravity(10);
                    dailyCaloriesNorm.setText("Введите данные о себе в настройках аккаунта, чтобы видеть прогресс.");
                    calInDayTextView.setText("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames);
        calcListProd.setAdapter(arrayAdapter);

        calcListProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                arrayAdapter.remove(productNames.get(i));
                selectedProducts.remove(selectedProducts.get(i));
                weightList.remove(weightList.get(i));
            }
        });
    }
    public void onClickMenu(View view){
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }


    /*Чтобы рассчитать свою потребность в калораже, нужно измерить свой вес.
     Затем измерить свой рост и определить суточную норму калорий по формуле:
     1. Qж = (рост в см х1,8) — (возраст в годах х 4,7) + (вес тела в кг х 9,6) + 655,
     где Qж — количество суточной нормы калорий для женщин.
     2. Qм = (рост в см х 5) — (возраст в годах х 6,8) + (вес тела в кг х13,7) + 66,
     где Qм -количество суточной нормы калорий для мужчин.
    */

    private void calculateNorm(User thisUser){
        if (thisUser.getUid() != null) {
            if (Objects.equals(thisUser.getGender(), "Мужской")) {
                dailyCalories = ((Float.parseFloat(thisUser.getHeight()) * 6.25) - (Float.parseFloat(thisUser.getAge()) * 5) + (Float.parseFloat(thisUser.getWeight()) * 10) + 5) * 1.375;
            } else if (Objects.equals(thisUser.getGender(), "Женский")) {
                dailyCalories = ((Float.parseFloat(thisUser.getHeight()) * 6.25) - (Float.parseFloat(thisUser.getAge()) * 5) + (Float.parseFloat(thisUser.getWeight()) * 10) - 161) * 1.375;
            }
        }
        else{
            dailyCalories = 2500.0F;
        }
    }
    public void onClickAdd(View view){
        nestedScrollView.setVisibility(View.GONE);
        enterUserData.setVisibility(View.VISIBLE);
        openProdList.setVisibility(View.VISIBLE);
        enterDataLayout.setVisibility(View.GONE);
    }
    public void onClickEnterData(View view){
        enterDataLayout.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);
        enterUserData.setVisibility(View.GONE);
        openProdList.setVisibility(View.GONE);
    }
    public void onClickAddFromBase(View view){
        Intent intent = new Intent(this, ProductList.class);
        startActivity(intent);
    }
    public void onClickAddEnteredData(View view){
        EditText nameEnteredProd = findViewById(R.id.nameEnteredProd);
        EditText calEnteredProd = findViewById(R.id.calEnteredProd);
        EditText weigthEnteredProd = findViewById(R.id.weigthEnteredProd);

        boolean check = addEnteredProd(nameEnteredProd, calEnteredProd, weigthEnteredProd);
        if (check){
            enterDataLayout.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            enterUserData.setVisibility(View.GONE);
            openProdList.setVisibility(View.GONE);
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames);
            calcListProd.setAdapter(arrayAdapter);
        }
    }
    public void onClickResult(View view){
        if (thisUser.getUid() == null){
            calculate();
            calInDayTextView.setText(String.format("%.0f", caloriesInThisDay));
            caloriesInThisDay = 0;
            selectedProducts.clear();
            weightList.clear();
            productNames.clear();
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames);
            calcListProd.setAdapter(arrayAdapter);
        }
        else {
            calculate();
            selectedProducts.clear();
            weightList.clear();
            productNames.clear();
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, productNames);
            calcListProd.setAdapter(arrayAdapter);
            saveStatOnBase();

            progressBar.setProgress((int) (caloriesInThisDay / dailyCalories * 100));
            if (caloriesInThisDay > dailyCalories) {
                Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                progressBar.setProgressDrawable(progressDrawable);
            }
            calInDayTextView.setText(String.format("%.0f", caloriesInThisDay));
        }
    }
    private void saveStatOnBase(){
        SimpleDateFormat simpledateFormat = new SimpleDateFormat("dd+MM+yyyy");
        String date = simpledateFormat.format(Calendar.getInstance().getTime());

        CaloriesStatistic stat = new CaloriesStatistic(caloriesInThisDay, date);
        thisUser.addStatList(stat);
        users.child(thisUser.getUid()).child("statList").child(date)
                .setValue(thisUser.getStatListToday());
        thisUser.getStatThisDay(date).setCaloriesInThisDay(caloriesInThisDay);
    }
    private boolean addEnteredProd(EditText nameEnteredProd, EditText calEnteredProd, EditText weigthEnteredProd) {
        Product product = new Product();
        if ((nameEnteredProd.getText().toString().equals("")) || (calEnteredProd.getText().toString().equals(""))
                || (weigthEnteredProd.getText().toString().equals("")) ){
            Toast.makeText(this, "Остались пустые поля", Toast.LENGTH_LONG).show();
            return false;
        }
        product.setName(nameEnteredProd.getText().toString());
        product.setCalories(Integer.parseInt(calEnteredProd.getText().toString()));
        selectedProducts.add(product);
        weightList.add(Float.parseFloat(calEnteredProd.getText().toString()));
        productNames.add(product.getName() + "\n" + weigthEnteredProd.getText().toString() + " грамм");
        return true;
    }
    public static void addFromBase(Product selectedProduct, String weight){
        selectedProducts.add(selectedProduct);
        weightList.add(Float.parseFloat(weight));
        productNames.add(selectedProduct.getName() + "\n" + weight + " грамм");
    }
    private void calculate(){
        for (int i = 0; i< selectedProducts.size(); i++){
            caloriesInThisDay += selectedProducts.get(i).getCalories()/100f*weightList.get(i);
        }
    }
}