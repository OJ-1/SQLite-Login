package com.ojs.sqlitelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // Global declarations
    private EditText loginUsername_et, loginPassword_et;
    private Button signIn_bt;
    private TextView register_tv;

    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDAO = new UserDAO(LoginActivity.this);

        // instantiate objects
        loginUsername_et = findViewById(R.id.loginEmail_et);
        loginPassword_et = findViewById(R.id.loginPassword_et);
        register_tv = findViewById(R.id.register_tv);
        signIn_bt = findViewById(R.id.signIn_bt);

        // set on click listeners
        signIn_bt.setOnClickListener(v -> {
            if(isValid()){
                boolean success = userDAO.attemptLogin(loginUsername_et.getText().toString(), loginPassword_et.getText().toString());
                if(success){
                    HomeActivity.currentUser = loginUsername_et.getText().toString().substring(0, loginUsername_et.getText().toString().indexOf("@"));
                    Intent nw = new Intent(LoginActivity.this, HomeActivity.class);
                    finish();
                    startActivity(nw);
                }
                else{
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register_tv.setOnClickListener(v -> {
            resetPage();
            // open the register user activity
            Intent nw = new Intent(LoginActivity.this, RegisterUserActivity.class);
            startActivity(nw);
        });

    }

    //Method to check the user login credentials are valid
    private boolean isValid(){
        // validate that the email (username) is not missing
        if(loginUsername_et.getText().toString() == null || loginUsername_et.getText().toString().isEmpty()){
            loginUsername_et.setError("Username required!");
            return false;
        }
        // validate the string is an email address
        else if (loginUsername_et.getText().toString().length() < 8 || !loginUsername_et.getText().toString().contains("@")){
            loginUsername_et.setError("Invalid email address");
            loginUsername_et.requestFocus();
            return false;
        }
        // validate that the password is not missing
        if(loginPassword_et.getText().toString() == null || loginPassword_et.getText().toString().isEmpty()){
            loginPassword_et.setError("Password required!");
            return false;
        }
        // if all inputs are valid return true
        return true;
    }

    // reset the edit text fields
    private void resetPage(){
        loginUsername_et.setText("");
        loginPassword_et.setText("");
    }
    
//===
}