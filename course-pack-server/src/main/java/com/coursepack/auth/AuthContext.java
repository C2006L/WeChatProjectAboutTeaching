package com.coursepack.auth;

/**
 * 当前登录人上下文（ThreadLocal，AuthInterceptor 写入，请求结束清理）
 */
public final class AuthContext {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static Long userId() {
        CurrentUser u = HOLDER.get();
        return u == null ? null : u.getId();
    }

    public static String role() {
        CurrentUser u = HOLDER.get();
        return u == null ? null : u.getRole();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /** 当前登录人 */
    public static class CurrentUser {
        private final Long id;
        private final String username;
        private final String name;
        private final String role;
        private final String status;
        private final Integer isSeed;
        private final String auditField;

        public CurrentUser(Long id, String username, String name, String role, String status,
                           Integer isSeed, String auditField) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
            this.status = status;
            this.isSeed = isSeed;
            this.auditField = auditField;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return status;
        }

        public Integer getIsSeed() {
            return isSeed;
        }

        public String getAuditField() {
            return auditField;
        }
    }
}