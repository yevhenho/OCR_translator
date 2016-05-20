package com.example.yevhenho.git_hub_test.db;


import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class RepoDAO {
    public static final String TABLE = "repos";

    public static final String ID = "_id";
    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String LANG = "lang";
    public static final String FORKS = "forks";
    public static final String STARS = "stars";

    public long id;
    public long userId;
    public String name;
    public String lang;
    public int forks;
    public int stars;

    public RepoDAO(long id, long userId, String name, String lang, int forks, int stars) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.lang = lang;
        this.forks = forks;
        this.stars = stars;
    }

    public static Func1<Cursor, List<RepoDAO>> MAP = new Func1<Cursor, List<RepoDAO>>() {
        @Override
        public List<RepoDAO> call(final Cursor cursor) {
            try {
                List<RepoDAO> values = new ArrayList<>(cursor.getCount());

                while (cursor.moveToNext()) {
                    long id = Db.getLong(cursor, ID);
                    long userId = Db.getLong(cursor, USER_ID);
                    String name = Db.getString(cursor, NAME);
                    String lang = Db.getString(cursor, LANG);
                    int forks = Db.getInt(cursor, FORKS);
                    int stars = Db.getInt(cursor, STARS);
                    values.add(new RepoDAO(id, userId, name, lang, forks, stars));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public Builder userId(long userId) {
            values.put(USER_ID, userId);
            return this;
        }

        public Builder name(String name) {
            values.put(NAME, name);
            return this;
        }

        public Builder lang(String lang) {
            values.put(LANG, lang);
            return this;
        }

        public Builder forks(String forks) {
            values.put(FORKS, forks);
            return this;
        }

        public Builder stars(String stars) {
            values.put(STARS, stars);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}

