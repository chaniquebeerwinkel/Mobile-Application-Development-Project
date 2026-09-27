package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;


    // Pantry table

    public static final String TABLE_PANTRY = "pantry_items";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    // Recipe table

    public static final String TABLE_RECIPES = "recipes";

    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_DESCRIPTION = "description";
    public static final String RECIPE_INSTRUCTIONS = "instructions";


    // Recipe ingredient table

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String RECIPE_INGREDIENT_ID = "id";
    public static final String RECIPE_ID_FK = "recipe_id";
    public static final String RECIPE_INGREDIENT_NAME =
            "ingredient_name";
    public static final String RECIPE_REQUIRED_QUANTITY =
            "required_quantity";
    public static final String RECIPE_UNIT = "unit";


    // Create pantry table

    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY + " REAL NOT NULL, " +
                    COLUMN_UNIT + " TEXT NOT NULL, " +
                    COLUMN_EXPIRY_DATE + " TEXT" +
                    ")";


    // Create recipe table

    private static final String CREATE_RECIPE_TABLE =
            "CREATE TABLE " + TABLE_RECIPES + " (" +
                    RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RECIPE_NAME + " TEXT NOT NULL, " +
                    RECIPE_DESCRIPTION + " TEXT, " +
                    RECIPE_INSTRUCTIONS + " TEXT" +
                    ")";


    // Create recipe ingredient table

    private static final String CREATE_RECIPE_INGREDIENT_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                    RECIPE_INGREDIENT_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                    RECIPE_ID_FK +
                    " INTEGER NOT NULL, " +

                    RECIPE_INGREDIENT_NAME +
                    " TEXT NOT NULL, " +

                    RECIPE_REQUIRED_QUANTITY +
                    " REAL NOT NULL, " +

                    RECIPE_UNIT +
                    " TEXT NOT NULL, " +

                    "FOREIGN KEY (" + RECIPE_ID_FK + ") " +
                    "REFERENCES " + TABLE_RECIPES +
                    "(" + RECIPE_ID + ")" +
                    ")";


    // The constructor

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // On create

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PANTRY_TABLE);

        db.execSQL(CREATE_RECIPE_TABLE);

        db.execSQL(CREATE_RECIPE_INGREDIENT_TABLE);

        insertDefaultRecipes(db);
    }

    // On upgrade

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            db.execSQL(CREATE_RECIPE_TABLE);

            db.execSQL(CREATE_RECIPE_INGREDIENT_TABLE);

            insertDefaultRecipes(db);
        }
    }

    // Methods for pantry

    public long addPantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        long result =
                db.insert(TABLE_PANTRY, null, values);

        db.close();

        return result;
    }


    public Cursor getAllPantryItems() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }


    public Cursor getPantryItem(int id) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }


    public int updatePantryItem(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        int result =
                db.update(
                        TABLE_PANTRY,
                        values,
                        COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)}
                );

        db.close();

        return result;
    }


    public int deletePantryItem(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        TABLE_PANTRY,
                        COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)}
                );

        db.close();

        return result;
    }


    public int getPantryItemCount() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM " +
                                TABLE_PANTRY,
                        null
                );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        db.close();

        return count;
    }

    // Recipe methods

    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String description,
            String instructions) {

        ContentValues values =
                new ContentValues();

        values.put(RECIPE_NAME, name);
        values.put(RECIPE_DESCRIPTION, description);
        values.put(RECIPE_INSTRUCTIONS, instructions);

        db.insert(
                TABLE_RECIPES,
                null,
                values
        );
    }


    private long addRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        ContentValues values =
                new ContentValues();

        values.put(
                RECIPE_ID_FK,
                recipeId
        );

        values.put(
                RECIPE_INGREDIENT_NAME,
                ingredientName
        );

        values.put(
                RECIPE_REQUIRED_QUANTITY,
                quantity
        );

        values.put(
                RECIPE_UNIT,
                unit
        );

        return db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }


    public Cursor getAllRecipes() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " ASC"
        );
    }


    public Cursor getRecipe(int recipeId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                RECIPE_ID + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );
    }


    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RECIPE_ID_FK + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                RECIPE_INGREDIENT_NAME + " ASC"
        );
    }

    // Default recipes

    private void insertDefaultRecipes(SQLiteDatabase db) {


        // 1. EASY CHICKEN ENCHILADAS

        addRecipeWithIngredients(
                db,
                "Easy Chicken Enchiladas",
                "Chicken enchiladas covered in a flavorful sauce.",
                "Prepare the chicken filling, assemble the enchiladas, cover with sauce and cheese, then bake until heated through.",
                new String[][]{
                        {"chicken", "500", "grams"},
                        {"tortillas", "8", "pieces"},
                        {"enchilada sauce", "400", "millilitres"},
                        {"cheese", "200", "grams"},
                        {"onion", "1", "pieces"}
                }
        );


        // 2. WORLD'S BEST LASAGNA

        addRecipeWithIngredients(
                db,
                "World's Best Lasagna",
                "A layered lasagna with meat sauce, pasta and cheese.",
                "Prepare the meat sauce and cheese mixture, layer with lasagna noodles and bake until hot and bubbling.",
                new String[][]{
                        {"ground beef", "500", "grams"},
                        {"lasagna noodles", "12", "pieces"},
                        {"tomato sauce", "700", "millilitres"},
                        {"ricotta", "400", "grams"},
                        {"mozzarella cheese", "250", "grams"},
                        {"Parmesan cheese", "100", "grams"}
                }
        );


        // 3. BEEF STEW

        addRecipeWithIngredients(
                db,
                "Beef Stew",
                "A hearty beef stew with vegetables.",
                "Brown the beef, add the vegetables and liquid, then simmer until the meat and vegetables are tender.",
                new String[][]{
                        {"beef", "600", "grams"},
                        {"potatoes", "4", "pieces"},
                        {"carrots", "3", "pieces"},
                        {"onion", "1", "pieces"},
                        {"beef stock", "750", "millilitres"}
                }
        );


        // 4. BAKED ZITI

        addRecipeWithIngredients(
                db,
                "Baked Ziti",
                "Baked pasta with ground beef, tomato sauce and cheese.",
                "Cook the pasta, prepare the meat and tomato sauce, combine with cheese and bake until golden.",
                new String[][]{
                        {"ziti pasta", "500", "grams"},
                        {"ground beef", "500", "grams"},
                        {"tomato sauce", "700", "millilitres"},
                        {"ricotta", "250", "grams"},
                        {"mozzarella cheese", "250", "grams"},
                        {"Parmesan cheese", "100", "grams"}
                }
        );


        // 5. BEST DAMN CHILI

        addRecipeWithIngredients(
                db,
                "Best Damn Chili",
                "A rich and hearty chili with meat, beans and tomatoes.",
                "Brown the meat, add the vegetables, beans and tomatoes, season and simmer until the flavours develop.",
                new String[][]{
                        {"ground beef", "500", "grams"},
                        {"kidney beans", "400", "grams"},
                        {"tomatoes", "400", "grams"},
                        {"onion", "1", "pieces"},
                        {"chili powder", "2", "tablespoons"},
                        {"beer", "330", "millilitres"}
                }
        );


        // 6. THAI CHICKEN CURRY IN COCONUT MILK

        addRecipeWithIngredients(
                db,
                "Thai Chicken Curry in Coconut Milk",
                "Chicken breast, bell peppers and onions in a coconut curry sauce.",
                "Cook the chicken and vegetables, add the curry ingredients and coconut milk, then simmer until cooked.",
                new String[][]{
                        {"chicken", "500", "grams"},
                        {"bell peppers", "2", "pieces"},
                        {"onion", "1", "pieces"},
                        {"coconut milk", "400", "millilitres"},
                        {"curry paste", "2", "tablespoons"},
                        {"rice", "300", "grams"}
                }
        );


        // 7. THE BEST MEATLOAF

        addRecipeWithIngredients(
                db,
                "The Best Meatloaf",
                "A classic meatloaf suitable for dinner and leftovers.",
                "Combine the meat with the other ingredients, shape into a loaf, add the topping and bake until cooked.",
                new String[][]{
                        {"ground beef", "700", "grams"},
                        {"breadcrumbs", "100", "grams"},
                        {"egg", "2", "pieces"},
                        {"onion", "1", "pieces"},
                        {"ketchup", "100", "millilitres"},
                        {"milk", "120", "millilitres"}
                }
        );


        // 8. MY FAVORITE CHICKEN SALAD

        addRecipeWithIngredients(
                db,
                "My Favorite Chicken Salad",
                "A creamy chicken salad with celery, pecans and sweet pickle relish.",
                "Combine the cooked chicken with the dressing and remaining ingredients. Chill before serving.",
                new String[][]{
                        {"chicken", "400", "grams"},
                        {"mayonnaise", "150", "millilitres"},
                        {"celery", "2", "pieces"},
                        {"pecans", "100", "grams"},
                        {"pickle relish", "50", "millilitres"},
                        {"dill", "2", "tablespoons"}
                }
        );


        // 9. CHANA MASALA

        addRecipeWithIngredients(
                db,
                "Chana Masala",
                "Chickpeas cooked in a spiced tomato sauce.",
                "Cook the onion and spices, add the tomatoes and chickpeas, then simmer until the flavours combine.",
                new String[][]{
                        {"chickpeas", "400", "grams"},
                        {"tomatoes", "400", "grams"},
                        {"onion", "1", "pieces"},
                        {"garlic", "2", "pieces"},
                        {"ginger", "1", "pieces"},
                        {"rice", "300", "grams"}
                }
        );


        // 10. SWEET AND SPICY GINGER BEER PULLED PORK

        addRecipeWithIngredients(
                db,
                "Sweet and Spicy Ginger Beer Pulled Pork",
                "Tender pulled pork with a sweet and spicy flavour.",
                "Cook the pork with the flavouring ingredients until tender, then shred and combine with the cooking sauce.",
                new String[][]{
                        {"pork", "1000", "grams"},
                        {"ginger beer", "500", "millilitres"},
                        {"brown sugar", "100", "grams"},
                        {"ginger", "2", "pieces"},
                        {"garlic", "3", "pieces"},
                        {"pineapple", "200", "grams"}
                }
        );


        // 11. CHEF JOHN'S AMERICAN GOULASH

        addRecipeWithIngredients(
                db,
                "Chef John's American Goulash",
                "Ground beef and macaroni in a tomato-based sauce.",
                "Brown the beef, add the tomato sauce and seasonings, then cook with macaroni until tender.",
                new String[][]{
                        {"ground beef", "500", "grams"},
                        {"macaroni", "400", "grams"},
                        {"tomato sauce", "500", "millilitres"},
                        {"onion", "1", "pieces"},
                        {"garlic", "2", "pieces"},
                        {"parsley", "2", "tablespoons"}
                }
        );


        // 12. ZIPPY SHEPHERD'S PIE

        addRecipeWithIngredients(
                db,
                "Zippy Shepherd's Pie",
                "A savoury shepherd's pie with vegetables and mashed potatoes.",
                "Prepare the meat and vegetable filling, top with mashed potatoes and bake until heated and golden.",
                new String[][]{
                        {"ground beef", "500", "grams"},
                        {"potatoes", "5", "pieces"},
                        {"carrots", "2", "pieces"},
                        {"peas", "200", "grams"},
                        {"onion", "1", "pieces"},
                        {"ketchup", "50", "millilitres"}
                }
        );


        // 13. STUFFED MEXICAN PEPPERS

        addRecipeWithIngredients(
                db,
                "Stuffed Mexican Peppers",
                "Bell peppers filled with ground beef, rice and tomato sauce.",
                "Prepare the beef and rice filling, stuff the peppers and bake until the peppers are tender.",
                new String[][]{
                        {"bell peppers", "4", "pieces"},
                        {"ground beef", "500", "grams"},
                        {"rice", "300", "grams"},
                        {"tomato sauce", "400", "millilitres"},
                        {"cheese", "150", "grams"},
                        {"onion", "1", "pieces"}
                }
        );


        // 14. GOOD NEW ORLEANS CREOLE GUMBO

        addRecipeWithIngredients(
                db,
                "Good New Orleans Creole Gumbo",
                "A hearty Creole gumbo served with rice.",
                "Prepare the gumbo base, add the meat and vegetables, simmer until the flavours develop and serve with rice.",
                new String[][]{
                        {"chicken", "500", "grams"},
                        {"sausage", "300", "grams"},
                        {"okra", "300", "grams"},
                        {"onion", "1", "pieces"},
                        {"bell peppers", "1", "pieces"},
                        {"rice", "300", "grams"}
                }
        );


        // 15. SLOPPY JOE BAKED POTATOES

        addRecipeWithIngredients(
                db,
                "Sloppy Joe Baked Potatoes",
                "Baked potatoes topped with a hearty sloppy Joe mixture.",
                "Bake the potatoes, prepare the sloppy Joe filling and serve the filling over the hot potatoes.",
                new String[][]{
                        {"potatoes", "4", "pieces"},
                        {"ground beef", "500", "grams"},
                        {"tomato sauce", "300", "millilitres"},
                        {"onion", "1", "pieces"},
                        {"ketchup", "100", "millilitres"},
                        {"cheese", "150", "grams"}
                }
        );
    }

    // Helper method
    private void addRecipeWithIngredients(
            SQLiteDatabase db,
            String name,
            String description,
            String instructions,
            String[][] ingredients) {

        // Create values for the recipe table
        ContentValues recipeValues = new ContentValues();

        recipeValues.put(RECIPE_NAME, name);
        recipeValues.put(RECIPE_DESCRIPTION, description);
        recipeValues.put(RECIPE_INSTRUCTIONS, instructions);

        // Insert the recipe into the recipes table
        long recipeId = db.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        // Stop if the recipe could not be inserted
        if (recipeId == -1) {
            return;
        }

        // Add each ingredient belonging to this recipe
        for (String[] ingredient : ingredients) {

            String ingredientName = ingredient[0];

            double quantity = Double.parseDouble(
                    ingredient[1]
            );

            String unit = ingredient[2];

            addRecipeIngredient(
                    db,
                    (int) recipeId,
                    ingredientName,
                    quantity,
                    unit
            );
        }
    }


}