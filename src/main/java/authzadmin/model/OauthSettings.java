package authzadmin.model;

import authzadmin.WebApplication;
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

  private boolean authorizationCodeAllowed;
  private boolean refreshTokenAllowed;
  private boolean implicitGrantAllowed;
  private boolean clientCredentialsAllowed;

  @Valid
  private List<Scope> scopes = new ArrayList<>();
  private boolean newClient = true;

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
    this.authorizationCodeAllowed = clientDetails.getAuthorizedGrantTypes().contains(WebApplication.AUTHORIZATION_CODE);
    this.refreshTokenAllowed = clientDetails.getAuthorizedGrantTypes().contains(WebApplication.REFRESH_TOKEN);
    this.implicitGrantAllowed = clientDetails.getAuthorizedGrantTypes().contains(WebApplication.IMPLICIT);
    this.clientCredentialsAllowed = clientDetails.getAuthorizedGrantTypes().contains(WebApplication.CLIENT_CREDENTIALS);
    newClient = false;
  }

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

  public List<Scope> getScopes() {
    return scopes;
  }

  public void setScopes(List<Scope> scopes) {
    this.scopes = scopes;
  }

  public boolean isNewClient() {
    return this.newClient;
  }

  public void setNewClient(boolean newClient) {
    this.newClient = newClient;
  }

  public boolean isAuthorizationCodeAllowed() {
    return authorizationCodeAllowed;
  }

  public void setAuthorizationCodeAllowed(boolean authorizationCodeAllowed) {
    this.authorizationCodeAllowed = authorizationCodeAllowed;
  }

  public boolean isRefreshTokenAllowed() {
    return refreshTokenAllowed;
  }

  public void setRefreshTokenAllowed(boolean refreshTokenAllowed) {
    this.refreshTokenAllowed = refreshTokenAllowed;
  }

  public boolean isImplicitGrantAllowed() {
    return implicitGrantAllowed;
  }

  public void setImplicitGrantAllowed(boolean implicitGrantAllowed) {
    this.implicitGrantAllowed = implicitGrantAllowed;
  }

  public String grantTypes() {
    List<String> grantTypes = new ArrayList<>();
    if (authorizationCodeAllowed) {
      grantTypes.add(WebApplication.AUTHORIZATION_CODE);
    }
    if (refreshTokenAllowed) {
      grantTypes.add(WebApplication.REFRESH_TOKEN);
    }
    if (implicitGrantAllowed) {
      grantTypes.add(WebApplication.IMPLICIT);
    }
    if (clientCredentialsAllowed) {
      grantTypes.add(WebApplication.CLIENT_CREDENTIALS);
    }
    return String.join(",", grantTypes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OauthSettings that = (OauthSettings) o;
    return Objects.equals(consumerKey, that.consumerKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consumerKey);
  }

  @Override
  public String toString() {
    return "OauthSettings{" +
      "consumerKey='" + consumerKey + '\'' +
      ", callbackUrl='" + callbackUrls + '\'' +
      '}';
  }
}
