package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtBack;
    private TextView txtRecipeName;
    private TextView txtRecipeDescription;
    private TextView txtAvailability;
    private TextView txtIngredients;
    private TextView txtInstructions;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        // Create database helper
        databaseHelper = new DatabaseHelper(this);

        // Connect Java variables to XML views
        txtBack = findViewById(R.id.txtBack);
        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtRecipeDescription = findViewById(R.id.txtRecipeDescription);
        txtAvailability = findViewById(R.id.txtAvailability);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtInstructions = findViewById(R.id.txtInstructions);

        // Back button
        txtBack.setOnClickListener(v -> finish());

        // Get the recipe ID sent from RecipeAdapter
        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        // Check that a valid recipe ID was received
        if (recipeId == -1) {
            finish();
            return;
        }

        // Load the recipe information
        loadRecipe(recipeId);
    }

    private void loadRecipe(int recipeId) {

        Cursor cursor = databaseHelper.getAllRecipes();

        try {

            if (cursor != null && cursor.moveToFirst()) {

                int idColumn = cursor.getColumnIndex("id");
                int nameColumn = cursor.getColumnIndex("name");
                int descriptionColumn = cursor.getColumnIndex("description");
                int instructionsColumn = cursor.getColumnIndex("instructions");

                do {

                    int currentId = cursor.getInt(idColumn);

                    if (currentId == recipeId) {

                        String recipeName =
                                cursor.getString(nameColumn);

                        String recipeDescription =
                                cursor.getString(descriptionColumn);

                        String recipeInstructions =
                                cursor.getString(instructionsColumn);

                        txtRecipeName.setText(recipeName);

                        txtRecipeDescription.setText(
                                recipeDescription
                        );

                        txtInstructions.setText(
                                recipeInstructions
                        );

                        loadIngredients(recipeId);

                        txtAvailability.setText(
                                "✓ You have all the required ingredients"
                        );

                        return;
                    }

                } while (cursor.moveToNext());
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        // Recipe was not found
        finish();
    }

    private void loadIngredients(int recipeId) {

        StringBuilder ingredientsText = new StringBuilder();

        Cursor cursor = databaseHelper.getRecipeIngredients(recipeId);

        try {

            if (cursor != null && cursor.moveToFirst()) {

                do {

                    int nameColumn = cursor.getColumnIndex(
                            "ingredient_name"
                    );

                    int quantityColumn = cursor.getColumnIndex(
                            "required_quantity"
                    );

                    int unitColumn = cursor.getColumnIndex(
                            "unit"
                    );

                    String ingredientName =
                            cursor.getString(nameColumn);

                    double quantity =
                            cursor.getDouble(quantityColumn);

                    String unit =
                            cursor.getString(unitColumn);

                    ingredientsText
                            .append("• ")
                            .append(ingredientName)
                            .append(" — ")
                            .append(quantity)
                            .append(" ")
                            .append(unit)
                            .append("\n");

                } while (cursor.moveToNext());

            } else {

                ingredientsText.append(
                        "No ingredients found."
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        txtIngredients.setText(
                ingredientsText.toString()
        );
    }
}