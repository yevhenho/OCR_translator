package com.example.yevhenho.git_hub_test.db;

import android.content.ContentValues;
import android.database.Cursor;

import rx.functions.Func1;

public class UserDAO {
    public static final String TABLE = "users";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String COMPANY = "company";
    public static final String AVATAR_URL = "avatarUrl";
    public static final String URL = "url";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";
    public static final String GISTS = "gists";
    public static final String REPOS = "repos";
    public static final String IS_SAVED = "isSaved";

    public long id;
    private String name;
    private String company;
    private String avatarUrl;
    private String url;
    private int followers;
    private int following;
    private int gists;
    private int repos;
    private boolean isSaved;

    public UserDAO(long id, String name, String company, String avatarUrl, String url, int followers, int following,
                   int gists, int repos, boolean isSaved) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.avatarUrl = avatarUrl;
        this.url = url;
        this.followers = followers;
        this.following = following;
        this.gists = gists;
        this.repos = repos;
        this.isSaved = isSaved;
    }

    public static final Func1<Cursor, UserDAO> MAPPER = new Func1<Cursor, UserDAO>() {
        @Override
        public UserDAO call(Cursor cursor) {
            long id = Db.getLong(cursor, ID);
            String name = Db.getString(cursor, NAME);
            String company = Db.getString(cursor, COMPANY);
            String avatarUrl = Db.getString(cursor, AVATAR_URL);
            String url = Db.getString(cursor, URL);
            int followers = Db.getInt(cursor, FOLLOWERS);
            int following = Db.getInt(cursor, FOLLOWING);
            int gists = Db.getInt(cursor, GISTS);
            int repos = Db.getInt(cursor, REPOS);
            boolean isSaved = Db.getBoolean(cursor, IS_SAVED);
            return new UserDAO(id, name, company, avatarUrl, url, followers, following,
                    gists, repos, isSaved);
        }
    };

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public Builder name(String name) {
            values.put(NAME, name);
            return this;
        }

        public Builder company(String company) {
            values.put(COMPANY, company);
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            values.put(AVATAR_URL, avatarUrl);
            return this;
        }

        public Builder url(String url) {
            values.put(URL, url);
            return this;
        }

        public Builder followers(int followers) {
            values.put(FOLLOWERS, followers);
            return this;
        }

        public Builder following(int following) {
            values.put(FOLLOWING, following);
            return this;
        }

        public Builder gists(int gists) {
            values.put(GISTS, gists);
            return this;
        }

        public Builder repos(int repos) {
            values.put(REPOS, repos);
            return this;
        }

        public Builder isSaved(boolean isSaved) {
            values.put(IS_SAVED, isSaved);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }

}
