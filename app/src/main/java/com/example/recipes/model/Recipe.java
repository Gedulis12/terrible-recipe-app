package com.example.recipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe implements Parcelable {

    private int id;
    private String name;
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> preparationSteps = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String name, ArrayList<String> ingredients, ArrayList<String> preparationSteps) {
        this.name = name;
        this.ingredients = ingredients;
        this.preparationSteps = preparationSteps;
    }

    public Recipe(int id, String name, ArrayList<String> ingredients, ArrayList<String> preparationSteps) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.preparationSteps = preparationSteps;
    }

    protected Recipe(Parcel in) {
        name = in.readString();
        ingredients = in.createStringArrayList();
        preparationSteps = in.createStringArrayList();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getPreparationSteps() {
        return preparationSteps;
    }

    public void setPreparationSteps(ArrayList<String> preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(ingredients);
        dest.writeStringList(preparationSteps);
    }
}