package de.cofinpro.account.configuration;

public class AccountConfiguration {

    private AccountConfiguration() {
        // no instances
    }

    public static final String NO_SUCH_EMPLOYEE_ERRORMSG = "No such employee registered!";

    public static final String NO_SUCH_SALES_RECORD_ERRORMSG = "No such record found for this employee and period!";

    public static final String RECORD_ALREADY_EXISTS_ERRORMSG = "A record already exists for this employee and period! Use PUT!";

    public static final String DUPLICATE_RECORDS_ERRORMSG = "Duplicate record for same employee and period provided!";

    public static final String ADDED_SUCCESSFULLY = "Added successfully!";

    public static final String UPDATED_SUCCESSFULLY = "Updated successfully!";

    public static final String RECORDMSG_START = "Record %d: %s";
}
