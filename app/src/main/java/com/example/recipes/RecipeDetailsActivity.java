package com.example.recipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipes.DAO.Database;
import com.example.recipes.R;
import com.example.recipes.model.Recipe;
import com.example.recipes.model.User;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // Retrieve the recipe data passed from MainActivity
//        Recipe recipe = getIntent().getParcelableExtra("recipe");
        int recipeId = (int) getIntent().getSerializableExtra("recipeId");
        String recipeName = (String) getIntent().getSerializableExtra("recipeName");
        ArrayList<String> recipeIngredients = (ArrayList<String>) getIntent().getSerializableExtra("recipeIngredients");
        ArrayList<String> recipeSteps = (ArrayList<String>) getIntent().getSerializableExtra("recipePreparationSteps");
        Recipe recipe = new Recipe(recipeId, recipeName, recipeIngredients, recipeSteps);

        // Display the recipe details in the layout
        TextView recipeNameTextView = findViewById(R.id.recipe_name);
        recipeNameTextView.setText(recipe.getName());

        // Display the ingredients
        LinearLayout ingredientsLayout = findViewById(R.id.recipe_ingredients_list);
        for (String ingredient : recipe.getIngredients()) {
            TextView ingredientTextView = new TextView(this);
            ingredientTextView.setText(ingredient);
            ingredientsLayout.addView(ingredientTextView);
        }

        // Display the preparation steps
        LinearLayout preparationStepsLayout = findViewById(R.id.recipe_instructions_list);
        for (String step : recipe.getPreparationSteps()) {
            TextView stepTextView = new TextView(this);
            stepTextView.setText(step);
            preparationStepsLayout.addView(stepTextView);
        }

        // Display the button
        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(recipe);
            }
        });
    }

    private void updateRecipeDetailsView(Recipe recipe) {
        // Update the recipe name
        TextView recipeNameTextView = findViewById(R.id.recipe_name);
        recipeNameTextView.setText(recipe.getName());

        // Update the ingredients
        LinearLayout ingredientsLayout = findViewById(R.id.recipe_ingredients_list);
        ingredientsLayout.removeAllViews();
        for (String ingredient : recipe.getIngredients()) {
            TextView ingredientTextView = new TextView(this);
            ingredientTextView.setText(ingredient);
            ingredientsLayout.addView(ingredientTextView);
        }

        // Update the preparation steps
        LinearLayout preparationStepsLayout = findViewById(R.id.recipe_instructions_list);
        preparationStepsLayout.removeAllViews();
        for (String step : recipe.getPreparationSteps()) {
            TextView stepTextView = new TextView(this);
            stepTextView.setText(step);
            preparationStepsLayout.addView(stepTextView);
        }
    }

    private void showUpdateDialog(Recipe recipe) {
        // Inflate the update_recipe_dialog.xml layout
        LayoutInflater inflater = LayoutInflater.from(RecipeDetailsActivity.this);
        View updateRecipeDialogView = inflater.inflate(R.layout.add_recipe_dialog, null);

        int currentRecipeId = recipe.getId();
        String currentRecipeName = recipe.getName();
        ArrayList<String> currentRecipeIngredients = recipe.getIngredients();
        ArrayList<String> currentRecipeSteps = recipe.getPreparationSteps();

        // Updated values
        ArrayList<String> recipeIngredients = new ArrayList<>();
        ArrayList<String> recipeSteps = new ArrayList<>();

        // Create the AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecipeDetailsActivity.this);
        alertDialogBuilder.setView(updateRecipeDialogView);
        final AlertDialog updateRecipeDialog = alertDialogBuilder.create();
        updateRecipeDialog.show();

        // Initialize the views
        final EditText recipeNameEditText = updateRecipeDialog.findViewById(R.id.recipe_name_edit_text);
        final EditText ingredientEditText = updateRecipeDialog.findViewById(R.id.ingredient_edit_text);
        final EditText preparationStepsEditText = updateRecipeDialog.findViewById(R.id.preparation_steps_edit_text);
        LinearLayout ingredientListLayout = updateRecipeDialog.findViewById(R.id.ingredient_linear_layout);
        LinearLayout preparationStepsListLayout = updateRecipeDialog.findViewById(R.id.preparation_steps_linear_layout);

        // pre-populating the dialog with current values from recipe
        recipeNameEditText.setText(currentRecipeName);

        for (String ingredient : currentRecipeIngredients) {
            TextView ingredientTextView = new TextView(RecipeDetailsActivity.this);
            ingredientTextView.setText(ingredient);
            ingredientTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click event for the preparation step TextView
                    // For example, allow editing of the step
                    editItem(ingredientListLayout, (TextView) v);
                }
            });
            ingredientListLayout.addView(ingredientTextView);
        }

        for (String step : currentRecipeSteps) {
            TextView preparationStepTextView = new TextView(RecipeDetailsActivity.this);
            preparationStepTextView.setText(step);
            preparationStepTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click event for the preparation step TextView
                    // For example, allow editing of the step
                    editItem(preparationStepsListLayout, (TextView) v);
                }
            });
            preparationStepsListLayout.addView(preparationStepTextView);
        }

        // Button to add a new ingredient
        Button addIngredientButton = updateRecipeDialog.findViewById(R.id.add_ingredient_button);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = ingredientEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient)) {
                    // Create a TextView for the ingredient
                    TextView ingredientTextView = new TextView(RecipeDetailsActivity.this);
                    ingredientTextView.setText(ingredient);
                    ingredientTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event for the ingredient TextView
                            // For example, allow editing of the ingredient
                            editItem(ingredientListLayout, (TextView) v);
                        }
                    });

                    // Add the ingredient TextView to the layout
                    LinearLayout ingredientListLayout = updateRecipeDialog.findViewById(R.id.ingredient_linear_layout);
                    ingredientListLayout.addView(ingredientTextView);

                    // Clear the ingredient EditText
                    ingredientEditText.setText("");
                }
            }
        });

        // Button to add a new preparation step
        Button addPreparationStepButton = updateRecipeDialog.findViewById(R.id.add_preparation_step_button);
        addPreparationStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String preparationStep = preparationStepsEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(preparationStep)) {

                    // Create a TextView for the preparation step
                    TextView preparationStepTextView = new TextView(RecipeDetailsActivity.this);
                    preparationStepTextView.setText(preparationStep);
                    preparationStepTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle the click event for the preparation step TextView
                            // For example, allow editing of the step
                            editItem(preparationStepsListLayout, (TextView) v);
                        }
                    });


                    // Add the preparation step TextView to the layout
                    LinearLayout preparationStepsListLayout = updateRecipeDialog.findViewById(R.id.preparation_steps_linear_layout);
                    preparationStepsListLayout.addView(preparationStepTextView);

                    // Clear the preparation step EditText
                    preparationStepsEditText.setText("");
                }
            }
        });

        // Button to save the updated recipe
        Button saveButton = updateRecipeDialog.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeName = recipeNameEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(recipeName)) {
                    // Update the recipe name
                    recipe.setName(recipeName);

                    // update ingredients list
                    ArrayList<String> updatedIngredientList = new ArrayList<>();
                    LinearLayout updatedIngredientListLayout = updateRecipeDialog.findViewById(R.id.ingredient_linear_layout);
                    for (int i = 0; i < updatedIngredientListLayout.getChildCount(); i++) {
                        View childView = updatedIngredientListLayout.getChildAt(i);
                        if (childView instanceof TextView) {
                            TextView textView = (TextView) childView;
                            String ingredient = ((TextView) childView).getText().toString();
                            updatedIngredientList.add(ingredient);
                        }
                    }
                    recipe.setIngredients(updatedIngredientList);

                    // update preparation steps list
                    ArrayList<String> updatedStepsList = new ArrayList<>();
                    LinearLayout updatedPreparationStepsLayout = updateRecipeDialog.findViewById(R.id.preparation_steps_linear_layout);
                    for (int i = 0; i < updatedPreparationStepsLayout.getChildCount(); i++) {
                        View childView = updatedPreparationStepsLayout.getChildAt(i);
                        if (childView instanceof TextView) {
                            TextView textView = (TextView) childView;
                            String step = ((TextView) childView).getText().toString();
                            updatedStepsList.add(step);
                        }
                    }
                    recipe.setPreparationSteps(updatedStepsList);


                    // Update the recipe details view
                    updateRecipeDetailsView(recipe);

                    // Update the database
                    User currentUser = new User();
                    currentUser.setId((Integer) getIntent().getSerializableExtra("currentUserId"));
                    currentUser.setUsername((String) getIntent().getSerializableExtra("currentUserUsername"));
                    currentUser.setPassword((String) getIntent().getSerializableExtra("currentUserPassword"));
                    currentUser.setAdmin((Integer) getIntent().getSerializableExtra("currentUserIsAdmin"));

                    Database database = new Database(getApplicationContext());
                    database.updateRecipe(currentUser, recipe);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedRecipeId", recipe.getId());
                    resultIntent.putExtra("updatedRecipeName", recipe.getName());
                    resultIntent.putExtra("updatedRecipeIngredients", recipe.getIngredients());
                    resultIntent.putExtra("updatedRecipeSteps", recipe.getPreparationSteps());
                    setResult(RESULT_OK, resultIntent);
                    finish();

                    // Dismiss the dialog
                    updateRecipeDialog.dismiss();

                    Toast.makeText(RecipeDetailsActivity.this, "Recipe updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecipeDetailsActivity.this, "Please enter a recipe name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void editItem(final LinearLayout listLayout, final TextView itemTextView) {
        AlertDialog.Builder editItemDialogBuilder = new AlertDialog.Builder(RecipeDetailsActivity.this);
        editItemDialogBuilder.setTitle("Edit Item");
        final EditText editItemEditText = new EditText(RecipeDetailsActivity.this);
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