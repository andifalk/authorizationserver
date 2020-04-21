package com.example.authorizationserver.oauth.pkce;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ProofKeyForCodeExchangeVerifierStandardImpl implements ProofKeyForCodeExchangeVerifier {

  private static final Logger LOG = LoggerFactory.getLogger(ProofKeyForCodeExchangeVerifierStandardImpl.class);

  @Override
  public void verifyCodeChallenge(String challengeMethod, String codeVerifier, String codeChallenge) throws CodeChallengeError {

    LOG.debug("Verifying PKCE code challenge with code verifier using method [{}]", challengeMethod);

    if (StringUtils.isBlank(codeVerifier)) {
      LOG.warn("Code verifier must not be empty");
      throw new CodeChallengeError();
    }

    if (codeVerifier.length() < 43 || codeVerifier.length() > 128) {
      LOG.warn("Code verifier must have a length between 43 and 128 characters");
      throw new CodeChallengeError();
    }

    if (CHALLENGE_METHOD_S_256.equalsIgnoreCase(challengeMethod)) {
      // Rehash the code verifier
      try {
        String rehashedChallenge = rehashCodeVerifier(codeVerifier);
        if (!MessageDigest.isEqual(
                codeChallenge.getBytes(UTF_8), rehashedChallenge.getBytes(UTF_8))) {
          throw new CodeChallengeError();
        }
      } catch (NoSuchAlgorithmException e) {
        throw new CodeChallengeError();
      }
    } else if (challengeMethod == null || challengeMethod.isBlank() || CHALLENGE_METHOD_PLAIN.equalsIgnoreCase(challengeMethod)) {
      if (!codeChallenge.equals(codeVerifier)) {
        throw new CodeChallengeError();
      }
    } else {
      LOG.warn("Invalid Code Challenge [{}]", codeChallenge);
      throw new CodeChallengeError();
    }
  }

  private String rehashCodeVerifier(String codeVerifier) throws NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA-256");
    final byte[] hashedBytes = digest.digest(codeVerifier.getBytes(UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
  }
}
