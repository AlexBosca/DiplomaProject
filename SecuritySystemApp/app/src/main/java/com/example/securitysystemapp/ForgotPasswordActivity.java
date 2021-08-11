package com.example.securitysystemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.securitysystemapp.Patterns.EMAIL;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailET;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailET = findViewById(R.id.emailET);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void searchAccount(View view) {
        if(emailValidation()) {
            String email = emailET.getText().toString();
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ForgotPasswordActivity.this, "Reset Link Was Sent To Your Email", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ForgotPasswordActivity.this, "Error On Sending Reset Link" + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private boolean emailValidation() {
        if(emailET.getText().toString().isEmpty() || !emailET.getText().toString().matches(EMAIL)) {
            emailET.setError("Enter a valid email address");
            return false;
        }

        return true;
    }
}