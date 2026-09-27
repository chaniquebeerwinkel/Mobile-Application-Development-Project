package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PantryActivity extends AppCompatActivity {

    private Button btnAddIngredient;
    private TextView txtEmptyPantry;
    private TextView txtItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pantry);

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);
        txtItemCount = findViewById(R.id.txtItemCount);

        // Open Add Ingredient screen
        btnAddIngredient.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });
    }
}