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

    private RecipeAdapter recipeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipe
        );


        recyclerRecipes =
                findViewById(
                        R.id.recyclerRecipes
                );

        txtRecipeCount =
                findViewById(
                        R.id.txtRecipeCount
                );

        txtNoRecipes =
                findViewById(
                        R.id.txtNoRecipes
                );


        databaseHelper =
                new DatabaseHelper(this);


        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );


        loadRecipes();
    }


    @Override
    protected void onResume() {

        super.onResume();

        loadRecipes();
    }


    private void loadRecipes() {

        recipes =
                new ArrayList<>();


        /*
         * TEMPORARY:
         * For this step we load all recipes.
         *
         * In the next step we will replace this
         * with the STRICT pantry matching algorithm.
         */

        Cursor cursor =
                databaseHelper.getAllRecipes();


        if (cursor != null) {

            while (cursor.moveToNext()) {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_ID
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_NAME
                                )
                        );

                String description =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_DESCRIPTION
                                )
                        );

                String instructions =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_INSTRUCTIONS
                                )
                        );


                Recipe recipe =
                        new Recipe(
                                id,
                                name,
                                description,
                                instructions
                        );


                recipes.add(recipe);
            }


            cursor.close();
        }


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

        } else {

            txtNoRecipes.setVisibility(
                    TextView.GONE
            );
        }


        recipeAdapter =
                new RecipeAdapter(
                        this,
                        recipes
                );


        recyclerRecipes.setAdapter(
                recipeAdapter
        );
    }
}