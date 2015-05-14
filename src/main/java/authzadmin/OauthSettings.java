package authzadmin;

import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OauthSettings {

  @NotNull
  @Size(min = 1)
  private String secret = UUID.randomUUID().toString();

  @NotNull
  @Size(min = 1)
  private String consumerKey;

  @NotNull
  @Size(min = 1)
  @URL
  private String callbackUrl;

  private boolean autoApprove;

  @Valid
  private List<Scope> scopes = new ArrayList<>();

  public String getConsumerKey() {
    return consumerKey;
  }

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public String getCallbackUrl() {
    return callbackUrl;
  }

  public void setCallbackUrl(String callbackUrl) {
    this.callbackUrl = callbackUrl;
  }

  public String getSecret() {

    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public void setAutoApprove(boolean autoApprove) {
    this.autoApprove = autoApprove;
  }

  public boolean isAutoApprove() {
    return autoApprove;
  }

  /**
   * Needed for Spring form binding.
   */
  public OauthSettings() {
  }

  public OauthSettings(String secret, String consumerKey, String callbackUrl) {
    this.secret = secret;
    this.consumerKey = consumerKey;
    this.callbackUrl = callbackUrl;
  }

  public List<Scope> getScopes() {
    return scopes;
  }

  public void setScopes(List<Scope> scopes) {
    this.scopes = scopes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OauthSettings that = (OauthSettings) o;

    if (callbackUrl != null ? !callbackUrl.equals(that.callbackUrl) : that.callbackUrl != null) return false;
    if (consumerKey != null ? !consumerKey.equals(that.consumerKey) : that.consumerKey != null) return false;
    if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = secret != null ? secret.hashCode() : 0;
    result = 31 * result + (consumerKey != null ? consumerKey.hashCode() : 0);
    result = 31 * result + (callbackUrl != null ? callbackUrl.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "OauthSettings{" +
      "consumerKey='" + consumerKey + '\'' +
      ", callbackUrl='" + callbackUrl + '\'' +
      '}';
  }
}
