package com.frame.base.security;

public class RuntimeSecurityUtils {

    public static String getCurrentUsernameSafely() {
        try {
            // Check if Spring Security exists on the classpath
            Class<?> holderClass = Class.forName("org.springframework.security.core.context.SecurityContextHolder");
            Object context = holderClass.getMethod("getContext").invoke(null);
            Object auth = context.getClass().getMethod("getAuthentication").invoke(context);

            if (auth != null) {
                Object principal = auth.getClass().getMethod("getPrincipal").invoke(auth);
                return principal.toString();
            }
        } catch (ClassNotFoundException e) {
            // Spring Security is not present at runtime
            return "ANONYMOUS_NO_SECURITY_MODULE";
        } catch (Exception e) {
            return "ANONYMOUS";
        }
        return "ANONYMOUS";
    }
}
