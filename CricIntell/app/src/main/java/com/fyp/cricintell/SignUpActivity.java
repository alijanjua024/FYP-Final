package com.fyp.cricintell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    EditText nameET, emailET, passwordET, confirmPasswordEt;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        nameET = findViewById(R.id.signupNameEt);
        emailET = findViewById(R.id.signupEmailEt);
        passwordET = findViewById(R.id.signupPasswordEt);
        confirmPasswordEt = findViewById(R.id.signupConfirmPasswordEt);

    }

    public void signUp(View view) {
        if(nameET.getText().toString().equals("") || emailET.getText().toString().equals("") || passwordET.getText().toString().equals("") || confirmPasswordEt.getText().toString().equals("")){
            Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_SHORT).show();
        }else if(!passwordET.getText().toString().equals(confirmPasswordEt.getText().toString())){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else{

            mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nameET.getText().toString()).build();
                                user.updateProfile(profileUpdates);
                            }
                            else {
                                Log.i("FirebaseAuthLog",task.getException().toString());
                                Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }
    }

    public void goToLogin(View view) {
        finish();
    }
}
