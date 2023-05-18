package com.example.recipes.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.recipes.DAO.Database;
import com.example.recipes.R;
import com.example.recipes.RecipeDetailsActivity;
import com.example.recipes.model.Recipe;
import com.example.recipes.model.User;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {

    private Activity context;
    private ArrayList<Recipe> recipeList;
    private Database database;
    private User currentUser;
    private static final int REQUEST_CODE_EDIT_RECIPE = 10;

    public RecipeAdapter(Activity context, ArrayList<Recipe> recipeList, Database database, User currentUser) {
        this.currentUser = currentUser;
        this.context = context;
        this.recipeList = recipeList;
        this.database = database;
    }

    public static class ViewHolder {
        TextView textViewRecipeName;
        TextView textViewRecipeIngredientName;
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return recipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
       // LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        Button delete;

        if (convertView == null) {
            viewHolder = new ViewHolder();
         //   row = inflater.inflate(R.layout.row_item, null, true);
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.textViewRecipeName = row.findViewById(R.id.recipe_name);
            viewHolder.textViewRecipeIngredientName = row.findViewById(R.id.ingredient_name);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.textViewRecipeName.setText(recipeList.get(position).getName());
        final Recipe recipe = recipeList.get(position);

        viewHolder.textViewRecipeName.setText(recipe.getName());

        StringBuilder ingredientsBuilder = new StringBuilder();
        ArrayList<String> ingredients = recipe.getIngredients();
        for (String ingredient : ingredients) {
           ingredientsBuilder.append("• ").append(ingredient).append("\n");
        }
        viewHolder.textViewRecipeIngredientName.setText(ingredientsBuilder.toString());

        delete = row.findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the recipe ID associated with the clicked button
                int recipeId = recipe.getId();

                // Delete the recipe from the database
                database.deleteRecipe(recipeId);

                // Remove the deleted recipe from the list
                recipeList.remove(position);

                // Notify the adapter about the change
                notifyDataSetChanged();
            }
        });


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the RecipeDetailsActivity and pass the recipe data
                Intent intent = new Intent(context, RecipeDetailsActivity.class);


                intent.putExtra("recipeId", recipe.getId());
                intent.putExtra("recipeName", recipe.getName());
                intent.putExtra("recipePreparationSteps", recipe.getPreparationSteps());
                intent.putExtra("recipeIngredients", recipe.getIngredients());

                intent.putExtra("currentUserId", currentUser.getId());
                intent.putExtra("currentUserUsername", currentUser.getUsername());
                intent.putExtra("currentUserPassword", currentUser.getPassword());
                intent.putExtra("currentUserIsAdmin", currentUser.getAdmin());
                context.startActivityForResult(intent, REQUEST_CODE_EDIT_RECIPE);
            }
        });
//        final int positionPopup = position;
        return row;
    }
}
