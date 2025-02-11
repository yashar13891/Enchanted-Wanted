package org.yashar.enchantedWanted;

public enum Permission {
    ADMIN("ewanted.admin"),
    PLAYER("ewanted.player");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
