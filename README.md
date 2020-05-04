[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
![Java CI](https://github.com/andifalk/authorizationserver/workflows/Java%20CI/badge.svg)

# Authorization Server

An OAuth 2.0 & OpenID Connect (OIDC) compliant authorization server just for demo purposes to be used as part of OAuth2/OIDC workshops.

## Targets

This authorization server should...

* be available for free as open-source
* support efforts to learn OAuth2/OpenID Connect (self-study or as part of workshops)
* provide an easy way for setting up and run it (i.e. without consulting tons of documentation)
* support latest specs and drafts for OAuth 2.x and OpenID Connect
* be provided as docker container & support [testcontainers](https://www.testcontainers.org/)

__IMPORTANT:__  
The intention of this project is to be used for demos and as part of trainings/workshops.  
It is __NOT__ ready for production use!!

If you are looking for a production-grade identity access management solution please consult the 
list of [Certified OpenID provider servers and services](https://openid.net/developers/certified/) 
at the [OpenID Foundation](https://openid.net/).

## Features (Supported)

* [RFC 6749: OAuth 2.0 Authorization Framework](https://www.rfc-editor.org/rfc/rfc6749.html)
* [RFC 8252: OAuth 2.0 for Native Apps](https://www.rfc-editor.org/rfc/rfc8252.html)
* [OpenID Connect 1.0](https://openid.net/specs/openid-connect-core-1_0.html)
* OAuth 2.0 Grant Flows:
  * [Authorization Code Grant](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) (+ [PKCE](https://tools.ietf.org/html/rfc7636))
  * [Client Credentials Grant](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.4)
  * [Resource Owner Password Credentials Grant](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.3)
* [RFC 6750: OAuth 2.0 Bearer Token Usage](https://www.rfc-editor.org/rfc/rfc6750.html)
  * Signed [Json Web Tokens (using RSA PKI)](https://tools.ietf.org/html/rfc7519)
  * Opaque Tokens + [OAuth 2.0 Token Introspection](https://tools.ietf.org/html/rfc7662)
  * [OAuth 2.0 Token Revocation](https://www.rfc-editor.org/rfc/rfc7009.html)
* [OpenID Connect Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html)
* Simple User Access Management API
* Simple User Access Management Web UI (Read access only)
* Management of OAuth2/OIDC Clients API
* Management of OAuth2/OIDC Clients Web UI (Read access only)J2

## Features (NOT Supported)

* OAuth 2.0 Grant Flows:
  * [Implicit Grant](https://www.rfc-editor.org/rfc/rfc6749#section-4.2) (Not supported by intention, because of [OAuth 2.0 Security Best Current Practice](https://www.ietf.org/id/draft-ietf-oauth-security-topics-15.html))

## Features (Planned)

* [RFC 8628: OAuth 2.0 Device Authorization Grant](https://www.rfc-editor.org/rfc/rfc8628.html)
* [RFC 8693: OAuth 2.0 Token Exchange](https://www.rfc-editor.org/rfc/rfc8693.html)
* [RFC 8707: OAuth 2.0 Resource Indicators](https://www.rfc-editor.org/rfc/rfc8707.html)
* [RFC 8705: OAuth 2.0 Mutual-TLS Client Authentication and Certificate-Bound Access Tokens](https://www.rfc-editor.org/rfc/rfc8705.html)
* [OAuth 2.0 Demonstration of Proof-of-Possession at the Application Layer (DPoP)](https://datatracker.ietf.org/doc/draft-ietf-oauth-dpop/)
* [JSON Web Token (JWT) Profile for OAuth 2.0 Access Tokens](https://datatracker.ietf.org/doc/draft-ietf-oauth-access-token-jwt/)
* [The OAuth 2.0 Authorization Framework: JWT Secured Authorization Request (JAR)](https://datatracker.ietf.org/doc/draft-ietf-oauth-jwsreq/)
* [OAuth 2.0 Pushed Authorization Requests](https://datatracker.ietf.org/doc/draft-ietf-oauth-par/)
* [OAuth 2.0 Rich Authorization Requests](https://datatracker.ietf.org/doc/draft-ietf-oauth-rar/)
* [JWT Response for OAuth Token Introspection](https://datatracker.ietf.org/doc/draft-ietf-oauth-jwt-introspection-response/)
* [OAuth 2.0 Incremental Authorization](https://datatracker.ietf.org/doc/draft-ietf-oauth-incremental-authz/)
* [The OAuth 2.1 Authorization Framework](https://datatracker.ietf.org/doc/draft-parecki-oauth-v2-1/)


## Roadmap

* May 2020: [Release 1.0](https://github.com/andifalk/authorizationserver/milestone/1) - Mandatory OAuth 2.0 & OIDC grant flows, user and client management, H2 in-memory storage
* June/July 2020: [Release 1.1](https://github.com/andifalk/authorizationserver/milestone/2) - Support additional OAuth 2.0 RFCs/Drafts + support other databases for storage

## Setup and Run the Authorization Server

To run the server you need at least a Java 11 JDK or higher (versions 11 and 14 are currently tested).

To run the server just perform a ```gradlew bootrun``` or 
run the Spring Boot starter class _com.example.authorizationserver.AuthorizationServerApplication_ via your Java IDE.

It is also planned to provide the server as pre-packaged docker container image at a later project stage.

## User Management

It is planned to provide an integrated user management system via Web UI and Rest API.
Currently, the Web UI only supports read-only access at [localhost:8080/auth/admin](http://localhost:8080/auth/admin).
The Rest API also supports creating new users already.

The following predefined users are setup at startup time automatically:

| Username | Email                    | Password | Role            |
| ---------| ------------------------ | -------- | --------------- |
| bwayne   | bruce.wayne@example.com  | wayne    | LIBRARY_USER    |
| pparker  | peter.parker@example.com | parker   | LIBRARY_CURATOR |
| ckent    | clark.kent@example.com   | kent     | LIBRARY_ADMIN   |
| admin    | max.root@example.com     | admin    | ADMIN           |


## Client Management

It is planned to provide an integrated client management system via Web UI and Rest API.
Currently the Web UI only supports read-only access at [localhost:8080/auth/admin](http://localhost:8080/auth/admin).
The Rest API also supports creating new clients already.

The following predefined clients are setup at startup time automatically:

| Client-Id           | Client-Secret | Confidential | Grants                              | Token-Format | Redirect Uris | CORS |
| --------------------| --------------| ------------ | ----------------------------------- |--------------|---------------|------|
| confidential-jwt    | demo          | yes          | Authz Code (+/- PKCE), Client Creds | JWT          | http://localhost:9090/demo-client/login/oauth2/code/demo | * |
| public-jwt          | --            | no           | Authz Code + PKCE                   | JWT          | http://localhost:9090/demo-client/login/oauth2/code/demo | * |
| confidential-opaque | demo          | yes          | Authz Code (+/- PKCE), Client Creds | Opaque       | http://localhost:9090/demo-client/login/oauth2/code/demo | * |
| public-opaque       | --            | no           | Authz Code + PKCE                   | Opaque       | http://localhost:9090/demo-client/login/oauth2/code/demo | * |


## Feedback

If you have any feedback on this project this is highly appreciated.

Just send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
