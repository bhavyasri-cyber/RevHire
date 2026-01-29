package com.revhire.dao;

import com.revhire.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public boolean apply(long jobId, long userId, String coverLetter) {
        String sql = "INSERT INTO RH_APPLICATIONS (JOB_ID, USER_ID, COVER_LETTER) VALUES (?,?,?)";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, jobId);
            ps.setLong(2, userId);
            ps.setString(3, coverLetter);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            return false;
        } finally {
            close(ps);
            close(con);
        }
    }

    public List<String> myApplications(long userId) {
        String sql =
                "SELECT a.APP_ID, a.JOB_ID, j.TITLE, c.NAME, a.STATUS, a.APPLIED_AT " +
                "FROM RH_APPLICATIONS a " +
                "JOIN RH_JOBS j ON j.JOB_ID=a.JOB_ID " +
                "JOIN RH_COMPANIES c ON c.COMPANY_ID=j.COMPANY_ID " +
                "WHERE a.USER_ID=? ORDER BY a.APP_ID DESC";

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
                out.add("APP_ID=" + rs.getLong(1) +
                        " | JOB_ID=" + rs.getLong(2) +
                        " | " + rs.getString(3) +
                        " | " + rs.getString(4) +
                        " | " + rs.getString(5) +
                        " | " + rs.getDate(6));
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

    public boolean withdraw(long jobId, long userId, String reason) {
        String sql =
                "UPDATE RH_APPLICATIONS SET STATUS='WITHDRAWN', WITHDRAW_REASON=?, UPDATED_AT=SYSDATE " +
                "WHERE JOB_ID=? AND USER_ID=? AND STATUS IN ('APPLIED','SHORTLISTED')";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setLong(2, jobId);
            ps.setLong(3, userId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    public List<String> applicantsForJob(long companyId, long jobId,
                                         Integer expFilter, String skillsFilter,
                                         Date fromDate, Date toDate) {

        String sql =
                "SELECT a.APP_ID, u.USER_ID, u.FULL_NAME, u.EMAIL, a.STATUS, a.APPLIED_AT, r.SKILLS " +
                "FROM RH_APPLICATIONS a " +
                "JOIN RH_USERS u ON u.USER_ID=a.USER_ID " +
                "JOIN RH_JOBS j ON j.JOB_ID=a.JOB_ID " +
                "LEFT JOIN RH_RESUMES r ON r.USER_ID=u.USER_ID " +
                "WHERE j.COMPANY_ID=? AND j.JOB_ID=? " +
                "AND (? IS NULL OR j.MIN_EXP_YEARS <= ?) " +
                "AND (? IS NULL OR LOWER(NVL(r.SKILLS,' ')) LIKE LOWER(?)) " +
                "AND (? IS NULL OR a.APPLIED_AT >= ?) " +
                "AND (? IS NULL OR a.APPLIED_AT <= ?) " +
                "ORDER BY a.APP_ID DESC";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> out = new ArrayList<String>();

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);

            ps.setLong(1, companyId);
            ps.setLong(2, jobId);

            if (expFilter == null) { ps.setNull(3, Types.NUMERIC); ps.setNull(4, Types.NUMERIC); }
            else { ps.setInt(3, expFilter.intValue()); ps.setInt(4, expFilter.intValue()); }

            if (skillsFilter == null || skillsFilter.trim().length() == 0) {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(5, skillsFilter);
                ps.setString(6, "%" + skillsFilter + "%");
            }

            if (fromDate == null) { ps.setNull(7, Types.DATE); ps.setNull(8, Types.DATE); }
            else { ps.setDate(7, fromDate); ps.setDate(8, fromDate); }

            if (toDate == null) { ps.setNull(9, Types.DATE); ps.setNull(10, Types.DATE); }
            else { ps.setDate(9, toDate); ps.setDate(10, toDate); }

            rs = ps.executeQuery();
            while (rs.next()) {
                out.add("APP_ID=" + rs.getLong(1) +
                        " | USER_ID=" + rs.getLong(2) +
                        " | " + rs.getString(3) +
                        " | " + rs.getString(4) +
                        " | " + rs.getString(5) +
                        " | " + rs.getDate(6) +
                        " | skills=" + rs.getString(7));
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

    public boolean updateStatusWithNotification(long appId, String status, String comment,
                                                long applicantUserId, String notifMsg) {

        String upd = "UPDATE RH_APPLICATIONS SET STATUS=?, COMMENT_TEXT=?, UPDATED_AT=SYSDATE WHERE APP_ID=?";
        String ins = "INSERT INTO RH_NOTIFICATIONS (USER_ID, MESSAGE) VALUES (?,?)";

        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            ps1 = con.prepareStatement(upd);
            ps1.setString(1, status);
            ps1.setString(2, comment);
            ps1.setLong(3, appId);
            int a = ps1.executeUpdate();

            ps2 = con.prepareStatement(ins);
            ps2.setLong(1, applicantUserId);
            ps2.setString(2, notifMsg);
            ps2.executeUpdate();

            con.commit();
            con.setAutoCommit(true);
            return a == 1;

        } catch (SQLException e) {
            rollback(con);
            throw new RuntimeException(e);

        } finally {
            close(ps2);
            close(ps1);
            resetAutoCommit(con);
            close(con);
        }
    }

    public int bulkUpdateStatus(long[] appIds, String status, String comment) {
        String sql = "UPDATE RH_APPLICATIONS SET STATUS=?, COMMENT_TEXT=?, UPDATED_AT=SYSDATE WHERE APP_ID=?";

        Connection con = null;
        PreparedStatement ps = null;
        int updated = 0;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);

            for (int i = 0; i < appIds.length; i++) {
                ps.setString(1, status);
                ps.setString(2, comment);
                ps.setLong(3, appIds[i]);
                updated += ps.executeUpdate();
            }

            return updated;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    private void rollback(Connection con) {
        try { if (con != null) con.rollback(); } catch (Exception ignored) {}
    }

    private void resetAutoCommit(Connection con) {
        try { if (con != null) con.setAutoCommit(true); } catch (Exception ignored) {}
    }

    private void close(AutoCloseable c) {
        try { if (c != null) c.close(); } catch (Exception ignored) {}
    }
}
