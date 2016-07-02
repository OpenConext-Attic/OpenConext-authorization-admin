package authzadmin;

import authzadmin.model.OauthSettings;
import authzadmin.model.RedirectURI;
import authzadmin.model.Scope;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.stream.Collectors;

import static authzadmin.WebApplication.ROLE_TOKEN_CHECKER;

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
      oauthSettings.grantTypes(),
      null,
      CollectionUtils.isEmpty(oauthSettings.getCallbackUrls()) ? null : StringUtils.collectionToCommaDelimitedString(oauthSettings.getCallbackUrls().stream().map(RedirectURI::getValue).collect(Collectors.toList()))
    );
    setClientSecret(oauthSettings.getSecret());
    if (oauthSettings.isAutoApprove()) {
      setAutoApproveScopes(Collections.singletonList(AUTO_APPROVE_SCOPE));
    }
    if (oauthSettings.isResourceServer()) {
      setAuthorities(AuthorityUtils.createAuthorityList(ROLE_TOKEN_CHECKER));
      setAuthorizedGrantTypes(Collections.emptyList());
    }
  }
}
