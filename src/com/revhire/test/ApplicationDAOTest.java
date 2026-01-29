package com.revhire.test;
import com.revhire.dao.*;
import com.revhire.db.DBConnection;
import com.revhire.model.RegisterResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ApplicationDAOTest {

	UserDAO userDAO;
     CompanyDAO companyDAO;
     JobDAO jobDAO;
     ApplicationDAO appDAO;

    private long seekerId;
    private long jobId;
    private long companyId;

    private String seekerEmail;
    private String empEmail;


    @After
    public void cleanup() {

        Connection con = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        PreparedStatement ps4 = null;

        try {
            con = DBConnection.getConnection();

            ps1 = con.prepareStatement(
                    "DELETE FROM RH_APPLICATIONS WHERE USER_ID=?");
            ps1.setLong(1, seekerId);
            ps1.executeUpdate();

            ps2 = con.prepareStatement(
                    "DELETE FROM RH_JOBS WHERE JOB_ID=?");
            ps2.setLong(1, jobId);
            ps2.executeUpdate();

            ps3 = con.prepareStatement(
                    "DELETE FROM RH_COMPANIES WHERE COMPANY_ID=?");
            ps3.setLong(1, companyId);
            ps3.executeUpdate();

            ps4 = con.prepareStatement(
                    "DELETE FROM RH_USERS WHERE EMAIL IN (?,?)");
            ps4.setString(1, seekerEmail);
            ps4.setString(2, empEmail);
            ps4.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try { if (ps4 != null) ps4.close(); } catch (Exception ignored) {}
            try { if (ps3 != null) ps3.close(); } catch (Exception ignored) {}
            try { if (ps2 != null) ps2.close(); } catch (Exception ignored) {}
            try { if (ps1 != null) ps1.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

   

   
}
