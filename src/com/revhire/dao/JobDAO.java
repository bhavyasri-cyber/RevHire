package com.revhire.dao;

import com.revhire.db.DBConnection;
import com.revhire.model.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    public long createJob(long companyId, String title, String desc, String skills,
                          int minExp, String education, String location,
                          Long salMin, Long salMax, String jobType, Date deadline) {

        String insertSql =
                "INSERT INTO RH_JOBS (COMPANY_ID, TITLE, JOB_DESC, SKILLS, MIN_EXP_YEARS, EDUCATION, LOCATION, SALARY_MIN, SALARY_MAX, JOB_TYPE, DEADLINE) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String idSql = "SELECT RH_JOBS_SEQ.CURRVAL FROM DUAL";

        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();

            ps = con.prepareStatement(insertSql);
            ps.setLong(1, companyId);
            ps.setString(2, title);
            ps.setString(3, desc);
            ps.setString(4, skills);
            ps.setInt(5, minExp);
            ps.setString(6, education);
            ps.setString(7, location);

            if (salMin == null) ps.setNull(8, Types.NUMERIC); else ps.setLong(8, salMin.longValue());
            if (salMax == null) ps.setNull(9, Types.NUMERIC); else ps.setLong(9, salMax.longValue());

            ps.setString(10, jobType);

            if (deadline == null) ps.setNull(11, Types.DATE); else ps.setDate(11, deadline);

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

    public boolean setJobStatus(long jobId, long companyId, String status) {
        String sql = "UPDATE RH_JOBS SET STATUS=? WHERE JOB_ID=? AND COMPANY_ID=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setLong(2, jobId);
            ps.setLong(3, companyId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    public boolean deleteJob(long jobId, long companyId) {
        String sql = "DELETE FROM RH_JOBS WHERE JOB_ID=? AND COMPANY_ID=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, jobId);
            ps.setLong(2, companyId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(ps);
            close(con);
        }
    }

    public List<Job> searchJobs(String title, String location, Integer exp,
                                String companyName, Long minSalary, String jobType) {

        String sql =
                "SELECT j.JOB_ID, c.NAME, j.TITLE, j.LOCATION, j.SKILLS, j.MIN_EXP_YEARS, j.JOB_TYPE, j.SALARY_MIN, j.SALARY_MAX, j.DEADLINE " +
                "FROM RH_JOBS j JOIN RH_COMPANIES c ON c.COMPANY_ID=j.COMPANY_ID " +
                "WHERE j.STATUS='OPEN' " +
                "AND (? IS NULL OR LOWER(j.TITLE) LIKE LOWER(?)) " +
                "AND (? IS NULL OR LOWER(j.LOCATION) LIKE LOWER(?)) " +
                "AND (? IS NULL OR j.MIN_EXP_YEARS <= ?) " +
                "AND (? IS NULL OR LOWER(c.NAME) LIKE LOWER(?)) " +
                "AND (? IS NULL OR j.SALARY_MIN >= ?) " +
                "AND (? IS NULL OR LOWER(j.JOB_TYPE) = LOWER(?)) " +
                "ORDER BY j.JOB_ID DESC";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<Job> out = new ArrayList<Job>();

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);

            setLike(ps, 1, 2, title);
            setLike(ps, 3, 4, location);

            if (exp == null) { ps.setNull(5, Types.NUMERIC); ps.setNull(6, Types.NUMERIC); }
            else { ps.setInt(5, exp.intValue()); ps.setInt(6, exp.intValue()); }

            setLike(ps, 7, 8, companyName);

            if (minSalary == null) { ps.setNull(9, Types.NUMERIC); ps.setNull(10, Types.NUMERIC); }
            else { ps.setLong(9, minSalary.longValue()); ps.setLong(10, minSalary.longValue()); }

            if (jobType == null || jobType.trim().length() == 0) { ps.setNull(11, Types.VARCHAR); ps.setNull(12, Types.VARCHAR); }
            else { ps.setString(11, jobType); ps.setString(12, jobType); }

            rs = ps.executeQuery();

            while (rs.next()) {
                Job j = new Job();
                j.setJobId(rs.getLong(1));
                j.setCompanyName(rs.getString(2));
                j.setTitle(rs.getString(3));
                j.setLocation(rs.getString(4));
                j.setSkills(rs.getString(5));
                j.setMinExp(rs.getInt(6));
                j.setJobType(rs.getString(7));

                long mn = rs.getLong(8);
                j.setSalaryMin(rs.wasNull() ? null : new Long(mn));

                long mx = rs.getLong(9);
                j.setSalaryMax(rs.wasNull() ? null : new Long(mx));

                j.setDeadline(rs.getDate(10));

                out.add(j);
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

    private void setLike(PreparedStatement ps, int nullPos, int likePos, String v) throws SQLException {
        if (v == null || v.trim().length() == 0) {
            ps.setNull(nullPos, Types.VARCHAR);
            ps.setNull(likePos, Types.VARCHAR);
        } else {
            ps.setString(nullPos, v);
            ps.setString(likePos, "%" + v + "%");
        }
    }

    private void close(AutoCloseable c) {
        try { if (c != null) c.close(); } catch (Exception ignored) {}
    }
}
