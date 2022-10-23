package com.ojs.sqlitelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    // Global declarations
    public static String currentUser = "";
    private TextView currentUserName;
    private Button signOut_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currentUserName = findViewById(R.id.current_user_label);
        currentUserName.setText(currentUser);

        signOut_bt = findViewById(R.id.sign_out_button);
        signOut_bt.setOnClickListener(v -> {
            // reset the current user variable
            currentUser = "";
            // close this activity and reopen the login activity
            Intent nw = new Intent(HomeActivity.this, LoginActivity.class);
            finish();
            startActivity(nw);
        });
    }
}