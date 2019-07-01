package com.example.classhelp;

public final class Contract {
    private Contract() {
    }

    public static class Token{
        public static final String TOKEN = "token";
    }

    public static class User {
        public static final String ID = "userId";
        public static final String PHONE = "userPhone";
        public static final String PASSWORD = "userPassword";
        public static final String STUNO = "userNo";
        public static final String SCHOOL = "userSchool";
        public static final String TYPE = "userType";

        public static final String SEX = "userSex";
        public static final String NICKNAME = "userNickname";
        public static final String NAME = "userName";
        public static final String HEADSRC = "userHeadSrc";
        public static final String MAJOR = "userMajor";
        public static final String GRADE = "userGrade";
        public static final String EMAIL = "userEmail";
    }

    public static class JSONKEY {
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USERINFO = "userInfo";
        public static final String NEWTOKEN = "newToken";
        public static final String CONTENT = "content";
        public static final String LISTTEA = "listTea";
        public static final String LIST = "list";
        public static final String SIGNSTATE = "signState";
    }

    public static class Class{
        public static final String ID = "classId";
        public static final String CODE = "classCode";
        public static final String NAME = "className";
        public static final String NO = "classNo";
        public static final String QRCODESRC = "classQrcodeSrc";
        public static final String QRCODE = "classQrcode";
        public static final String USERID = "userId";
    }

    public static class SignTask{
        public static final String ID = "taskId";
        public static final String TYPE = "taskType";
        public static final String END = "taskEnd";
        public static final String NAME = "taskName";
        public static final String LONGITUDE = "taskLongitude";
        public static final String LATITUDE = "taskLatitude";
        public static final String CODE = "checkInCode";
        public static final String TASKCODE = "taskCode";
        public static final String QRCODESRC = "qrCodeSrc";
    }

    public static class SignIn{
        public static final String LOCATIONTYPE = "locationType";
        public static final String CODE = "code";
    }

    public static class OTHER{
        public static final String VERIFYCODE = "verifyCode";
        public static final String FILE = "file";
    }

    public static class SP{
        public static final String TOKEN_FILE_NAME = "sp_token";
        public static final String USER_FILE_NAME = "sp_user";
        public static final String REMEMBER_FILE_NAME = "sp_login";
        public static final String LOGIN_PHONE = "login_phone";
        public static final String LOGIN_PASSWORD = "login_passowrd";
        public static final String LOGIN_REMEMBER = "loginn_remember";
    }
}
