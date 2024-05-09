package com.example.qrshare;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String databaseName = "QRShare.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {

        //Create users table
        MyDatabase.execSQL("create Table users(username TEXT, email TEXT primary key, password TEXT)");

        // Create qrcodes table for history frag
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS qrcodes (content TEXT, type TEXT, timestamp TEXT, image BLOB, favorite INTEGER DEFAULT 0)");

        //Create favorite table
        MyDatabase.execSQL("CREATE TABLE IF NOT EXISTS favorite (id INTEGER primary key AUTOINCREMENT, content TEXT, type TEXT, timestamp TEXT, image BLOB)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int i, int i1) {

        //Drop tables if they exists
        MyDatabase.execSQL("drop Table if exists users");
        MyDatabase.execSQL("drop Table if exists qrcodes");
        MyDatabase.execSQL("drop Table if exists favorite");

        onCreate(MyDatabase);
    }

    //Insert user data into users table
    public boolean insertData(String username, String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = MyDatabase.insert("users", null, contentValues);

        if(result == -1){
            return  false;
        }
        else{
            return true;
        }
    }


    //Check if email exits in users table
    public boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ?", new String[]{email});

        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }


    public  boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ? and password = ?", new String[]{email, password});

        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }


    // Insert QR code details into qrcodes table
    public boolean insertQRCode(String content, String type, String timestamp, byte[] imageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("content", content);
        contentValues.put("type", type);
        contentValues.put("timestamp", timestamp);
        contentValues.put("image", imageByteArray); //store the image Byte Array
        long result = db.insert("qrcodes", null, contentValues);
        return result != -1;
    }


    //for history
    // Retrieve all QR code history from qrcodes table
    public ArrayList<QRCodeInfo> getAllQRCodeHistory() {
        ArrayList<QRCodeInfo> qrCodeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM qrcodes ORDER BY timestamp DESC", null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                @SuppressLint("Range") byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex("image"));
                qrCodeList.add(new QRCodeInfo(content, type, timestamp, imageByteArray));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return qrCodeList;
    }

    //for favorite
    // Retrieve favorite QR code list from qrcodes table
    public ArrayList<QRCodeInfo> getFavoriteQRCodeList() {
        ArrayList<QRCodeInfo> favoriteQRCodeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM qrcodes WHERE favorite = 1 ORDER BY timestamp DESC", null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                @SuppressLint("Range") byte[] imageByteArray = cursor.getBlob(cursor.getColumnIndex("image"));
                favoriteQRCodeList.add(new QRCodeInfo(content, type, timestamp, imageByteArray));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteQRCodeList;
    }


    //     Method to mark a QR code as favorite
    public boolean markAsFavorite(String content, byte[] imageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("favorite", 1);
        values.put("image", imageByteArray);
        int affectedRows = db.update("qrcodes", values, "content = ?", new String[]{content});
//        return affectedRows > 0;


        if (affectedRows > 0){

            Cursor cursor = db.query("qrcodes", null, "content = ?", new String[]{content}, null, null, null);
            if(cursor.moveToFirst()){
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

                ContentValues favoriteValues = new ContentValues();
                favoriteValues.put("content", content);
                favoriteValues.put("type", type);
                favoriteValues.put("timestamp", timestamp);
                favoriteValues.put("image", imageByteArray);
                long favoriteRowId = db.insert("favorite", null, favoriteValues);

                return favoriteRowId != -1;
            }
            cursor.close();

        }
        return  false;

    }

    // Method to remove a QR code from favorites
    public boolean removeFromFavorites(String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("favorite", 0);
        int affectedRows = db.update("qrcodes", values, "content = ?", new String[]{content});
//        return affectedRows > 0;

        int affectedRowsFavorites = db.delete("favorite", "content = ?", new String[]{content});

        return affectedRows > 0 && affectedRowsFavorites >0;

    }



    //for Settings/More fragment
    // Method to retrieve username and email from the user table
    public User getUserData() {
        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);
        if (cursor.moveToLast()) {
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
            user = new User(username, email);
        }

        cursor.close();
        return user;
    }


    // Method to clear QR code history
    public void clearQRCodeHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM qrcodes");
        db.close();
    }
}
