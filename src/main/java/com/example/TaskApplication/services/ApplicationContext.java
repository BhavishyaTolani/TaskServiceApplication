package com.example.TaskApplication.services;

public class ApplicationContext {
    private static long userId;
    private static String email;

    public static void setCurrentUser(long userId) {
        ApplicationContext.userId = userId;
    }

    public static void setCurrentUserEmail(String userId) {
        ApplicationContext.email = email;
    }

    public static long getCurrentUser() {
        return ApplicationContext.userId;
    }

    public static String getCurrentUserEmail() {
        return ApplicationContext.email;
    }
}
