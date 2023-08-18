package com.example.budgetkitaapp.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.User;
import com.example.budgetkitaapp.login.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private TextView welcomeRegister, registerUser;
    private TextInputEditText editTextUserName, editTextPassword, editTextEmail;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_register);

        //HIDE TOOLBAR
        //getSupportActionBar().hide();


        //Assign variable and set listener
        welcomeRegister = (TextView) findViewById(R.id.wkmTitleLinkRegister);
        welcomeRegister.setOnClickListener(this);

        //Assign variable and set listener
        registerUser = (Button) findViewById(R.id.registerBtn);
        registerUser.setOnClickListener(this);

        //Assign variable
        editTextUserName = (TextInputEditText) findViewById(R.id.registerUsername);
        editTextPassword = (TextInputEditText) findViewById(R.id.registerPassword);
        editTextEmail = (TextInputEditText) findViewById(R.id.registerEmail);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);

        //enable arrow icon to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wkmTitleLinkRegister:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerBtn:
                registerUser();
                break;
        }
    }


    //To register user account
    private void registerUser() {
        // Get the input data from the EditText
        String username = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        // Input validation
        if (!validateUserName(username)) {
            return;
        }

        if (!validatePassword(password)) {
            return;
        }

        if (!validateEmail(email)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create a new user
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(username, email);

                    String userId = task.getResult().getUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Accounts").child(userId);

                    // Set UserDetail data
                    databaseReference.child("UserDetail").child("email").setValue(email);
                    databaseReference.child("UserDetail").child("username").setValue(username);

                    // Set Expenses and Income as empty nodes
                    databaseReference.child("Expenses").setValue(null);
                    databaseReference.child("Income").setValue(null);

                    Toast.makeText(Register.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();

                    //progressBar.setVisibility(View.GONE);
                    // Reset progress bar
                    //progressBar.setProgress(0);

                    // Redirect to login layout
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    // Check if the error is due to email already existing
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Register.this, "Email already exists. Please choose a different email.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(Register.this, "Failed to register! Please try again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // Check if the full name is empty
    private boolean validateUserName(String username) {
        if (username.isEmpty()) {
            editTextUserName.setError("Full name is required!");
            editTextUserName.requestFocus();
            return false;
        }
        return true;
    }

    // Check if the password meets the requirements
    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6 || !password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$")) {
            editTextPassword.setError("Password must be at least 6 characters with a combination of uppercase, alphanumeric, and special characters.");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    // Check if the email is empty and valid
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email address.");
            editTextEmail.requestFocus();
            return false;
        }

        return true;
    }

    //Display the arrow on top to go back to previous activity
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu (Menu menu){
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Register.this, MainActivity.class));
    }

}


