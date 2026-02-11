package com.revhire.exceptionhandler;

import java.sql.SQLException;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static void handle(Exception ex) {
        Throwable root = rootCause(ex);

        if (root instanceof SQLException) {
            handleSql((SQLException) root);
            return;
        }

        System.out.println("Something went wrong. Please try again.");
    }

    private static void handleSql(SQLException e) {
        int code = e.getErrorCode();
        String msg = e.getMessage() == null ? "" : e.getMessage();

        if (code == 2290) {
            System.out.println(
                "Invalid input value. Please enter valid status like: SHORTLISTED / REJECTED."
            );
            return;
        }

        if (code == 1) {
            System.out.println(
                "Duplicate data not allowed. (Example: Email already exists)"
            );
            return;
        }

        if (code == 2291) {
            System.out.println(
                "Invalid reference. (Example: JOB_ID or USER_ID not found)"
            );
            return;
        }

        if (code == 2292) {
            System.out.println(
                "Cannot delete this record because related data exists."
            );
            return;
        }

        if (msg.contains("ORA-12514") ||
            msg.contains("ORA-12541") ||
            msg.contains("ORA-12154")) {
            System.out.println(
                "Database connection problem. Check Oracle service/listener."
            );
            return;
        }

        System.out.println("Database error occurred. Please try again.");
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }
}
