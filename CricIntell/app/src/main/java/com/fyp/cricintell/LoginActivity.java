package com.fyp.cricintell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailEt, passwordEt;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        emailEt = findViewById(R.id.loginEmailEt);
        passwordEt = findViewById(R.id.loginPasswordEt);
        sharedPreferences = getSharedPreferences("CricIntellPrefs", MODE_PRIVATE);
    }

    public void login(View view) {
        if (emailEt.getText().toString().equals("") || passwordEt.getText().toString().equals("")) {
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(emailEt.getText().toString(), passwordEt.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Log.i("FirebaseAuthLog",task.getException().toString());
                                Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void goToSignup(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    public void continueAsGuest(View view) {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }
}
