package authzadmin;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.net.URLEncoder.encode;

public class ClientDetailsWrapper {

  private static final SimpleGrantedAuthority ROLE_TOKEN_CHECKER_AUTHORITY = new SimpleGrantedAuthority(WebApplication.ROLE_TOKEN_CHECKER);

  private final boolean mutable;
  private final ClientDetails clientDetails;

  public ClientDetailsWrapper(ClientDetails clientDetails, boolean mutable) {
    this.clientDetails = clientDetails;
    this.mutable = mutable;
  }

  public ClientDetails getClientDetails() {
    return clientDetails;
  }

  public boolean isMutable() {
    return mutable;
  }

  public boolean isAutoApprove() {
    return clientDetails.isAutoApprove(OauthClientDetails.AUTO_APPROVE_SCOPE);
  }

  public boolean isResourceServer() {
    return clientDetails.getAuthorities().contains(ROLE_TOKEN_CHECKER_AUTHORITY) ;
  }

  public boolean isClientCredentialsAllowed() {
    return clientDetails.getAuthorizedGrantTypes().contains(WebApplication.CLIENT_CREDENTIALS);
  }

  public boolean isImplicitGrantAllowed() {
    return clientDetails.getAuthorizedGrantTypes().contains(WebApplication.IMPLICIT);
  }

  public boolean isRefreshTokenAllowed() {
    return clientDetails.getAuthorizedGrantTypes().contains(WebApplication.REFRESH_TOKEN);
  }

  public boolean isAuthorizationCodeAllowed() {
    return clientDetails.getAuthorizedGrantTypes().contains(WebApplication.AUTHORIZATION_CODE);
  }

  public String getClientIdEncoded() throws UnsupportedEncodingException {
    return encode(getClientDetails().getClientId(), "UTF-8");
  }
}
