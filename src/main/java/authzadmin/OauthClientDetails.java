package authzadmin;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;

public class OauthClientDetails extends BaseClientDetails {

  public OauthClientDetails(OauthSettings oauthSettings) {
    super(oauthSettings.getConsumerKey(), null, null, null, null, oauthSettings.getCallbackUrl());
    setClientSecret(oauthSettings.getSecret());
  }
}
