package authzadmin;

import com.google.common.collect.ImmutableList;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static authzadmin.WebApplication.CLIENT_CREDENTIALS;

public class OauthClientDetails extends BaseClientDetails {

  /*
   * Spring logic for auto approving all scopes
   */
  public static final String AUTO_APPROVE_SCOPE = "true";
  private static final String DEFAULT_AUTHORIZED_GRANT_TYPES = "authorization_code,refresh_token,implicit";

  public OauthClientDetails(OauthSettings oauthSettings) {
    super(
      oauthSettings.getConsumerKey(),
      null,
      CollectionUtils.isEmpty(oauthSettings.getScopes()) ? null : StringUtils.collectionToCommaDelimitedString(oauthSettings.getScopes().stream().map(Scope::getValue).collect(Collectors.toList())),
      oauthSettings.isClientCredentialsAllowed() ? DEFAULT_AUTHORIZED_GRANT_TYPES + "," + CLIENT_CREDENTIALS : DEFAULT_AUTHORIZED_GRANT_TYPES,
      null,
      oauthSettings.getCallbackUrl()
    );
    setClientSecret(oauthSettings.getSecret());
    if (oauthSettings.isAutoApprove()) {
      setAutoApproveScopes(Arrays.asList(AUTO_APPROVE_SCOPE));
    }
    if (oauthSettings.isResourceServer()) {
      setAuthorities(ImmutableList.of(new SimpleGrantedAuthority(WebApplication.ROLE_TOKEN_CHECKER)));
      setAuthorizedGrantTypes(Collections.emptyList());
    }
  }
}
