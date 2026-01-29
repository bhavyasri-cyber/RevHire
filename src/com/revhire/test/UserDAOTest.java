package com.revhire.test;

import com.revhire.dao.UserDAO;
import com.revhire.db.DBConnection;
import com.revhire.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

public class UserDAOTest {

    private UserDAO userDAO;
    private String email;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
        email = "test_" + System.currentTimeMillis() + "@mail.com";
    }

    @After
    public void cleanup() {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("DELETE FROM RH_USERS WHERE EMAIL=?");
            ps.setString(1, email);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    @Test
    public void login_shouldReturnUser_whenCredentialsCorrect() {

        userDAO.register(
                "JUnit User",
                email,
                "hash123",
                "JOBSEEKER",
                "q",
                "a"
        );

        User u = userDAO.login(email, "hash123");

        assertNotNull(u);
        assertEquals(email, u.getEmail());
        assertEquals("JOBSEEKER", u.getRole());
    }
}
