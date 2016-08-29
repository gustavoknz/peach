package com.gustavok.peach;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

class SenatorDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Senators.db";
    private static final String INT_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SenatorEntry.TABLE_NAME + " (" +
                    SenatorEntry.COLUMN_NAME_ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    SenatorEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    SenatorEntry.COLUMN_NAME_PARTY + TEXT_TYPE + COMMA_SEP +
                    SenatorEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    SenatorEntry.COLUMN_NAME_VOTE1 + INT_TYPE + COMMA_SEP +
                    SenatorEntry.COLUMN_NAME_VOTE2 + INT_TYPE + COMMA_SEP +
                    SenatorEntry.COLUMN_NAME_URL + TEXT_TYPE + ")";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SenatorEntry.TABLE_NAME;

    public SenatorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static abstract class SenatorEntry implements BaseColumns {
        public static final String TABLE_NAME = "senators";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PARTY = "party";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_VOTE1 = "vote1";
        public static final String COLUMN_NAME_VOTE2 = "vote2";
        public static final String COLUMN_NAME_URL = "url";
    }
}