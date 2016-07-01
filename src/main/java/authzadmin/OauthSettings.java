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

  @Valid
  private List<RedirectURI> callbackUrls = new ArrayList<>();

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

  public List<RedirectURI> getCallbackUrls() {
    return callbackUrls;
  }

  public void setCallbackUrls(List<RedirectURI> callbackUrls) {
    this.callbackUrls = callbackUrls;
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
    this.callbackUrls = Arrays.asList(new RedirectURI(callbackUrl));
  }

  public OauthSettings(ClientDetails clientDetails) {
    this.secret = clientDetails.getClientSecret();
    this.consumerKey = clientDetails.getClientId();
    Set<String> registeredRedirectUri = clientDetails.getRegisteredRedirectUri();
    this.callbackUrls = isEmpty(registeredRedirectUri) ? null : registeredRedirectUri.stream().map(RedirectURI::new).collect(toList());
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

  public boolean isNewClient() {
    return this.consumerKey == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OauthSettings that = (OauthSettings) o;
    return autoApprove == that.autoApprove &&
      resourceServer == that.resourceServer &&
      clientCredentialsAllowed == that.clientCredentialsAllowed &&
      Objects.equals(secret, that.secret) &&
      Objects.equals(consumerKey, that.consumerKey) &&
      Objects.equals(callbackUrls, that.callbackUrls) &&
      Objects.equals(scopes, that.scopes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consumerKey, callbackUrls, scopes);
  }

  @Override
  public String toString() {
    return "OauthSettings{" +
      "consumerKey='" + consumerKey + '\'' +
      ", callbackUrl='" + callbackUrls + '\'' +
      '}';
  }
}
