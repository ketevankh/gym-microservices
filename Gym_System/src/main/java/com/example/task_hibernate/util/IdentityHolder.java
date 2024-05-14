package com.example.task_hibernate.util;

public class IdentityHolder {
    private static ThreadLocal<String> requestIdentities = new ThreadLocal<>();

    public static String getIdentity() {
        return requestIdentities.get();
    }

    public static void setIdentity(String identity) {
        requestIdentities.set(identity);
    }

    public static void clearIdentity() {
        requestIdentities.remove();
    }
}
