package com.example.authorizationserver.oauth.pkce;

public interface ProofKeyForCodeExchangeVerifier {

  String CHALLENGE_METHOD_S_256 = "S256";
  String CHALLENGE_METHOD_PLAIN = "plain";

  /**
   * @param challengeMethod OPTIONAL, defaults to "plain" if not present in the request. Code
   *     verifier transformation method is "S256" or "plain".
   * @param codeVerifier high-entropy cryptographic random STRING using the unreserved characters
   *     [A-Z] / [a-z] / [0-9] / "-" / "." / "_" / "~" from Section 2.3 of [RFC3986], with a minimum
   *     length of 43 characters and a maximum length of 128 characters.
   * @param codeChallenge The client creates a code challenge derived from the code verifier by
   *     using one of the following transformations on the code verifier:
   *     <p>plain code_challenge = code_verifier
   *     <p>S256 code_challenge = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))
   *
   * @throws CodeChallengeError if verification of code challenge with code verifier fails
   */
  void verifyCodeChallenge(String challengeMethod, String codeVerifier, String codeChallenge)
      throws CodeChallengeError;
}
