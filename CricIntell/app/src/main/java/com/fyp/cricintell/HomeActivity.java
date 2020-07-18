package com.fyp.cricintell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.cricintell.database.DBManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView nameTv;
    LinearLayout logoutIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        nameTv = findViewById(R.id.nameTv);
        logoutIcon = findViewById(R.id.logoutIcon);
        if (mAuth.getCurrentUser() != null) {
            nameTv.setText("Hello " + mAuth.getCurrentUser().getDisplayName() + "!");
        } else {
            nameTv.setText("Hello Guest!");
            logoutIcon.setVisibility(LinearLayout.GONE);
        }
    }

    public void logout(View view) {
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    public void teamsClick(View view) {
        startActivity(new Intent(HomeActivity.this, TeamsActivity.class));
    }

    public void analysisClick(View view) {
        startActivity(new Intent(HomeActivity.this, AnalysisActivity.class));
    }

    public void predictionClick(View view) {
        startActivity(new Intent(HomeActivity.this, PredictionActivity.class));
    }

    public void analysisLogClick(View view) {

        startActivity(new Intent(HomeActivity.this, AnalysisLogActivity.class));
    }

    public void settingsClick(View view) {
        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));

    }
}
