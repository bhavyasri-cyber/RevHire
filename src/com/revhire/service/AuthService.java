package com.revhire.service;

import com.revhire.dao.UserDAO;
import com.revhire.model.User;

import java.security.MessageDigest;

public class AuthService {
     UserDAO userDAO = new UserDAO();

    public long register(String name, String email, String pass, String role, String q, String a) {
        return userDAO.register(name, email, sha(pass), role, q, a);
    }

    public User login(String email, String pass) {
        return userDAO.login(email, sha(pass));
    }

    public boolean changePassword(long userId, String currentPass, String newPass) {
        return userDAO.changePassword(userId, sha(currentPass), sha(newPass));
    }

    public String securityQ(String email) {
        return userDAO.getSecurityQ(email);
    }

    public boolean reset(String email, String secA, String newPass) {
        return userDAO.resetPassword(email, secA, sha(newPass));
    }

     String sha(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < b.length; i++) sb.append(String.format("%02x", b[i]));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
