package com.revhire.dao;

import com.revhire.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<String> unread(long userId) {
        String sql = "SELECT NOTIF_ID, MESSAGE, CREATED_AT FROM RH_NOTIFICATIONS WHERE USER_ID=? AND IS_READ=0 ORDER BY NOTIF_ID DESC";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> out = new ArrayList<String>();

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, userId);

            rs = ps.executeQuery();
            while (rs.next()) {
                out.add("NOTIF_ID=" + rs.getLong(1) + " | " + rs.getString(2) + " | " + rs.getDate(3));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(ps);
            close(con);
        }
    }

    public boolean markRead(long notifId, long userId) {
        String sql = "UPDATE RH_NOTIFICATIONS SET IS_READ=1 WHERE NOTIF_ID=? AND USER_ID=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, notifId);
            ps.setLong(2, userId);
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
