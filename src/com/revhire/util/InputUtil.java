package com.revhire.util;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner sc = new Scanner(System.in);

    public static String s(String label) {
        System.out.print(label + ": ");
        return sc.nextLine().trim();
    }

    public static int i(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Enter valid number");
            }
        }
    }

    public static long l(String label) {
        while (true) {
            try {
                System.out.print(label + ": ");
                return Long.parseLong(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Enter valid number");
            }
        }
    }

    public static String email(String label) {
        while (true) {
            System.out.print(label + ": ");
            String email = sc.nextLine().trim();
            if (Validator.isEmail(email)) return email;
            System.out.println("Enter valid email (example: name@gmail.com)");
        }
    }

    public static String password(String label) {
        while (true) {
            System.out.print(label + ": ");
            String pass = sc.nextLine().trim();
            if (Validator.isPasswordOk(pass)) return pass;
            System.out.println("Password must be at least 6 characters");
        }
    }

    public static String statusShortlistReject(String label) {
        while (true) {
            System.out.print(label + ": ");
            String st = sc.nextLine().trim().toUpperCase();
            if (Validator.isStatusShortlistReject(st)) return st;
            System.out.println("Enter valid status: SHORTLISTED or REJECTED");
        }
    }

    public static String dateEmptyOk(String label) {
        while (true) {
            System.out.print(label + ": ");
            String d = sc.nextLine().trim();
            if (d.length() == 0) return "";
            if (Validator.isDateYYYYMMDD(d)) return d;
            System.out.println("Enter valid date yyyy-mm-dd (example: 2026-01-30) or leave empty");
        }
    }
}
