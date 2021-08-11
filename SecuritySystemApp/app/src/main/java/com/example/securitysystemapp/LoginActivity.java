package com.example.securitysystemapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.securitysystemapp.Patterns.EMAIL;


public class LoginActivity extends AppCompatActivity {
    private EditText emailET, passwordET;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void logIn(View view) {
        if(emailValidation() && passwordValidation()) {
            firebaseAuth.signInWithEmailAndPassword(emailET.getText().toString().trim(), passwordET.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(user.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "You successfully logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "You haven't verify your email", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "You cannot log in", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean emailValidation() {
        if(emailET.getText().toString().isEmpty() || !emailET.getText().toString().matches(EMAIL)) {
            emailET.setError("Enter a valid email address");
            return false;
        }

        return true;
    }

    private boolean passwordValidation() {
        if(passwordET.getText().toString().isEmpty() || passwordET.length() < 8) {
            passwordET.setError("Enter a valid password");
            return false;
        }

        return true;
    }
}