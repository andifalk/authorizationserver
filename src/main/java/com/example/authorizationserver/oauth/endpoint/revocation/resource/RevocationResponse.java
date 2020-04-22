package com.example.authorizationserver.oauth.endpoint.revocation.resource;

/**
 * Revocation response as defined in https://www.rfc-editor.org/rfc/rfc7009.html.
 */
public class RevocationResponse {

  private String status;
  private String error;

  public RevocationResponse(String status, String error) {
    this.status = status;
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "RevocationResponse{"
        + "status='"
        + status
        + '\''
        + ", error='"
        + error
        + '\''
        + '}';
  }
}
