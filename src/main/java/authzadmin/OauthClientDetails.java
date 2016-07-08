package authzadmin;

import authzadmin.model.*;
import authzadmin.model.RedirectURI;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static authzadmin.WebApplication.ROLE_TOKEN_CHECKER;
import static java.util.stream.Collectors.toList;

public class OauthClientDetails extends BaseClientDetails {

  /*
   * Spring logic for auto approving all scopes
   */
  public static final String AUTO_APPROVE_SCOPE = "true";

  public OauthClientDetails(OauthSettings oauthSettings) {
    super(
      oauthSettings.getConsumerKey(),
      collectionToCommaDelimitedValue(oauthSettings.getResourceIds()),
      collectionToCommaDelimitedValue(oauthSettings.getScopes()),
      oauthSettings.grantTypes(),
      null,
      collectionToCommaDelimitedValue(oauthSettings.getCallbackUrls())
    );
    setClientSecret(oauthSettings.getSecret());
    if (oauthSettings.isAutoApprove()) {
      setAutoApproveScopes(Collections.singletonList(AUTO_APPROVE_SCOPE));
    }
    if (oauthSettings.isResourceServer()) {
      setAuthorities(AuthorityUtils.createAuthorityList(ROLE_TOKEN_CHECKER));
      setAuthorizedGrantTypes(Collections.singletonList("resource_server"));
    }
  }

  private static String collectionToCommaDelimitedValue(Collection<? extends ValueHolder> valueHolders) {
    return CollectionUtils.isEmpty(valueHolders) ? null : StringUtils.collectionToCommaDelimitedString(valueHolders.stream().map(ValueHolder::getValue).collect(toList()));
  }
}
