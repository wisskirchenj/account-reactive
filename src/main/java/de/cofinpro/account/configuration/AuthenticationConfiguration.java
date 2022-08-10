package de.cofinpro.account.configuration;

import java.util.Set;

/**
 * collection of configuration stuff, constants for the authentication endpoints
 * - e.g. breached passwords set and check-method.
 */
public class AuthenticationConfiguration {

    private AuthenticationConfiguration() {
        // no instances
    }

    public static final int BCRYPT_STRENGTH = 13;

    public static final int MIN_PASSWORD_LENGTH = 12;

    public static final String USER_EXISTS_ERRORMSG = "User exist!";

    public static final String PASSWORD_TOO_SHORT_ERRORMSG = "The password length must be at least "
            + MIN_PASSWORD_LENGTH + " chars!";

    public static final String PASSWORD_HACKED_ERRORMSG = "The password is in the hacker's database!";

    public static final String SAME_PASSWORD_ERRORMSG = "The passwords must be different!";

    public static final String PASSWORD_UPDATEMSG = "The password has been updated successfully";

    /**
     * checks a provided password against a collection of known breached passwords.
     * @param password to check
     * @return check result (true, if password is breached)
     */
    public static boolean passwordIsHacked(String password) {
        return breachedPasswords.contains(password);
    }

    private static final Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary",
            "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");
}
