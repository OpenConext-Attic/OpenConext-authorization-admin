package authzadmin;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

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
}
