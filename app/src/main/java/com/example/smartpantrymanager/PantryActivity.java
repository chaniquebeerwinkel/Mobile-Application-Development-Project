package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    private TextView txtEmptyPantry;
    private TextView txtItemCount;

    private RecyclerView recyclerPantry;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pantry);

        // Connect XML controls
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

        txtEmptyPantry =
                findViewById(R.id.txtEmptyPantry);

        txtItemCount =
                findViewById(R.id.txtItemCount);

        recyclerPantry =
                findViewById(R.id.recyclerPantry);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // RecyclerView setup
        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Add ingredient button
        btnAddIngredient.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        loadPantryItems();
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadPantryItems();
    }

    private void loadPantryItems() {

        ArrayList<PantryItem> pantryItems = new ArrayList<>();

        Cursor cursor =
                databaseHelper.getAllPantryItems();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_QUANTITY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_UNIT
                        )
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_EXPIRY_DATE
                        )
                );

                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                pantryItems.add(item);
            }

            cursor.close();
        }

        // Update item count
        int count = pantryItems.size();

        txtItemCount.setText(
                count + (count == 1
                        ? " item in your pantry"
                        : " items in your pantry")
        );

        // Empty message
        if (pantryItems.isEmpty()) {

            txtEmptyPantry.setVisibility(
                    TextView.VISIBLE
            );

        } else {

            txtEmptyPantry.setVisibility(
                    TextView.GONE
            );
        }

        // Adapter
        PantryAdapter pantryAdapter = new PantryAdapter(
                this,
                pantryItems
        );

        recyclerPantry.setAdapter(
                pantryAdapter
        );
    }
}