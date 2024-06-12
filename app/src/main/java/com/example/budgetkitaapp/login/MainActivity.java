package com.example.budgetkitaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetkitaapp.HomeActivity;
import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.forgotPassword.ForgotPassword;
import com.example.budgetkitaapp.register.Register;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, passReset;
    private TextInputEditText editTextEmail, editTextPassword;
    private Button signIn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();  //HIDE TOOLBAR

        // Assign variables and set click listeners
        register = findViewById(R.id.titleSignUpLink); // INITIALIZE REGISTER LINK IN LOGIN PAGE
        register.setOnClickListener(this);

        signIn = findViewById(R.id.loginBtn);
        signIn.setOnClickListener(this);

        editTextEmail = findViewById(R.id.loginEmail);
        editTextPassword = findViewById(R.id.loginPassword);

        progressBar = findViewById(R.id.progressBarRegister);

        // Check if current user exists in Firebase
        mAuth = FirebaseAuth.getInstance();

        // Make Forgot password text clickable and link it to next Forgot Password Activity
        passReset = findViewById(R.id.forgetPasswordLink);
        passReset.setOnClickListener(view -> {
            // go to ForgotPassword activity using intent
            Intent reset = new Intent(MainActivity.this, ForgotPassword.class);
            startActivity(reset);
        });
    }

    @Override
    public void onClick(View view) {
        // switch to differentiate if user clicks sign in or sign up
        switch (view.getId()) {
            case R.id.titleSignUpLink:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.loginBtn:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        // get the user input for email and password
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check the fields to make sure they have been filled
        // if empty, show error and highlight the empty field
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6 || !isValidPassword(password)) {
            editTextPassword.setError("Min password length should be 6 characters with a combination of uppercase, alphanumeric, and special characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //  Function to close the keyboard
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

        // Authenticate the user using the email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // redirect to home page
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                // if user does not exist in Firebase
                Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check the password strength
    // Must satisfy the requirement which is must have alphanumeric, uppercase and special char
    public static boolean isValidPassword(final String editTextPassword) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(editTextPassword);

        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

