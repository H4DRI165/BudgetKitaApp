package com.example.budgetkitaapp.forgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.login.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ForgotPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText edtEmail;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Assign variable
        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.forgotLoginEmail);
        btnReset = (Button) findViewById(R.id.resetBtn);

        // Set listener at reset password button
        btnReset.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim(); // Get entered email and remove leading/trailing spaces

            // Validate email format
            if (!isValidEmail(email)) {
                Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final ProgressDialog progressDialog = new ProgressDialog(ForgotPassword.this);
            progressDialog.setMessage("Verifying..");
            progressDialog.show();

            // Check if email exists
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> signInMethods = task.getResult().getSignInMethods();
                            if (signInMethods != null && !signInMethods.isEmpty()) {
                                // Email exists, send password reset email
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(task1 -> {
                                            progressDialog.dismiss();
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Reset password instructions has been sent to your email",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to send reset password email", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                // Start intent to main activity
                                Intent MainActivity = new Intent(ForgotPassword.this, com.example.budgetkitaapp.login.MainActivity.class);
                                startActivity(MainActivity);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Email does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to check email existence", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    });
        });
        //Enable arrow icon at top to go back to previous activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Email validation method
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
        startActivity(new Intent(ForgotPassword.this, MainActivity.class));
    }
}


