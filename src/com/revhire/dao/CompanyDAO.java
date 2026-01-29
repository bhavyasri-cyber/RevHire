package com.revhire.dao;

import com.revhire.db.DBConnection;

import java.sql.*;

public class CompanyDAO {

    public Long getCompanyIdByUser(long userId) {
        String sql = "SELECT COMPANY_ID FROM RH_COMPANIES WHERE USER_ID=?";

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

    public long upsertCompany(long userId, String name, String industry, String sizeRange,
                             String desc, String website, String location) {

        Long companyId = getCompanyIdByUser(userId);
        if (companyId == null) return insert(userId, name, industry, sizeRange, desc, website, location);
        update(companyId.longValue(), name, industry, sizeRange, desc, website, location);
        return companyId.longValue();
    }

    private long insert(long userId, String name, String industry, String sizeRange,
                        String desc, String website, String location) {

        String insertSql =
                "INSERT INTO RH_COMPANIES (USER_ID, NAME, INDUSTRY, SIZE_RANGE, DESCRIPTION, WEBSITE, LOCATION) " +
                "VALUES (?,?,?,?,?,?,?)";
        String idSql = "SELECT RH_COMPANIES_SEQ.CURRVAL FROM DUAL";

        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();

            ps = con.prepareStatement(insertSql);
            ps.setLong(1, userId);
            ps.setString(2, name);
            ps.setString(3, industry);
            ps.setString(4, sizeRange);
            ps.setString(5, desc);
            ps.setString(6, website);
            ps.setString(7, location);
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

    private void update(long companyId, String name, String industry, String sizeRange,
                        String desc, String website, String location) {

        String sql =
                "UPDATE RH_COMPANIES SET NAME=?, INDUSTRY=?, SIZE_RANGE=?, DESCRIPTION=?, WEBSITE=?, LOCATION=? " +
                "WHERE COMPANY_ID=?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, industry);
            ps.setString(3, sizeRange);
            ps.setString(4, desc);
            ps.setString(5, website);
            ps.setString(6, location);
            ps.setLong(7, companyId);
            ps.executeUpdate();

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
