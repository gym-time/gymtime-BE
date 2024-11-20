package gymtime.gymtime_core.auth.user;

public enum UserType {
    CUSTOMER,
    BUSINESS;

    public static UserType fromAuthority(String authority) {
        return UserType.valueOf(authority.replace("ROLE_", ""));
    }
}