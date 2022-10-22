package com.ojs.sqlitelogin;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ojs.sqlitelogin.models.User;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USERS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_USERNAME = "USERNAME";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_SALT = "SALT";

    public UserDAO(@Nullable Context context) {
        super(context, "ice4.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + USER_TABLE + " ("
                + COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_SALT + " TEXT)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

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

            //****
            System.console().printf("comparerHash:" + hashPassword(retrievedPassword, retrievedSalt));
            if(username.equals(retrievedUsername) && password.equals(hashPassword(retrievedPassword, retrievedSalt))){
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

    private String hashPassword(String password, String salt) {
        Encryption encryption = new Encryption();
        return encryption.get_SHA_512_SecurePassword(password, salt);
    }

//===
}
