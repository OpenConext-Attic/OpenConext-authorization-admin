package authzadmin;

import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

import static authzadmin.WebApplication.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class OauthSettings {

  @NotNull
  @Size(min = 1)
  private String secret = UUID.randomUUID().toString();

  @NotNull
  @Size(min = 1)
  private String consumerKey;

  @URL
  private String callbackUrl;

  private boolean autoApprove;

  private boolean resourceServer;

  private boolean clientCredentialsAllowed;

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

  public boolean isResourceServer() {
    return resourceServer;
  }

  public void setResourceServer(boolean resourceServer) {
    this.resourceServer = resourceServer;
  }

  public boolean isClientCredentialsAllowed() {
    return clientCredentialsAllowed;
  }

  public void setClientCredentialsAllowed(boolean clientCredentialsAllowed) {
    this.clientCredentialsAllowed = clientCredentialsAllowed;
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

  public OauthSettings(ClientDetails clientDetails) {
    this.secret = clientDetails.getClientSecret();
    this.consumerKey = clientDetails.getClientId();
    Set<String> redirectUris = clientDetails.getRegisteredRedirectUri();
    this.callbackUrl = isEmpty(redirectUris) ? null : redirectUris.iterator().next();
    Set<String> scopes = clientDetails.getScope();
    this.scopes = isEmpty(scopes) ? null : scopes.stream().map(Scope::new).collect(toList());
    this.autoApprove = clientDetails.isAutoApprove("auto");
    Collection<GrantedAuthority> authorities = clientDetails.getAuthorities();
    this.resourceServer = isEmpty(authorities) ? false : authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_TOKEN_CHECKER));
    this.clientCredentialsAllowed = clientDetails.getAuthorizedGrantTypes().stream().anyMatch(grant -> grant.equals(CLIENT_CREDENTIALS));
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

  public boolean isNewClient() {
    return this.consumerKey == null;
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
