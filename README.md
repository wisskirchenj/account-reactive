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

> POST /api/auth/singup (unauthenticated). -> receives a signup JSON to register a user. Returns a response without the password
but with an id, which the user is stored under in the database. If the requested user exists already, a 400 informative error
is returned.
The request JSON looks as:<pre>
{
    "name": "Toni",
    "lastname": "Seeler",
    "email": "toni.seeler@xyz.de",
    "password": "123456"
}</pre>

> POST /api/auth/changepass (authenticated). -> currently just checks authentication and returns 200 or 401 empty.
Will be implemented later with change password functionality.

> GET /api/empl/payment (authenticated). -> currently returns the login data (same as after suceesful signup) of the
authenticated user's principal.

[//]: # (Project was completed on xx.0d.22.)

## Repository Contents

Sources for all project tasks (7 stages) with tests and configurations.

## Progress

26.07.22 Project started. IDEA-setup and first repo.

26.07.22 Stage 1 completed - signup (functional) endpoint offered reactively with (JPA-)validated Request-DTO.

01.08.22 Stage 2 completed - setup of WebFluxSecurity, R2DBC-data base connected and initialized via scripts using
Spring properties, signup data are saved, authenticated endpoints (all but signup).

