/*
    CODE ATTRIBUTION:
    How to use SQLite on Android
    https://www.youtube.com/watch?v=312RhjfetP8
    Source: FreeCodeCamp - Shad Sluiter
    https://www.youtube.com/user/shadsluiter
 */

package com.ojs.sqlitelogin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ojs.sqlitelogin.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO extends SQLiteOpenHelper {

    // Global declarations
    public static final String USER_TABLE = "USERS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_SALT = "SALT";

    // create the db if it doesn't exist named "ice4.db"
    public UserDAO(@Nullable Context context) {
        super(context, "ice4.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the SQLite table
        String createTableStatement = "CREATE TABLE " + USER_TABLE + " ("
                + COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_SALT + " TEXT)";
        db.execSQL(createTableStatement);
    }

    // not needed for now, this is for when new versions are released
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // method to add a user to the SQLite database
    public boolean addOne(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, user.getUsername().trim());
        cv.put(COLUMN_PASSWORD, user.getPassword());
        cv.put(COLUMN_SALT, user.getSalt());

        long insert = db.insert(USER_TABLE, null, cv);
        if(insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    // method to retrieve all users from the list - not currently used but additionally implemented
    public List<User> getAll(){
        List<User> returnList = new ArrayList();

        String queryString = "SELECT * FROM " + USER_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do {
                int userID = cursor.getInt(0);
                String username = cursor.getString(1);
                String userPassword = cursor.getString(2);
                String userSalt = cursor.getString(2);

                User user = new User(userID, username, userPassword, userSalt);
                returnList.add(user);
            } while (cursor.moveToNext());
        }
        else{
            // do not do anything on failure
        }
        cursor.close();
        db.close();
        return returnList;
    }

    // method to delete a user from the SQLite db - again not used but additionally added
    public boolean deleteOne(User user){

        SQLiteDatabase db = this.getWritableDatabase();

        String queryString = "DELETE FROM " + USER_TABLE + " WHERE " + COLUMN_ID + " = " + user.getId();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            return true;
        }
        else {
            return false;
        }
    }

    // method to check if a username is unique in the SQLite db
    public boolean isUsernameUnique(String username){

        boolean isUnique;

        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USERNAME + " = '" + username + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            // return false if a matching record is found
            isUnique = false;
        }
        else{
            // return true if the username is not found
            isUnique = true;
        }
        cursor.close();
        db.close();

        return isUnique;
    }

    // method to authenticate user login credentials
    public boolean attemptLogin(String username, String password){
        boolean isSuccessful;

        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USERNAME + " = '" + username + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){

            // id is at index 0 so start from 1
            String retrievedUsername = cursor.getString(1);
            String retrievedPassword = cursor.getString(2);
            String retrievedSalt = cursor.getString(3);

            // check if the saved password hash matches the hashed login password
            if(username.equals(retrievedUsername) && retrievedPassword.equals(hashPassword(password, retrievedSalt))){
                isSuccessful = true;
            }
            else{
                isSuccessful = false;
            }
        }
        else{
            // return false if the username is not found
            isSuccessful = false;
        }
        cursor.close();
        db.close();

        return isSuccessful;

    }

    // method to has the password entered at login
    private String hashPassword(String password, String salt) {
        Encryption encryption = new Encryption();
        return encryption.get_SHA_512_SecurePassword(password, salt);
    }

//===
}
