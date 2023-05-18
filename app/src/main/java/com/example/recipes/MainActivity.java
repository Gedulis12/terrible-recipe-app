package com.example.recipes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipes.DAO.Database;
import com.example.recipes.adapter.RecipeAdapter;
import com.example.recipes.model.Recipe;
import com.example.recipes.model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Recipe> recipeList;
    private Database database;
    private Button add;
    private Button delete;
    private PopupWindow popupWindow;
    private ListView listView;
    private RecipeAdapter recipeAdapter;
    private User currentUser = new User();
    private static final int REQUEST_CODE_EDIT_RECIPE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser.setId((Integer) getIntent().getSerializableExtra("currentUserId"));
        currentUser.setUsername((String) getIntent().getSerializableExtra("currentUserUsername"));
        currentUser.setPassword((String) getIntent().getSerializableExtra("currentUserPassword"));
        currentUser.setAdmin((Integer) getIntent().getSerializableExtra("currentUserIsAdmin"));
        database = new Database(getApplicationContext());
        listView = findViewById(R.id.recipe_list);
        add = findViewById(R.id.add_new_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPopup();
            }
        });

        if (currentUser.getAdmin() == 1) {
            recipeList = (ArrayList<Recipe>) database.getAllRecipes();
        } else {
            recipeList = (ArrayList<Recipe>) database.getAllRecipesForCurrentUser(currentUser);
        }

        recipeAdapter = new RecipeAdapter(MainActivity.this, recipeList, database, currentUser);
        listView.setAdapter(recipeAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_RECIPE && resultCode == RESULT_OK) {
            int updatedRecipeId = data.getIntExtra("updatedRecipeId", -1);
            String updatedRecipeName = data.getStringExtra("updatedRecipeName");
            ArrayList<String> updatedRecipeIngredients = data.getStringArrayListExtra("updatedRecipeIngredients");
            ArrayList<String> updatedRecipeSteps = data.getStringArrayListExtra("updatedRecipeSteps");

            if (updatedRecipeId != -1) {
                // Find the index of the recipe in recipeList
                int index = -1;
                for (int i = 0; i < recipeList.size(); i++) {
                    Recipe recipe = recipeList.get(i);
                    if (recipe.getId() == updatedRecipeId) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    // Update the recipe in the list
                    Recipe updatedRecipe = recipeList.get(index);
                    updatedRecipe.setName(updatedRecipeName);
                    updatedRecipe.setIngredients(updatedRecipeIngredients);
                    updatedRecipe.setPreparationSteps(updatedRecipeSteps);
                    recipeAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void addPopup() {
        // Inflate the add_recipe_dialog.xml layout
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View addRecipeDialogView = inflater.inflate(R.layout.add_recipe_dialog, null);

        // Create the AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(addRecipeDialogView);
        final AlertDialog addRecipeDialog = alertDialogBuilder.create();
        addRecipeDialog.show();

        // Initialize the views
        final EditText recipeNameEditText = addRecipeDialog.findViewById(R.id.recipe_name_edit_text);
        final EditText ingredientEditText = addRecipeDialog.findViewById(R.id.ingredient_edit_text);
        final EditText preparationStepsEditText = addRecipeDialog.findViewById(R.id.preparation_steps_edit_text);
        final LinearLayout ingredientLinearLayout = addRecipeDialog.findViewById(R.id.ingredient_linear_layout);
        final LinearLayout preparationStepsLinearLayout = addRecipeDialog.findViewById(R.id.preparation_steps_linear_layout);

        // Button to add a new ingredient
        Button addIngredientButton = addRecipeDialog.findViewById(R.id.add_ingredient_button);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = ingredientEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient)) {
                    // Create a TextView for the ingredient
                    TextView ingredientTextView = new TextView(MainActivity.this);
                    ingredientTextView.setText(ingredient);
                    ingredientTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event for the ingredient TextView
                            // For example, allow editing of the ingredient
                            editItem(ingredientLinearLayout, (TextView) v);
                        }
                    });

                    // Add the ingredient TextView to the layout
                    ingredientLinearLayout.addView(ingredientTextView);

                    // Clear the ingredient EditText
                    ingredientEditText.setText("");
                }
            }
        });

        // Button to add a new preparation step
        Button addPreparationStepButton = addRecipeDialog.findViewById(R.id.add_preparation_step_button);
        addPreparationStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String preparationStep = preparationStepsEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(preparationStep)) {
                    // Create a TextView for the preparation step
                    TextView preparationStepTextView = new TextView(MainActivity.this);
                    preparationStepTextView.setText(preparationStep);
                    preparationStepTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event for the preparation step TextView
                            // For example, allow editing of the step
                            editItem(preparationStepsLinearLayout, (TextView) v);
                        }
                    });

                    // Add the preparation step TextView to the layout
                    preparationStepsLinearLayout.addView(preparationStepTextView);

                    // Clear the preparation step EditText
                    preparationStepsEditText.setText("");
                }
            }
        });

        // Button to save the recipe
        Button saveRecipeButton = addRecipeDialog.findViewById(R.id.button_save);
        saveRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeName = recipeNameEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(recipeName)) {
                    // Create a new Recipe object and add it to the recipeList
                    Recipe newRecipe = new Recipe();
                    newRecipe.setName(recipeName);

                    // Retrieve the added ingredients
                    ArrayList<String> ingredients = new ArrayList<>();
                    for (int i = 0; i < ingredientLinearLayout.getChildCount(); i++) {
                        TextView ingredientTextView = (TextView) ingredientLinearLayout.getChildAt(i);
                        String ingredient = ingredientTextView.getText().toString();
                        ingredients.add(ingredient);
                    }
                    newRecipe.setIngredients(ingredients);

                    // Retrieve the added preparation steps
                    ArrayList<String> preparationSteps = new ArrayList<>();
                    for (int i = 0; i < preparationStepsLinearLayout.getChildCount(); i++) {
                        TextView preparationStepTextView = (TextView) preparationStepsLinearLayout.getChildAt(i);
                        String preparationStep = preparationStepTextView.getText().toString();
                        preparationSteps.add(preparationStep);
                    }
                    newRecipe.setPreparationSteps(preparationSteps);

                    // Add the new recipe to the recipeList
                    recipeList.add(newRecipe);
                    database.createRecipe(currentUser, newRecipe);

                    // Update the RecyclerView
                    recipeAdapter.notifyDataSetChanged();

                    // Dismiss the dialog
                    addRecipeDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a recipe name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to handle editing and removing of an item in the list
    private void editItem(final LinearLayout listLayout, final TextView itemTextView) {
        AlertDialog.Builder editItemDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        editItemDialogBuilder.setTitle("Edit Item");
        final EditText editItemEditText = new EditText(MainActivity.this);
        editItemEditText.setText(itemTextView.getText().toString());
        editItemDialogBuilder.setView(editItemEditText);
        editItemDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editedItem = editItemEditText.getText().toString().trim();
                itemTextView.setText(editedItem);
            }
        });
        editItemDialogBuilder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listLayout.removeView(itemTextView);
            }
        });
        editItemDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog editItemDialog = editItemDialogBuilder.create();
        editItemDialog.show();
    }


}