package com.janhvi.qrshare.database;

public class SqliteDatabaseConstants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "QRShare.db";
    //TABLE NAME & COLUMNS
    public static final String TABLE_USER = "User";
    public static final String TABLE_QRCODE = "QRCode";

    public static final String UID = "Uid";
    public static final String EMAIL = "Email";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";

    public static final String QRCODE_ID = "Qid";
    public static final String CONTENT = "Content";
    public static final String TYPE = "Type";
    public static final String DATE = "Date";
    public static final String TIME = "Time";
    public static final String IMAGE = "Image";
    public static final String IS_FAVORITE = "IsFavorite";

}
