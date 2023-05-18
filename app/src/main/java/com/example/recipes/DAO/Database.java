package com.example.recipes.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.recipes.model.Recipe;
import com.example.recipes.model.User;
import com.example.recipes.util.Password;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db";

    private static final String USERS_TABLE_NAME = "users";
    private static final String KEY_ID = "id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String IS_ADMIN = "is_admin";

    private static final String RECIPE_TABLE_NAME = "recipes";
    private static final String RECIPE_NAME = "recipe_name";

    private static final String USERS_RECIPES_TABLE_NAME = "users_recipes";
    private static final String USER_ID = "user_id";
    private static final String RECIPE_ID = "recipe_id";

    private static final String INGREDIENTS_TABLE_NAME = "ingredients";
    private static final String INGREDIENT_NAME = "name";
    private static final String INGREDIENT_RECIPE_ID = "recipe_id";

    private static final String PREPARATION_TABLE_NAME = "preparation_steps";
    private static final String PREPARATION_STEP = "step";
    private static final String PREPARATION_RECIPE_ID = "recipe_id";


    private static final String CREATE_RECIPE_TABLE = "CREATE TABLE IF NOT EXISTS " + RECIPE_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + RECIPE_NAME + " TEXT"
            + ")";

    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + USERNAME + " TEXT, "
            + PASSWORD + " TEXT, "
            + IS_ADMIN + " INT"
            + ")";

    private static final String CREATE_USERS_RECIPES_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_RECIPES_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + USER_ID + " INTEGER, "
            + RECIPE_ID + " INTEGER, "
            + "FOREIGN KEY (" + USER_ID + ") REFERENCES " + USERS_TABLE_NAME + " (id), "
            + "FOREIGN KEY (" + RECIPE_ID + ") REFERENCES " + RECIPE_TABLE_NAME + " (id)" + ")";

    private static final String CREATE_INGREDIENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + INGREDIENTS_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + INGREDIENT_NAME + " TEXT, "
            + INGREDIENT_RECIPE_ID + " INTEGER, "
            + "FOREIGN KEY (" + INGREDIENT_RECIPE_ID + ") REFERENCES " + RECIPE_TABLE_NAME + " (id)" + " )";

    private static final String CREATE_PREPARATION_TABLE = "CREATE TABLE IF NOT EXISTS " + PREPARATION_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + PREPARATION_STEP + " TEXT, "
            + PREPARATION_RECIPE_ID + " INTEGER, "
            + "FOREIGN KEY (" + PREPARATION_RECIPE_ID + ") REFERENCES " + RECIPE_TABLE_NAME + " (id)" + " )";

    private static final String CREATE_ADMIN_USER = "INSERT INTO users ("
            + USERNAME + ", " + PASSWORD + ", " + IS_ADMIN + ") "
            + "VALUES" + "(\'adminacc\', \'$2a$12$GXk3GhZBFzQIF/cIcUPCLOphpXHmUT1U/JO4YSdMq3TSjbCYYyVwS\', 1)"; // password admin123

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_USERS_RECIPES_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_PREPARATION_TABLE);
        db.execSQL(CREATE_ADMIN_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        onCreate(db);
    }

    public String createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (checkIfUserExists(user.getUsername())) {
            return "this username already exists";
        } else {
            values.put(USERNAME, user.getUsername());
            String pass = Password.hashPassword(user.getPassword());
            values.put(PASSWORD, pass);
            values.put(IS_ADMIN, user.getAdmin());
            db.insert(USERS_TABLE_NAME, null, values);
            db.close();
            return "user created successfully";
        }
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(USERS_TABLE_NAME, new String[]{KEY_ID,
                        USERNAME, PASSWORD, IS_ADMIN}, USERNAME + " =?",
                new String[]{String.valueOf(username)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            int id = Integer.parseInt(cursor.getString(0));
            String userUsername = cursor.getString(1);
            String userPassword = cursor.getString(2);
            int userIsAdmin = Integer.parseInt(cursor.getString(3));
            cursor.close();
            db.close();

            User user = new User(id, userUsername, userPassword, userIsAdmin);
            return user;
        }
        return new User();
    }

    public boolean checkIfUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT username FROM " + USERS_TABLE_NAME + " WHERE username LIKE " + "\'" + username + "\'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public User authenticateUser(String username, String password) {
        User user = getUserByUsername(username);
        if (!checkIfUserExists(username)) {
            return new User("", "");
        } else {
            boolean authentication = Password.checkPassword(password, user.getPassword());
            if (authentication) {
                return user;
            } else {
                return new User("", "");
            }
        }
    }

    public void createRecipe(User user, Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> ingredients = recipe.getIngredients();
        ArrayList<String> preparationSteps = recipe.getPreparationSteps();

        ContentValues valuesRecipe = new ContentValues();
        valuesRecipe.put(RECIPE_NAME, recipe.getName());
        long recipeId = db.insert(RECIPE_TABLE_NAME, null, valuesRecipe);

        ContentValues valuesUsersRecipes = new ContentValues();
        valuesUsersRecipes.put(USER_ID, user.getId());
        valuesUsersRecipes.put(RECIPE_ID, recipeId);
        db.insert(USERS_RECIPES_TABLE_NAME, null, valuesUsersRecipes);


        for (String ingredient : ingredients) {
            ContentValues valuesIngredient = new ContentValues();
            valuesIngredient.put(INGREDIENT_NAME, ingredient);
            valuesIngredient.put(INGREDIENT_RECIPE_ID, recipeId);
            db.insert(INGREDIENTS_TABLE_NAME, null, valuesIngredient);
        }

        for (String step : preparationSteps) {
            ContentValues valuesPreparation = new ContentValues();
            valuesPreparation.put(PREPARATION_STEP, step);
            valuesPreparation.put(PREPARATION_RECIPE_ID, recipeId);
            db.insert(PREPARATION_TABLE_NAME, null, valuesPreparation);
        }
    }

    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipeList = new ArrayList<>();

        String selectRecipeQuery = "SELECT * FROM " + RECIPE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectRecipeQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ArrayList<String> ingredients = new ArrayList<>();
                ArrayList<String> recipeSteps = new ArrayList<>();

                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(0));
                recipe.setName(cursor.getString(1));

                String selectIngredientsQuery = "SELECT * FROM " + INGREDIENTS_TABLE_NAME + " WHERE " + INGREDIENT_RECIPE_ID + " = " + recipe.getId();
                Cursor cursorIngredients = db.rawQuery(selectIngredientsQuery, null);
                if (cursorIngredients.moveToFirst()) {
                    do {
                        ingredients.add(cursorIngredients.getString(1));
                    } while (cursorIngredients.moveToNext());
                }
                recipe.setIngredients(ingredients);

                String selectStepsQuery = "SELECT * FROM " + PREPARATION_TABLE_NAME + " WHERE " + PREPARATION_RECIPE_ID + " = " + recipe.getId();
                Cursor cursorSteps = db.rawQuery(selectStepsQuery, null);
                if (cursorSteps.moveToFirst()) {
                    do {
                        recipeSteps.add(cursorSteps.getString(1));
                    } while (cursorSteps.moveToNext());
                }
                recipe.setPreparationSteps(recipeSteps);

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }
        return recipeList;
    }

    public ArrayList<Recipe> getAllRecipesForCurrentUser(User user) {
        ArrayList<Recipe> recipeList = new ArrayList<>();

        String selectRecipeQuery = "SELECT " + RECIPE_TABLE_NAME + "." + KEY_ID + ", " + RECIPE_TABLE_NAME + "." + RECIPE_NAME
                + " FROM " + RECIPE_TABLE_NAME
                + " INNER JOIN " + USERS_RECIPES_TABLE_NAME
                + " ON " + USERS_RECIPES_TABLE_NAME + "." + RECIPE_ID + " = " + RECIPE_TABLE_NAME + "." + KEY_ID
                + " WHERE " + USERS_RECIPES_TABLE_NAME + "." + USER_ID + " = " + user.getId();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectRecipeQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ArrayList<String> ingredients = new ArrayList<>();
                ArrayList<String> recipeSteps = new ArrayList<>();

                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(0));
                recipe.setName(cursor.getString(1));

                String selectIngredientsQuery = "SELECT * FROM " + INGREDIENTS_TABLE_NAME + " WHERE " + INGREDIENT_RECIPE_ID + " = " + recipe.getId();
                Cursor cursorIngredients = db.rawQuery(selectIngredientsQuery, null);
                if (cursorIngredients.moveToFirst()) {
                    do {
                        ingredients.add(cursorIngredients.getString(1));
                    } while (cursorIngredients.moveToNext());
                }
                recipe.setIngredients(ingredients);

                String selectStepsQuery = "SELECT * FROM " + PREPARATION_TABLE_NAME + " WHERE " + PREPARATION_RECIPE_ID + " = " + recipe.getId();
                Cursor cursorSteps = db.rawQuery(selectStepsQuery, null);
                if (cursorSteps.moveToFirst()) {
                    do {
                        recipeSteps.add(cursorSteps.getString(1));
                    } while (cursorSteps.moveToNext());
                }
                recipe.setPreparationSteps(recipeSteps);

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }
        return recipeList;
    }

    public void updateRecipe(User user, Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Retrieve the ID of the recipe
        int recipeId = recipe.getId();

        // Update the recipe in the recipes table
        ContentValues valuesRecipe = new ContentValues();
        valuesRecipe.put(RECIPE_NAME, recipe.getName());
        String whereClause = KEY_ID + " = ?";
        String[] whereArgs = {String.valueOf(recipeId)};
        db.update(RECIPE_TABLE_NAME, valuesRecipe, whereClause, whereArgs);

        // Clear existing ingredients and preparation steps for the recipe
        String deleteIngredientsQuery = "DELETE FROM " + INGREDIENTS_TABLE_NAME + " WHERE " + INGREDIENT_RECIPE_ID + " = " + recipeId;
        String deletePreparationStepsQuery = "DELETE FROM " + PREPARATION_TABLE_NAME + " WHERE " + PREPARATION_RECIPE_ID + " = " + recipeId;
        db.execSQL(deleteIngredientsQuery);
        db.execSQL(deletePreparationStepsQuery);

        // Insert the updated ingredients
        ArrayList<String> ingredients = recipe.getIngredients();
        for (String ingredient : ingredients) {
            ContentValues valuesIngredient = new ContentValues();
            valuesIngredient.put(INGREDIENT_NAME, ingredient);
            valuesIngredient.put(INGREDIENT_RECIPE_ID, recipeId);
            db.insert(INGREDIENTS_TABLE_NAME, null, valuesIngredient);
        }

        // Insert the updated preparation steps
        ArrayList<String> preparationSteps = recipe.getPreparationSteps();
        for (String step : preparationSteps) {
            ContentValues valuesPreparation = new ContentValues();
            valuesPreparation.put(PREPARATION_STEP, step);
            valuesPreparation.put(PREPARATION_RECIPE_ID, recipeId);
            db.insert(PREPARATION_TABLE_NAME, null, valuesPreparation);
        }

        db.close();
    }

    public void deleteRecipe(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the recipe from the recipes table
        String whereClause = KEY_ID + " = ?";
        String[] whereArgs = {String.valueOf(recipeId)};
        db.delete(RECIPE_TABLE_NAME, whereClause, whereArgs);

        // Delete the associated ingredients from the ingredients table
        String deleteIngredientsQuery = "DELETE FROM " + INGREDIENTS_TABLE_NAME + " WHERE " + INGREDIENT_RECIPE_ID + " = " + recipeId;
        db.execSQL(deleteIngredientsQuery);

        // Delete the associated preparation steps from the preparation_steps table
        String deletePreparationStepsQuery = "DELETE FROM " + PREPARATION_TABLE_NAME + " WHERE " + PREPARATION_RECIPE_ID + " = " + recipeId;
        db.execSQL(deletePreparationStepsQuery);

        db.close();
    }

}