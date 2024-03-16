package com.fit2081.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    String usernamePreference, passwordPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.editTextUsernameLogin);
        etPassword = findViewById(R.id.editTextPasswordLogin);

        restorePreferences();
    }

    private void restorePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(KeyStore.FILE_NAME, MODE_PRIVATE);
        usernamePreference = sharedPreferences.getString(KeyStore.KEY_USERNAME, null);
        passwordPreference = sharedPreferences.getString(KeyStore.KEY_PASSWORD, null);

        if (usernamePreference != null) {
            etUsername.setText(usernamePreference);
        }
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.equals(usernamePreference) && password.equals(passwordPreference)) {
            // User is logged in, redirect to Main activity
            redirectToMainActivity();
        } else {
            Toast.makeText(this, "Authentication failure: Username or Password is incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRedirectRegisterButtonClick(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}