package de.cofinpro.account.configuration;

import java.util.Set;

public class AuthenticationConfiguration {

    private AuthenticationConfiguration() {
        // no instances
    }

    public static final int BCRYPT_STRENGTH = 13;

    public static final int MIN_PASSWORD_LENGTH = 12;

    public static final String USER_EXISTS_ERRORMSG = "User exist!";

    public static final String PASSWORT_TOO_SHORT_ERRORMSG = "The password length must be at least "
            + MIN_PASSWORD_LENGTH + " chars!";
    public static final String PASSWORT_HACKED_ERRORMSG = "The password is in the hacker's database!";

    private static final Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary",
            "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public static boolean passwordIsHacked(String password) {
        return breachedPasswords.contains(password);
    }
}
