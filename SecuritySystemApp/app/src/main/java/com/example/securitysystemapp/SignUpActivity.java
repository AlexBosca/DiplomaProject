package com.example.securitysystemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.securitysystemapp.Patterns.*;

public class SignUpActivity extends AppCompatActivity {
    private EditText firstNameET, lastNameET, emailET, passwordET, phoneET;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        phoneET = findViewById(R.id.phoneET);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if(firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void alreadySignedIn(View view) {
        finish();
    }

    public void signIn(View view) {
        if(firstNameValidation() && lastNameValidation() && emailValidation() && phonedValidation() && passwordValidation()) {
            String firstName = firstNameET.getText().toString().trim();
            String lastName = lastNameET.getText().toString().trim();
            String email = emailET.getText().toString().trim();
            String phone = phoneET.getText().toString().trim();

            firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString().trim(), passwordET.getText().toString().trim()).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    firebaseUser.sendEmailVerification()
                            .addOnSuccessListener(aVoid -> Toast.makeText(SignUpActivity.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error Sending The Verification Email", Toast.LENGTH_SHORT).show());

                    String userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", firstName);
                    user.put("lastName", lastName);
                    user.put("email", email);
                    user.put("phone", phone);

                    documentReference.set(user)
                            .addOnSuccessListener(aVoid -> {
                                appendUserID(userID);
                                Log.d("TAG", "user profile is created for " + userID);
                            })
                            .addOnFailureListener(e -> Log.d("TAG", "creating user profile just failed: " + e.toString()));

                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "You cannot sign in", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void appendUserID(String userID) {
        databaseReference.child("userIDs").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                long numberOfUsers = task.getResult().getChildrenCount();

                databaseReference.child("userIDs").child("user" + (numberOfUsers + 1)).setValue(userID);
                createUserPIN(userID);
            }
        });
    }

    private void createUserPIN(String userID) {
        // create date of signing up
        databaseReference.child("users").child(userID).child("date").setValue(getTodayDate());


        //create date of last update of PIN with default value
        databaseReference.child("users").child(userID).child("pin").child("date").setValue("default");

        //create the PIN with default value
        databaseReference.child("users").child(userID).child("pin").child("value").setValue("default");
    }

    private String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private boolean firstNameValidation() {
        if(firstNameET.getText().toString().isEmpty() || !firstNameET.getText().toString().matches(NAME)) {
            firstNameET.setError("Enter your first name");
            return false;
        }

        return true;
    }

    private boolean lastNameValidation() {
        if(lastNameET.getText().toString().isEmpty() || !lastNameET.getText().toString().matches(NAME)) {
            lastNameET.setError("Enter your last name");
            return false;
        }

        return true;
    }

    private boolean emailValidation() {
        if(emailET.getText().toString().isEmpty() || !emailET.getText().toString().matches(EMAIL)) {
            emailET.setError("Enter a valid email address");
            return false;
        }

        return true;
    }

    private boolean passwordValidation() {
        if(passwordET.getText().toString().isEmpty() || !passwordET.getText().toString().matches(PASSWORD)) {
            passwordET.setError("Enter a valid password");
            return false;
        }

        return true;
    }

    private boolean phonedValidation() {
        if(phoneET.getText().toString().isEmpty() || !phoneET.getText().toString().matches(PHONE)) {
            phoneET.setError("Enter a valid phone number");
            return false;
        }

        return true;
    }
}