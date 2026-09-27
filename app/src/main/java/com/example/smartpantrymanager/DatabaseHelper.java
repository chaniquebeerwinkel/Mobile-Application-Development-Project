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


    // =========================================================
    // RECIPE METHODS
    // =========================================================

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

    }
}