package authzadmin;

import org.springframework.security.oauth2.provider.ClientDetails;

public class ClientDetailsWrapper {


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
}
