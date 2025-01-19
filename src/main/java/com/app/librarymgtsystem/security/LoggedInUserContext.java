package com.app.librarymgtsystem.security;

import org.springframework.stereotype.Component;

@Component

public class LoggedInUserContext {

        private static final ThreadLocal<String> sessionEmail = new ThreadLocal<>();
        private static final ThreadLocal<String> sessionToken = new ThreadLocal<>();

        public static void setSessionEmail(String email) {
            sessionEmail.set(email);
        }
        public static String getSessionEmail() {
            return sessionEmail.get();
        }
        public static void setSessionToken(String token) {
            sessionToken.set(token);
        }
        public static String getSessionToken() {
            return sessionToken.get();
        }
        public static void clear() {
            sessionEmail.remove();
            sessionToken.remove();
        }

}
