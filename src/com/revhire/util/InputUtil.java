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
}
