package com.example.yevhenho.git_hub_test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static final String CREATE_LIST = ""
            + "CREATE TABLE " + RepoDAO.TABLE + "("
            + RepoDAO.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + RepoDAO.NAME + " TEXT NOT NULL,"
            + RepoDAO.ARCHIVED + " INTEGER NOT NULL DEFAULT 0"
            + ")";
    private static final String CREATE_ITEM = ""
            + "CREATE TABLE " + UserDAO.TABLE + "("
            + UserDAO.ID + " INTEGER NOT NULL PRIMARY KEY,"
            + UserDAO.LIST_ID + " INTEGER NOT NULL REFERENCES " + RepoDAO.TABLE + "(" + RepoDAO.ID + "),"
            + UserDAO.DESCRIPTION + " TEXT NOT NULL,"
            + UserDAO.COMPLETE + " INTEGER NOT NULL DEFAULT 0"
            + ")";
    private static final String CREATE_ITEM_LIST_ID_INDEX =
            "CREATE INDEX item_list_id ON " + UserDAO.TABLE + " (" + UserDAO.LIST_ID + ")";

    public DbOpenHelper(Context context) {
        super(context, "todo.db", null /* factory */, VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LIST);
        db.execSQL(CREATE_ITEM);
        db.execSQL(CREATE_ITEM_LIST_ID_INDEX);

        long groceryListId = db.insert(RepoDAO.TABLE, null, new RepoDAO.Builder()
                .name("Grocery List")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(groceryListId)
                .description("Beer")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(groceryListId)
                .description("Point Break on DVD")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(groceryListId)
                .description("Bad Boys 2 on DVD")
                .build());

        long holidayPresentsListId = db.insert(RepoDAO.TABLE, null, new RepoDAO.Builder()
                .name("Holiday Presents")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(holidayPresentsListId)
                .description("Pogo Stick for Jake W.")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(holidayPresentsListId)
                .description("Jack-in-the-box for Alec S.")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(holidayPresentsListId)
                .description("Pogs for Matt P.")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(holidayPresentsListId)
                .description("Coal for Jesse W.")
                .build());

        long workListId = db.insert(RepoDAO.TABLE, null, new RepoDAO.Builder()
                .name("Work Items")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(workListId)
                .description("Finish SqlBrite library")
                .complete(true)
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(workListId)
                .description("Finish SqlBrite sample app")
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder()
                .listId(workListId)
                .description("Publish SqlBrite to GitHub")
                .build());

        long birthdayPresentsListId = db.insert(RepoDAO.TABLE, null, new RepoDAO.Builder()
                .name("Birthday Presents")
                .archived(true)
                .build());
        db.insert(UserDAO.TABLE, null, new UserDAO.Builder().listId(birthdayPresentsListId)
                .description("New car")
                .complete(true)
                .build());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}