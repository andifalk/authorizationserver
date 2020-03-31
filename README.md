[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
![Java CI](https://github.com/andifalk/authorizationserver/workflows/Java%20CI/badge.svg)

# Authorization Server

An OAuth 2.0 & OpenID Connect (OIDC) compliant authorization server just for demo purposes to be used as part of OAuth2/OIDC workshops.

## Targets

This authorization server should

* be easy to setup and start
* support latest specs and drafts for OAuth 2.x and OpenID Connect 1.0
* available as docker container / [testcontainers](https://www.testcontainers.org/)

__IMPORTANT:__ This project initially is intended to be used for demos and as part of trainings/workshops. It is __NOT__ ready for production use!!

## Features

* [RFC 6749: OAuth 2.0](https://www.rfc-editor.org/rfc/rfc6749.html) compliant (Implemented)
* [OpenID Connect 1.0](https://openid.net/specs/openid-connect-core-1_0.html) compliant (Implemented)
* OAuth 2.0 Grant Flows:
  * [Authorization Code Grant Flow](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) (+ [PKCE](https://tools.ietf.org/html/rfc7636)) (Implemented)
  * [Client Credentials Flow](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.4) (Implemented)
  * [Resource Owner Password Credentials Grant Flow](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.3) (Not Supported)
* [OAuth 2.0 / OIDC Bearer Tokens](https://www.rfc-editor.org/rfc/rfc6750.html) (Implemented)
  * Signed [Json Web Tokens (using RSA PKI)](https://tools.ietf.org/html/rfc7519) (Implemented)
  * Opaque Tokens + [OAuth 2.0 Token Introspection](https://tools.ietf.org/html/rfc7662) (Implemented)
* [OpenID Connect Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html) (Implemented)
* [RFC 8693: OAuth 2.0 Token Exchange](https://www.rfc-editor.org/rfc/rfc8693.html) (Planned)
* [RFC 8707: OAuth 2.0 Resource Indicators](https://www.rfc-editor.org/rfc/rfc8707.html) (Planned)
* [RFC 8705: OAuth 2.0 Mutual-TLS Client Authentication and Certificate-Bound Access Tokens](https://www.rfc-editor.org/rfc/rfc8705.html) (Planned)
* [OAuth 2.0 Demonstration of Proof-of-Possession at the Application Layer (DPoP)](https://tools.ietf.org/html/draft-fett-oauth-dpop) (Planned)
* Simple User Access Management (API & Web UI) (Partly implemented)
* Management of OAuth2/OIDC Clients (API & Web UI) (Partly implemented)

## Setup and Run the Authorization Server

To run the server you need at least a Java 11 JDK or higher (All LTS versions, i.e. 11 and 14 are tested).

To run the server just perform a ```gradlew bootrun``` or 
run the Spring Boot starter class _com.example.authorizationserver.AuthorizationServerApplication_ via your Java IDE.

It is also planned to provide the server as pre-packaged docker container image at a later project stage.

## User Management

It is planned to provide an integrated user management system via Web UI and Rest API.
Currently the Web UI only supports read-only access at [localhost:8080/auth/admin](http://localhost:8080/auth/admin).
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

| Client-Id           | Client-Secret | Confidential | Grants                              | Token-Format | Redirect Uris |
| --------------------| --------------| ------------ | ----------------------------------- |--------------|---------------|
| confidential-jwt    | demo          | yes          | Authz Code (+/- PKCE), Client Creds | JWT          | http://localhost:9090/demo-client/login/oauth2/code/demo |
| public-jwt          | --            | no           | Authz Code + PKCE                   | JWT          | http://localhost:9090/demo-client/login/oauth2/code/demo |
| confidential-opaque | demo          | yes          | Authz Code (+/- PKCE), Client Creds | Opaque       | http://localhost:9090/demo-client/login/oauth2/code/demo |
| public-opaque       | --            | no           | Authz Code + PKCE                   | Opaque       | http://localhost:9090/demo-client/login/oauth2/code/demo |


## Feedback

Any feedback on this project is highly appreciated.

Just send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
