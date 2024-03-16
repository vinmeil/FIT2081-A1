package com.fit2081.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.editTextUsernameRegister);
        etPassword = findViewById(R.id.editTextPasswordRegister);
        etConfirmPassword = findViewById(R.id.editTextConfirmRegister);
    }

    private void saveDataToSharedPreferences(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(KeyStore.FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KeyStore.KEY_USERNAME, username);
        editor.putString(KeyStore.KEY_PASSWORD, password);
        editor.apply();
    }

    public void onRegisterButtonClick(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String toastMessage = null;

        if (password.equals(confirmPassword)) {
            // Save the username and password to SharedPreferences
            saveDataToSharedPreferences(username, password);
            toastMessage = String.format("User %s has been registered successfully!", username);

            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
            redirectToLoginActivity();
        } else {
            toastMessage = "Passwords do not match!";
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void onRedirectLoginButtonClick(View view) {
        redirectToLoginActivity();
    }

    private void redirectToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}