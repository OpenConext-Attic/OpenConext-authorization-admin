package authzadmin;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class OauthClientDetails extends BaseClientDetails {

  /*
   * Spring logic for auto approving all scopes
   */
  public static final String AUTO_APPROVE_SCOPE = "true";

  public OauthClientDetails(OauthSettings oauthSettings) {
    super(
      oauthSettings.getConsumerKey(),
      null,
      CollectionUtils.isEmpty(oauthSettings.getScopes()) ? null : StringUtils.collectionToCommaDelimitedString(oauthSettings.getScopes().stream().map(Scope::getValue).collect(Collectors.toList())),
      "authorization_code,refresh_token,implicit",
      null,
      oauthSettings.getCallbackUrl()
    );
    setClientSecret(oauthSettings.getSecret());
    if (oauthSettings.isAutoApprove()) {
      setAutoApproveScopes(Arrays.asList(AUTO_APPROVE_SCOPE));
    }
  }
}
