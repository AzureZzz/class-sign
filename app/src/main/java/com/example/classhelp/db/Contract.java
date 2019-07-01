package com.example.classhelp.db;

import android.provider.BaseColumns;

public final class Contract {
    private Contract() {
    }

    public static class TokenEntry implements BaseColumns {
        public static final String TABLE_NAME = "tbl_token";
        public static final String COLUMN_NAME_TOKEN = "token";
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "tbl_user";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STUNO = "stuno";
        public static final String COLUMN_NAME_SCHOOL = "school";
        public static final String COLUMN_NAME_TYPE = "type";
    }

    public static class UserInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "tbl_user_info";
        public static final String COLUMN_NAME_SEX = "sex";
        public static final String COLUMN_NAME_NICKNAME = "nickname";
        public static final String COLUMN_NAME_HEADIMG = "headimg";
        public static final String COLUMN_NAME_MAJOR = "major";
        public static final String COLUMN_NAME_GRADE = "grade";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

}
