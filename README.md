[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
![Java CI](https://github.com/andifalk/authorizationserver/workflows/Java%20CI/badge.svg)

# Authorization Server

An authorization server just for demo purposes to be used during workshops.

## Targets

This authorization server should

* be easy to setup and start
* include latest specs and drafts for OAuth 2.x and OpenID Connect 1.0
* available as docker container / testcontainer

__IMPORTANT:__ This project is __NOT__ intended for production use!!

## Features

* OAuth 2.0 compliant
* OpenID Connect 1.0 compliant
* Grant Flows:
  * Authorization Code Grant Flow (+ PKCE)
  * Client Credentials Flow
  * Password Flow
* Tokens
  * Signed Json Web Tokens (using RSA PKI)
  * Opaque Tokens + Introspection
* OpenID Connect Discovery
* Token Exchange
* Resource Indicators
    

## Feedback

Any feedback on this project is highly appreciated.

Just send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt