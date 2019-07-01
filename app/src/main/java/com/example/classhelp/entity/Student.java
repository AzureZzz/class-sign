package com.example.classhelp.entity;

public class Student {

    private Long userId;
    private String userPhone;
    private String userName;
    private String userNo;
    private String userSchool;
    private String userType;
    private String userHeadSrc;

    public Student() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserHeadSrc() {
        return userHeadSrc;
    }

    public void setUserHeadSrc(String userHeadSrc) {
        this.userHeadSrc = userHeadSrc;
    }
}
