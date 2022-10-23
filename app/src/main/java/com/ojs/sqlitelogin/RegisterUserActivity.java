/*
    CODE ATTRIBUTION:
    How to use SQLite on Android
    https://www.youtube.com/watch?v=312RhjfetP8
    Source: FreeCodeCamp - Shad Sluiter
    https://www.youtube.com/user/shadsluiter
 */

package com.ojs.sqlitelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ojs.sqlitelogin.models.User;

import java.security.NoSuchAlgorithmException;

public class RegisterUserActivity extends AppCompatActivity {

    // Global declarations
    private EditText registerUsername_et, registerPassword_et, registerConfirmPassword_et;
    private Button registerUser_bt;
    private TextView signIn_tv;

    private User user;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        user = new User();
        userDAO = new UserDAO(RegisterUserActivity.this);

        registerUsername_et = findViewById(R.id.registerEmail_et);
        registerPassword_et = findViewById(R.id.register_password_et);
        registerConfirmPassword_et = findViewById(R.id.register_confirm_Password_et);
        signIn_tv = findViewById(R.id.signIn_tv);

        registerUser_bt = findViewById(R.id.registerUser_bt);
        registerUser_bt.setOnClickListener(v -> {
            if(validateInputs()){
                if(userDAO.isUsernameUnique(registerUsername_et.getText().toString())){
                    // store only the password hash
                    try {
                        user.setPassword(hashPassword(registerPassword_et.getText().toString()));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    boolean success = userDAO.addOne(user);
                    if(success){
                        login();
                        Toast.makeText(this, "User successfully registered", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Username not available!", Toast.LENGTH_SHORT).show();
                    registerUsername_et.setText("");
                    registerUsername_et.setError("Username unavailable");
                    registerUsername_et.requestFocus();
                }
            }
        });

        // go back to the login pack if sign in text is clicked
        signIn_tv.setOnClickListener(v -> {
            //Intent nw = new Intent(RegisterUserActivity.this, LoginActivity.class);
            finish();
            //startActivity(nw);
        });
    }

    private boolean validateInputs(){
        //reset the user object
        user = new User();

        // set the attributes
        user.setUsername(registerUsername_et.getText().toString().trim());
        user.setPassword(registerPassword_et.getText().toString());

        // set valid bool to true
        boolean isValid = true;

        // confirm the username is not empty and meets the minimum security requirements
        if(TextUtils.isEmpty(user.getUsername())){
            registerUsername_et.setError("Email required");
            registerUsername_et.requestFocus();
            isValid = false;
        } else if (user.getUsername().length() < 8 || !user.getUsername().contains("@")){
            registerUsername_et.setError("Invalid email address");
            registerUsername_et.requestFocus();
            isValid = false;
        }

        // confirm the password is not empty and meets the minimum security requirements
        if(TextUtils.isEmpty(user.getPassword())){
            registerPassword_et.setError("Password required");
            registerPassword_et.requestFocus();
            isValid = false;
        } else if (!user.getPassword().matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$") || user.getPassword().length() < 6){
            registerPassword_et.setText("");
            registerConfirmPassword_et.setText("");
            registerPassword_et.setError("Invalid password");
            registerPassword_et.requestFocus();
            isValid = false;
        }

        // confirm that the passwords match
        if (TextUtils.isEmpty(registerConfirmPassword_et.getText().toString()) || !user.getPassword().equals(registerConfirmPassword_et.getText().toString())){
            registerPassword_et.setText("");
            registerPassword_et.setError("Password does not match");
            registerConfirmPassword_et.setText("");
            registerConfirmPassword_et.setError("Password does not match");
            registerPassword_et.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void login(){
        HomeActivity.currentUser = user.getUsername().substring(0, user.getUsername().indexOf("@"));
        Intent nw = new Intent(RegisterUserActivity.this, HomeActivity.class);
        finish();// close the registration activity
        finish();// close the login activity
        startActivity(nw);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        Encryption encryption = new Encryption();
        String salt = encryption.getSalt();
        user.setSalt(salt);
        return encryption.get_SHA_512_SecurePassword(password, salt);
    }

//===
}