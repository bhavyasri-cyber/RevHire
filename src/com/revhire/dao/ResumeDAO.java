package com.revhire.dao;

import com.revhire.db.DBConnection;

import java.sql.*;

public class ResumeDAO {

    public Long getResumeId(long userId) {
        String sql = "SELECT RESUME_ID FROM RH_RESUMES WHERE USER_ID=?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, userId);

            rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(ps);
            close(con);
        }
    }

    public void upsertResume(long userId, String objective, String education,
                             String experience, String skills, String projects, String certs) {

        Long resumeId = getResumeId(userId);
        if (resumeId == null) insert(userId, objective, education, experience, skills, projects, certs);
        else update(resumeId.longValue(), objective, education, experience, skills, projects, certs);
    }

    private void insert(long userId, String objective, String education,
                        String experience, String skills, String projects, String certs) {

        String sql =
                "INSERT INTO RH_RESUMES (USER_ID, OBJECTIVE, EDUCATION, EXPERIENCE, SKILLS, PROJECTS, CERTIFICATIONS) " +
                "VALUES (?,?,?,?,?,?,?)";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setString(2, objective);
            ps.setString(3, education);
            ps.setString(4, experience);
            ps.setString(5, skills);
            ps.setString(6, projects);
            ps.setString(7, certs);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    private void update(long resumeId, String objective, String education,
                        String experience, String skills, String projects, String certs) {

        String sql =
                "UPDATE RH_RESUMES SET OBJECTIVE=?, EDUCATION=?, EXPERIENCE=?, SKILLS=?, PROJECTS=?, CERTIFICATIONS=?, UPDATED_AT=SYSDATE " +
                "WHERE RESUME_ID=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, objective);
            ps.setString(2, education);
            ps.setString(3, experience);
            ps.setString(4, skills);
            ps.setString(5, projects);
            ps.setString(6, certs);
            ps.setLong(7, resumeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    public String[] getResume(long userId) {
        String sql =
                "SELECT OBJECTIVE, EDUCATION, EXPERIENCE, SKILLS, PROJECTS, CERTIFICATIONS " +
                "FROM RH_RESUMES WHERE USER_ID=?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, userId);

            rs = ps.executeQuery();
            if (!rs.next()) return null;

            return new String[] {
                    rs.getString(1),
                    readClob(rs.getClob(2)),
                    readClob(rs.getClob(3)),
                    rs.getString(4),
                    readClob(rs.getClob(5)),
                    rs.getString(6)
            };

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(rs);
            close(ps);
            close(con);
        }
    }

    private String readClob(Clob c) throws SQLException {
        if (c == null) return null;
        return c.getSubString(1, (int)c.length());
    }

    private void close(AutoCloseable c) {
        try { if (c != null) c.close(); } catch (Exception ignored) {}
    }
}
