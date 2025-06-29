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

    public User getUserByUid(long uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{UID, EMAIL, USERNAME, PASSWORD},  // include PASSWORD
                UID + " = ?",
                new String[]{String.valueOf(uid)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD)); // get password
            cursor.close();
            return new User(uid, email, username, password); // use constructor with password
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

    public boolean updatePassword(long uid, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, verify the old password matches
        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{PASSWORD},
                UID + " = ?",
                new String[]{String.valueOf(uid)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String currentPassword = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
            cursor.close();

            if (currentPassword != null && currentPassword.equals(oldPassword)) {
                ContentValues values = new ContentValues();
                values.put(PASSWORD, newPassword);

                int rowsUpdated = db.update(
                        TABLE_USER,
                        values,
                        UID + " = ?",
                        new String[]{String.valueOf(uid)}
                );
                return rowsUpdated > 0;
            }
        }

        if (cursor != null) cursor.close();
        return false;
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

    public boolean deleteAllQRCodes() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            int rowsDeleted = db.delete(TABLE_QRCODE, null, null);
            return rowsDeleted > 0; // returns true if any row was deleted
        } catch (Exception e) {
            Log.e(TAG, "Error deleting all QR codes: ", e);
            return false;
        }
    }

    public List<QRCode> getFavoriteQRCodes() {
        List<QRCode> favoriteList = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(
                     TABLE_QRCODE,
                     null,
                     IS_FAVORITE + " = ?",
                     new String[]{"1"},
                     null, null, null)) {

            while (cursor.moveToNext()) {
                favoriteList.add(getQRCodeFromCursor(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading favorite QR codes: ", e);
        }

        return favoriteList;
    }

    public boolean removeQrCodeFromFavorite(int qid) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(IS_FAVORITE, 0); // set favorite to false (0)

            int rowsUpdated = db.update(
                    TABLE_QRCODE,
                    values,
                    QRCODE_ID + " = ?",
                    new String[]{String.valueOf(qid)}
            );

            return rowsUpdated > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error removing QR code from favorites", e);
            return false;
        }
    }

    private QRCode getQRCodeFromCursor(Cursor cursor) {
        QRCode qrCode = new QRCode();
        qrCode.setQid(cursor.getInt(cursor.getColumnIndexOrThrow(QRCODE_ID)));
        qrCode.setContent(cursor.getString(cursor.getColumnIndexOrThrow(CONTENT)));
        qrCode.setType(cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));
        qrCode.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
        qrCode.setTime(cursor.getString(cursor.getColumnIndexOrThrow(TIME)));
//        question.setImage(Arrays.toString(cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE))).getBytes());
        qrCode.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE)));

//        qrCode.setIsFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(IS_FAVORITE))));
        qrCode.setIsFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(IS_FAVORITE)));

        return qrCode;
    }
}
