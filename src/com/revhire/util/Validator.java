package com.revhire.util;



public class Validator {

    private Validator() {}

    public static boolean isEmail(String email) {
        if (email == null) return false;
        email = email.trim();
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isPasswordOk(String pass) {
        if (pass == null) return false;
        pass = pass.trim();
        return pass.length() >= 6;
    }

    public static boolean isStatusShortlistReject(String status) {
        if (status == null) return false;
        status = status.trim().toUpperCase();
        return "SHORTLISTED".equals(status) || "REJECTED".equals(status);
    }

    public static boolean isDateYYYYMMDD(String s) {
        if (s == null) return false;
        s = s.trim();
        if (!s.matches("^\\d{4}-\\d{2}-\\d{2}$")) return false;
        try {
           
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
