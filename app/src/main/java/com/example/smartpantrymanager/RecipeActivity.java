package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private TextView txtRecipeCount;
    private TextView txtNoRecipes;

    private DatabaseHelper databaseHelper;

    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);

        recyclerRecipes =
                findViewById(R.id.recyclerRecipes);

        txtRecipeCount =
                findViewById(R.id.txtRecipeCount);

        txtNoRecipes =
                findViewById(R.id.txtNoRecipes);

        databaseHelper =
                new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadMatchingRecipes();
    }


    @Override
    protected void onResume() {

        super.onResume();

        loadMatchingRecipes();
    }

// Only load recipes that you can actually make
    private void loadMatchingRecipes() {

        recipes = new ArrayList<>();

        Cursor recipeCursor =
                databaseHelper.getAllRecipes();

        if (recipeCursor != null) {

            while (recipeCursor.moveToNext()) {

                int recipeId =
                        recipeCursor.getInt(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_ID
                                )
                        );

                String recipeName =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_NAME
                                )
                        );

                String description =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_DESCRIPTION
                                )
                        );

                String instructions =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_INSTRUCTIONS
                                )
                        );

                // Check whether every ingredient is available.
                boolean canMakeRecipe =
                        canMakeRecipe(recipeId);

                if (canMakeRecipe) {

                    Recipe recipe =
                            new Recipe(
                                    recipeId,
                                    recipeName,
                                    description,
                                    instructions
                            );

                    recipes.add(recipe);
                }
            }

            recipeCursor.close();
        }

        displayRecipes();
    }

    // Check one recipe.

    private boolean canMakeRecipe(int recipeId) {

        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(recipeId);


        if (ingredientCursor == null) {
            return false;
        }

        while (ingredientCursor.moveToNext()) {

            String requiredIngredient =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_INGREDIENT_NAME
                            )
                    );

            double requiredQuantity =
                    ingredientCursor.getDouble(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_REQUIRED_QUANTITY
                            )
                    );

            String requiredUnit =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_UNIT
                            )
                    );

            boolean ingredientAvailable =
                    isIngredientAvailable(
                            requiredIngredient,
                            requiredQuantity,
                            requiredUnit
                    );


            // If ONE ingredient is missing, the entire recipe will be rejected.

            if (!ingredientAvailable) {

                ingredientCursor.close();

                return false;
            }
        }

        ingredientCursor.close();

        return true;
    }


    // Check pantry for one ingredient

    private boolean isIngredientAvailable(
            String requiredIngredient,
            double requiredQuantity,
            String requiredUnit) {


        Cursor pantryCursor =
                databaseHelper.getAllPantryItems();


        if (pantryCursor == null) {
            return false;
        }

        double availableQuantity = 0;

        while (pantryCursor.moveToNext()) {

            String pantryIngredient =
                    pantryCursor.getString(
                            pantryCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_NAME
                            )
                    );

            double pantryQuantity =
                    pantryCursor.getDouble(
                            pantryCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_QUANTITY
                            )
                    );

            String pantryUnit =
                    pantryCursor.getString(
                            pantryCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_UNIT
                            )
                    );


            // Compare ingredient names without worrying about capital letters.

            if (normalizeName(pantryIngredient)
                    .equals(normalizeName(requiredIngredient))) {


                Double convertedQuantity =
                        convertQuantity(
                                pantryQuantity,
                                pantryUnit,
                                requiredUnit
                        );


                if (convertedQuantity != null) {

                    availableQuantity +=
                            convertedQuantity;
                }
            }
        }


        pantryCursor.close();

        return availableQuantity >= requiredQuantity;
    }

    // Normalize ingredient name

    private String normalizeName(String name) {

        if (name == null) {
            return "";
        }

        return name
                .trim()
                .toLowerCase();
    }

    // Unit conversion

    private Double convertQuantity(
            double quantity,
            String fromUnit,
            String toUnit) {


        String from =
                fromUnit.trim().toLowerCase();

        String to =
                toUnit.trim().toLowerCase();


        // Same unit

        if (from.equals(to)) {
            return quantity;
        }

        // Weight

        if (from.equals("kilograms")
                && to.equals("grams")) {

            return quantity * 1000;
        }

        if (from.equals("grams")
                && to.equals("kilograms")) {

            return quantity / 1000;
        }


        // Liquid

        if (from.equals("litres")
                && to.equals("millilitres")) {

            return quantity * 1000;
        }

        if (from.equals("millilitres")
                && to.equals("litres")) {

            return quantity / 1000;
        }

        // Cups/ tablespoons/ teaspoons

        if (from.equals("cups")
                && to.equals("tablespoons")) {

            return quantity * 16;
        }


        if (from.equals("tablespoons")
                && to.equals("cups")) {

            return quantity / 16;
        }

        if (from.equals("cups")
                && to.equals("teaspoons")) {

            return quantity * 48;
        }

        if (from.equals("teaspoons")
                && to.equals("cups")) {

            return quantity / 48;
        }

        if (from.equals("tablespoons")
                && to.equals("teaspoons")) {

            return quantity * 3;
        }

        if (from.equals("teaspoons")
                && to.equals("tablespoons")) {

            return quantity / 3;
        }

        // Incompatible units

        return null;
    }

    //Display matching recipes

    private void displayRecipes() {

        int count =
                recipes.size();


        txtRecipeCount.setText(
                count +
                        (count == 1
                                ? " recipe available"
                                : " recipes available")
        );

        if (recipes.isEmpty()) {

            txtNoRecipes.setVisibility(
                    TextView.VISIBLE
            );

            recyclerRecipes.setVisibility(
                    RecyclerView.GONE
            );

        } else {

            txtNoRecipes.setVisibility(
                    TextView.GONE
            );

            recyclerRecipes.setVisibility(
                    RecyclerView.VISIBLE
            );
        }

        RecipeAdapter recipeAdapter = new RecipeAdapter(
                this,
                recipes
        );

        recyclerRecipes.setAdapter(
                recipeAdapter
        );
    }
}