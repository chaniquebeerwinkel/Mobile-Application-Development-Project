package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private EditText edtExpiryDate;

    private Spinner spinnerUnit;

    private Button btnSaveIngredient;
    private Button btnCancelIngredient;

    private DatabaseHelper databaseHelper;

    // Used when editing an existing ingredient
    private int ingredientId = -1;

    private boolean isEditMode = false;

    private String[] units = {
            "pieces",
            "grams",
            "kilograms",
            "millilitres",
            "litres",
            "cups",
            "tablespoons",
            "teaspoons"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_ingredient
        );

        // Connect controls
        edtIngredientName =
                findViewById(R.id.edtIngredientName);

        edtQuantity =
                findViewById(R.id.edtQuantity);

        edtExpiryDate =
                findViewById(R.id.edtExpiryDate);

        spinnerUnit =
                findViewById(R.id.spinnerUnit);

        btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);

        btnCancelIngredient =
                findViewById(R.id.btnCancelIngredient);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // Spinner
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(adapter);

        // Check whether we're editing
        checkEditMode();

        // Save
        btnSaveIngredient.setOnClickListener(
                view -> saveIngredient()
        );

        // Cancel
        btnCancelIngredient.setOnClickListener(
                view -> finish()
        );
    }

    private void checkEditMode() {

        if (getIntent().hasExtra("ingredient_id")) {

            isEditMode = true;

            ingredientId =
                    getIntent().getIntExtra(
                            "ingredient_id",
                            -1
                    );

            String name =
                    getIntent().getStringExtra(
                            "ingredient_name"
                    );

            double quantity =
                    getIntent().getDoubleExtra(
                            "ingredient_quantity",
                            0
                    );

            String unit =
                    getIntent().getStringExtra(
                            "ingredient_unit"
                    );

            String expiry =
                    getIntent().getStringExtra(
                            "ingredient_expiry"
                    );

            // Put existing values into form
            edtIngredientName.setText(name);

            edtQuantity.setText(
                    String.valueOf(quantity)
            );

            edtExpiryDate.setText(expiry);

            // Select existing unit
            for (int i = 0; i < units.length; i++) {

                if (units[i].equals(unit)) {

                    spinnerUnit.setSelection(i);

                    break;
                }
            }

            // Change button text
            btnSaveIngredient.setText(
                    "UPDATE INGREDIENT"
            );
        }
    }

    private void saveIngredient() {

        String name =
                edtIngredientName
                        .getText()
                        .toString()
                        .trim();

        String quantityText =
                edtQuantity
                        .getText()
                        .toString()
                        .trim();

        String unit =
                spinnerUnit
                        .getSelectedItem()
                        .toString();

        String expiryDate =
                edtExpiryDate
                        .getText()
                        .toString()
                        .trim();

        // Validate name
        if (name.isEmpty()) {

            edtIngredientName.setError(
                    "Please enter an ingredient name"
            );

            edtIngredientName.requestFocus();

            return;
        }

        // Validate quantity
        if (quantityText.isEmpty()) {

            edtQuantity.setError(
                    "Please enter a quantity"
            );

            edtQuantity.requestFocus();

            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            edtQuantity.setError(
                    "Please enter a valid number"
            );

            edtQuantity.requestFocus();

            return;
        }

        // Quantity must be positive
        if (quantity <= 0) {

            edtQuantity.setError(
                    "Quantity must be greater than zero"
            );

            edtQuantity.requestFocus();

            return;
        }

        long result;

        if (isEditMode) {

            // UPDATE
            result =
                    databaseHelper.updatePantryItem(
                            ingredientId,
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            if (result > 0) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully!",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Unable to update ingredient.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            // CREATE
            result =
                    databaseHelper.addPantryItem(
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Ingredient added successfully!",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Unable to save ingredient.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}