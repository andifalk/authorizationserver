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

## Features (Planned)

* [RFC 6749: OAuth 2.0](https://www.rfc-editor.org/rfc/rfc6749.html) compliant
* [OpenID Connect 1.0](https://openid.net/specs/openid-connect-core-1_0.html) compliant
* OAuth 2.0 Grant Flows:
  * [Authorization Code Grant Flow](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) (+ [PKCE](https://tools.ietf.org/html/rfc7636))
  * [Client Credentials Flow](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.4)
  * [Resource Owner Password Credentials Grant Flow](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.3)
* [OAuth 2.0 / OIDC Bearer Tokens](https://www.rfc-editor.org/rfc/rfc6750.html)
  * Signed [Json Web Tokens (using RSA PKI)](https://tools.ietf.org/html/rfc7519)
  * Opaque Tokens + [OAuth 2.0 Token Introspection](https://tools.ietf.org/html/rfc7662)
* [OpenID Connect Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html)
* [RFC 8693: OAuth 2.0 Token Exchange](https://www.rfc-editor.org/rfc/rfc8693.html)
* [RFC 8707: OAuth 2.0 Resource Indicators](https://www.rfc-editor.org/rfc/rfc8707.html)
* [RFC 8705: OAuth 2.0 Mutual-TLS Client Authentication and Certificate-Bound Access Tokens](https://www.rfc-editor.org/rfc/rfc8705.html)
* [OAuth 2.0 Demonstration of Proof-of-Possession at the Application Layer (DPoP)](https://tools.ietf.org/html/draft-fett-oauth-dpop)
* Simple User Access Management (API & Web UI)
* Management of OAuth2/OIDC Clients (API & Web UI)

## Feedback

Any feedback on this project is highly appreciated.

Just send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
