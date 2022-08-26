package de.cofinpro.account.admin;

/**
 * immutable response object to inform on a succssful user deletion - for DELETE /api/admin/user endpoint
 */
public record UserDeletedResponse(String user, String status) {
}
