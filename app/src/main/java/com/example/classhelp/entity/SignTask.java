package com.example.classhelp.entity;

public class SignTask {
    private String taskId;
    private Integer taskType;
    private String taskStart;
    private String taskEnd;
    private String taskCode;
    private String taskLongitude;
    private String taskLatitude;
    private String taskName;
    private String taskQrSrc;
    private String classId;

    public SignTask() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskStart() {
        return taskStart;
    }

    public void setTaskStart(String taskStart) {
        this.taskStart = taskStart;
    }

    public String getTaskEnd() {
        return taskEnd;
    }

    public void setTaskEnd(String taskEnd) {
        this.taskEnd = taskEnd;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskQrSrc() {
        return taskQrSrc;
    }

    public void setTaskQrSrc(String taskQrSrc) {
        this.taskQrSrc = taskQrSrc;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
