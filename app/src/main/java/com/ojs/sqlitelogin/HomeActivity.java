package com.ojs.sqlitelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

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
            currentUser = "";
            Intent nw = new Intent(HomeActivity.this, LoginActivity.class);
            finish();
            startActivity(nw);
        });

        Toast.makeText(this, "Welcome: " + currentUser, Toast.LENGTH_SHORT).show();
    }
}