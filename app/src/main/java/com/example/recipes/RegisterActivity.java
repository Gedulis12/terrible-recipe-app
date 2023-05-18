package com.example.recipes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipes.DAO.Database;
import com.example.recipes.model.User;
import com.example.recipes.util.Validation;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText username = findViewById(R.id.register_username_input);
        EditText password = findViewById(R.id.register_password_input);
        Button register = findViewById(R.id.register_button);

        Database database = new Database(getApplicationContext());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username.setError(null);
                password.setError(null);

                if(!Validation.isUsernameValid(username.getText().toString())) {
                    username.setError(getResources().getString(R.string.bad_username_input));
                } else if (!Validation.isPasswordValid(password.getText().toString())) {
                    password.setError(getResources().getString(R.string.bad_password_input));
                } else {
                    String registerStatus = database.createUser(new User(username.getText().toString(), password.getText().toString()));
                    Intent goToLoginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(goToLoginActivity);
                    Toast.makeText(getApplicationContext(),
                            registerStatus,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}