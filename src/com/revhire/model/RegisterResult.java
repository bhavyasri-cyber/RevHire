package com.revhire.model;

public class RegisterResult {
    private boolean success;
    private long userId;
    private String message;

    public RegisterResult(boolean success, long userId, String message) {
        this.success = success;
        this.userId = userId;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public long getUserId() { return userId; }
    public String getMessage() { return message; }
}
