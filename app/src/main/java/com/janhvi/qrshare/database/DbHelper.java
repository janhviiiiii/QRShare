package com.janhvi.qrshare.database;

import static com.janhvi.qrshare.database.SqliteDatabaseConstants.CONTENT;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.DATABASE_NAME;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.DATABASE_VERSION;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.DATE;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.EMAIL;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.IMAGE;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.IS_FAVORITE;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.PASSWORD;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.QRCODE_ID;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.TABLE_QRCODE;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.TABLE_USER;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.TIME;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.TYPE;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.UID;
import static com.janhvi.qrshare.database.SqliteDatabaseConstants.USERNAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.janhvi.qrshare.model.QRCode;
import com.janhvi.qrshare.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_USER + " ("
                + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL + " TEXT UNIQUE, "
                + USERNAME + " TEXT , "
                + PASSWORD + " TEXT );";

        String CREATE_QRCODE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_QRCODE + " ("
                + QRCODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTENT + " TEXT, "
                + TYPE + " TEXT, "
                + DATE + " TEXT, "
                + TIME + " TEXT, "
                + IMAGE + " BLOB, "
                + IS_FAVORITE + " INTEGER DEFAULT 0);";


        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_QRCODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QRCODE);
        onCreate(db);
    }

    public User userLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{"Uid", "Email", "Username"},
                "email=? AND password=?",
                new String[]{email, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            long uid = cursor.getLong(cursor.getColumnIndexOrThrow("Uid"));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
            cursor.close();
            return new User(uid, userEmail, username);
        }

        if (cursor != null) cursor.close();
        return null;
    }


    public long addOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if user already exists by email
        Cursor cursor = db.query(TABLE_USER, new String[]{UID},
                EMAIL + " = ?", new String[]{user.getEmail()},
                null, null, null);

        ContentValues values = new ContentValues();
        values.put(EMAIL, user.getEmail());
        values.put(USERNAME, user.getUsername());
        values.put(PASSWORD, user.getPassword());

        long result;

        if (cursor != null && cursor.moveToFirst()) {
            // User exists – update
            int existingUid = cursor.getInt(cursor.getColumnIndexOrThrow(UID));
            result = db.update(TABLE_USER, values, UID + " = ?", new String[]{String.valueOf(existingUid)});
        } else {
            // User does not exist – insert
            result = db.insert(TABLE_USER, null, values);
        }

        if (cursor != null) cursor.close();
        return result;
    }


    public long addOrUpdateQRCode(QRCode qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONTENT, qrCode.getContent());
        values.put(TYPE, qrCode.getType());
        values.put(DATE, qrCode.getDate());
        values.put(TIME, qrCode.getTime());
        values.put(IMAGE, qrCode.getImage());
        values.put(IS_FAVORITE, qrCode.getIsFavorite());

        if (qrCode.getQid() == 0) {  // Insert new QRCode
            long newRowId = db.insert(TABLE_QRCODE, null, values);
            qrCode.setQid(newRowId); // properly set the ID here
            return newRowId;
        } else {  // Update existing QRCode
            db.update(TABLE_QRCODE, values, QRCODE_ID + " = ?", new String[]{String.valueOf(qrCode.getQid())});
            return qrCode.getQid();
        }
    }

    public boolean deleteQRCodeByQid(int qid) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            // Delete the qrcode by its ID
            int rowsAffected = db.delete(TABLE_QRCODE, QRCODE_ID + "=?", new String[]{String.valueOf(qid)});
            return rowsAffected > 0; // Return true if deletion was successful
        } catch (Exception e) {
            Log.e(TAG, "Error deleting Qrcode: ", e);
            return false; // Return false if deletion failed
        }
    }

    public List<QRCode> getAllQRCode() {
        List<QRCode> qrCode = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(TABLE_QRCODE, null, null, null, null, null, null)) {

            while (cursor.moveToNext()) {
                qrCode.add(getQRCodeFromCursor(cursor));
            }
        }
        return qrCode;
    }


    private QRCode getQRCodeFromCursor(Cursor cursor) {
        QRCode question = new QRCode();
        question.setQid(cursor.getInt(cursor.getColumnIndexOrThrow(QRCODE_ID)));
        question.setContent(cursor.getString(cursor.getColumnIndexOrThrow(CONTENT)));
        question.setType(cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));
        question.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
        question.setTime(cursor.getString(cursor.getColumnIndexOrThrow(TIME)));
        question.setImage(Arrays.toString(cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE))).getBytes());
        question.setIsFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(IS_FAVORITE))));
        return question;
    }
}
