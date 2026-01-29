package com.revhire.dao;

import com.revhire.db.DBConnection;
import com.revhire.model.User;

import java.sql.*;

public class UserDAO {

    public long register(String fullName, String email, String passHash,
                         String role, String q, String a) {

        String insertSql =
                "INSERT INTO RH_USERS (FULL_NAME, EMAIL, PASSWORD_HASH, ROLE, SECURITY_Q, SECURITY_A) " +
                "VALUES (?,?,?,?,?,?)";
        String idSql = "SELECT RH_USERS_SEQ.CURRVAL FROM DUAL";

        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();

            ps = con.prepareStatement(insertSql);
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, passHash);
            ps.setString(4, role);
            ps.setString(5, q);
            ps.setString(6, a);
            ps.executeUpdate();

            st = con.createStatement();
            rs = st.executeQuery(idSql);
            if (rs.next()) return rs.getLong(1);

            return -1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(st);
            close(ps);
            close(con);
        }
    }

    public User login(String email, String passHash) {
        String sql = "SELECT USER_ID, FULL_NAME, EMAIL, ROLE FROM RH_USERS WHERE EMAIL=? AND PASSWORD_HASH=?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, passHash);

            rs = ps.executeQuery();
            if (rs.next()) return new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4));
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(ps);
            close(con);
        }
    }

    public boolean changePassword(long userId, String oldHash, String newHash) {
        String sql = "UPDATE RH_USERS SET PASSWORD_HASH=? WHERE USER_ID=? AND PASSWORD_HASH=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, newHash);
            ps.setLong(2, userId);
            ps.setString(3, oldHash);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    public String getSecurityQ(String email) {
        String sql = "SELECT SECURITY_Q FROM RH_USERS WHERE EMAIL=?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(ps);
            close(con);
        }
    }

    public boolean resetPassword(String email, String secA, String newHash) {
        String sql = "UPDATE RH_USERS SET PASSWORD_HASH=? WHERE EMAIL=? AND SECURITY_A=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, newHash);
            ps.setString(2, email);
            ps.setString(3, secA);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    private void close(AutoCloseable c) {
        try { if (c != null) c.close(); } catch (Exception ignored) {}
    }
}
