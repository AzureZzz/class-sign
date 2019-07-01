package com.example.classhelp.entity;

public class SignInfo {

    private Long userId;
    private Long taskId;
    private Integer signState;
    private String signTime;
    private String userName;
    private String taskName;
    private String signType;
    private String taskType;
    private String taskLongitude;
    private String taskLatitude;
    private String taskEnd;

    public SignInfo() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getSignState() {
        return signState;
    }

    public void setSignState(Integer signState) {
        this.signState = signState;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskLongitude() {
        return taskLongitude;
    }

    public void setTaskLongitude(String taskLongitude) {
        this.taskLongitude = taskLongitude;
    }

    public String getTaskLatitude() {
        return taskLatitude;
    }

    public void setTaskLatitude(String taskLatitude) {
        this.taskLatitude = taskLatitude;
    }

    public String getTaskEnd() {
        return taskEnd;
    }

    public void setTaskEnd(String taskEnd) {
        this.taskEnd = taskEnd;
    }
}
