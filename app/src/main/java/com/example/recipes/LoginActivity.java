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

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Database database = new Database(getApplicationContext());

        EditText username = findViewById(R.id.username_input);
        EditText password = findViewById(R.id.password_input);
        Button login = findViewById(R.id.login_button);
        Button register = findViewById(R.id.register_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegisterActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(goToRegisterActivity);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = database.authenticateUser(username.getText().toString(), password.getText().toString());
                if (!user.getUsername().isEmpty()) {
                    Intent goToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    goToMainActivity.putExtra("currentUserId", user.getId());
                    goToMainActivity.putExtra("currentUserUsername", user.getUsername());
                    goToMainActivity.putExtra("currentUserPassword", user.getPassword());
                    goToMainActivity.putExtra("currentUserIsAdmin", user.getAdmin());
                    startActivity(goToMainActivity);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.bad_credentials,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}