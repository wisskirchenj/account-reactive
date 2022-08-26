# IDEA EDU Course ...

Implemented in the Java <b>Backend Developer</b> Track of hyperskill.org's JetBrain Academy.<br>
https://hyperskill.org/projects/217

Project goal (or rather my interpretation of it) is to implement a reactive Spring webflux application with functional endpoints
using Basic Auth Spring security eventually on HTTPS-protocol.

Purpose of doing this project, is further practising Spring boot and security applications - experimenting with the reactive 
Spring webflux using functional Spring 5 routing/handler techniques. Also, with this project the Java Backend Developer track
should be completed, as only 1 or 2 applied topics are still missing to fulfill the 95% hurdle :-)

## Program description

The application will represent a simple account service with user authentication, different authorization roles
and some business logic endpoints accessing employee payroll data in a database.

Currently implemented endpoints:

> <b>POST /api/auth/signup (unauthenticated)</b>. -> receives a signup JSON to register a user. Returns a response without the password
but with an id, which the user is stored under in the database. If the requested user exists already, a 400 informative error
is returned.
The request JSON looks as:<pre>
{
    "name": "Toni",
    "lastname": "Seeler",
    "email": "toni.seeler@xyz.de",
    "password": "123456"
}</pre>

> <b>POST /api/auth/changepass (authenticated)</b>. -> receives a Json with newPassword, which is validated on length and checked
against a collection of breached passwords (This breach check is also done on signup and for every authenticated endpoint!)
If the password further differs from the previous one, an informative success Json is returned.

> <b>POST /api/acct/payments (authorized - Role ACCOUNTANT)</b>. -> receives a Json array of SalaryRecords as :<pre>
{
    "employee": "p.s@acme.COM",
    "period": "01-2022",
    "salary": 515050
}</pre>
Salary in Cents for a given period. The list may contain records for different employees, but no duplicates
regarding employee and period. Further, the employees must all be registered and no previous sales record
must exist in the database for an employee - month combi.

> <b>PUT /api/acct/payments (authorized - Role ACCOUNTANT)</b>. -> receives one Json object as above via POST, but the employee
period must in this case be existent in the database to get updated.

> <b>GET /api/empl/payment (authorized - Roles USER & ACCOUNTANT - with request query parameter "period")</b> -> gives an authenticated user
access to his payroll data - either all when leaving out the period-parameter - or for the parameterized month.

> <b>GET /api/admin/user (authorized - Role ADMINISTRATOR)</b> -> list all user data records with id, roles
and name, email information.

> <b>DELETE /api/admin/user (authorized - Role ADMINISTRATOR - with path parameter "email")</b> -> delete the specified
user together with all her roles and salary information. Admin cannot delete himself from the system.

> <b>PUT /api/admin/user/role (authorized - Role ADMINISTRATOR)</b> -> toggles (Grant/Revoke) a role to
a specified user - consumes Json RoleToggleRequest:<pre>
{
    "user": "p.s@acme.COM",
    "role": "ACCOUNTANT",   (or "USER"; "ADMINISTRATOR" is not allowed to grant or remove) 
    "operation": "Grant"    (or "REMOVE")
}</pre> It is also not allowed to assign "USER" or "ACCOUNTANT" to the "ADMINISTRATOR", nor is it allowed
to delete the last role remaining from a user.


> <b>PUT /api/admin/user/access (authorized - Role ADMINISTRATOR)</b> -> locks or unlocks a user - 
consumes Json LockUserToggleRequest:<pre>
{
    "user": "p.s@acme.COM",
    "operation": "lock"    (or "unlock")
}</pre> It is not allowed to lock the "ADMINISTRATOR".


> <b>GET /api/security/events (authorized - Role AUDITOR)</b> -> list all recorded security events for failed
or unathorized logins, all admin activities, signups, change passwords and brute force locking after 5 failed attempts.
and name, email information.

## Project was completed on 26.08.22.

## Repository Contents

Sources for all project tasks (7 stages) with tests and configurations.

## Progress

26.07.22 Project started. IDEA-setup and first repo.

26.07.22 Stage 1 completed - signup (functional) endpoint offered reactively with (JPA-)validated Request-DTO.

01.08.22 Stage 2 completed - setup of WebFluxSecurity, R2DBC-data base connected and initialized via scripts using
Spring properties, signup data are saved, authenticated endpoints (all but signup).

06.08.22 Stage 3 completed - custom authentication manager to include breached passwords check in login authentication,
change password functionality, Mono-zipWith and Tuple2 used..

10.08.22 Stage 4 completed - domain logic endpoints /api/acct/payments and /api/empl/payment added / adapted to
full functionality. Flux publishers used and many interisting reactor operators.

16.08.22 Stage 5 completed - authorization role concept and admin endpoints added. Used .then() Publisher-operator
for Mono<Void> returning delete-repo methods.

24.08.22 Stage 6 completed - persistent security logging implemented, save login failures and programtically lock a
user after 5 failed login attempts, reset failedLogins on successful login. Implement admin lock/unlock user endpoint.
new AUDITOR role authorizing new endpoint /api/security/events.

26.08.22 Final Stage 7 completed - switch to https-communication (i.e. TLS) with self-generated certificate via keytool

