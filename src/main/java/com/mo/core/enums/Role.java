package com.mo.core.enums;

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ROOT("ROLE_ROOT"),
    SENDER("ROLE_SENDER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.getRole().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rôle inconnu: " + role);
    }
}
