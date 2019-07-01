package com.example.classhelp.entity;

public class SignInItemInfo {

    private String releaseTime;
    private String signInTime;
    private String signInType;
    private String signInStatus;
    private Integer signInTypeCode;
    private Integer signInStatusCode;


    public SignInItemInfo() {
    }

    public SignInItemInfo(String releaseTime, String signInTime, Integer signInTypeCode,
                          Integer signInStatusCode) {
        this.releaseTime = releaseTime;
        this.signInTime = signInTime;
        this.signInTypeCode = signInTypeCode;
        this.signInStatusCode = signInStatusCode;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    public String getSignInType() {
        return signInType;
    }

    public void setSignInType(String signInType) {
        this.signInType = signInType;
    }

    public String getSignInStatus() {
        return signInStatus;
    }

    public void setSignInStatus(String signInStatus) {
        this.signInStatus = signInStatus;
    }

    public Integer getSignInTypeCode() {
        return signInTypeCode;
    }

    public void setSignInTypeCode(Integer signInTypeCode) {
        this.signInTypeCode = signInTypeCode;
    }

    public Integer getSignInStatusCode() {
        return signInStatusCode;
    }

    public void setSignInStatusCode(Integer signInStatusCode) {
        this.signInStatusCode = signInStatusCode;
    }
}
