package com.example.classhelp.entity;

public class ClassDetail {

    private String classId;
    private String userHeadSrc;
    private String userName;
    private String classCode;
    private Integer stuNumber;
    private String className;
    private String classQrcodeSrc;

    public ClassDetail() {
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getUserHeadSrc() {
        return userHeadSrc;
    }

    public void setUserHeadSrc(String userHeadSrc) {
        this.userHeadSrc = userHeadSrc;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getStuNumber() {
        return stuNumber;
    }

    public void setStuNumber(Integer stuNumber) {
        this.stuNumber = stuNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassQrcodeSrc() {
        return classQrcodeSrc;
    }

    public void setClassQrcodeSrc(String classQrcodeSrc) {
        this.classQrcodeSrc = classQrcodeSrc;
    }
}
