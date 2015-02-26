package authzadmin;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

public class OauthClientDetails extends BaseClientDetails {

  public OauthClientDetails(OauthSettings oauthSettings) {
    super(
      oauthSettings.getConsumerKey(),
      null,
      StringUtils.collectionToCommaDelimitedString(oauthSettings.getScopes()),
      null,
      null,
      oauthSettings.getCallbackUrl()
    );
    setClientSecret(oauthSettings.getSecret());
  }
}
