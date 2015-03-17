package authzadmin;

import authzadmin.voot.VootClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import java.util.Arrays;

@Configuration
public class VootClientConfig {

  @Value("${voot.accessTokenUri}")
  private String accessTokenUri;

  @Value("${voot.userAuthorizationUri}")
  private String userAuthorizationUri;

  @Value("${voot.clientId}")
  private String clientId;

  @Value("${voot.clientSecret}")
  private String clientSecret;

  @Value("${voot.redirectUri}")
  private String redirectUri;

  @Value("${voot.scopes}")
  private String spaceDelimitedScopes;

  @Bean
  public OAuth2ProtectedResourceDetails voot() {
    AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
    details.setId("voot");
    details.setClientId(clientId);
    details.setClientSecret(clientSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setUserAuthorizationUri(userAuthorizationUri);
    details.setUseCurrentUri(false);
    details.setScope(Arrays.asList(spaceDelimitedScopes.split(" ")));
    return details;
  }

  @Bean
  public OAuth2RestTemplate vootRestTemplate(OAuth2ClientContext context) {
    return new OAuth2RestTemplate(voot(), context);
  }

  @Bean
  @Profile("!dev")
  public VootClient vootClient(OAuth2RestTemplate vootService,
                               @Value("${voot.serviceUrl}") String vootServiceUrl) {
    return new VootClient(vootService, vootServiceUrl);
  }

  @Bean
  @Profile("dev")
  public VootClient mockVootClient(OAuth2RestTemplate vootService,
                                   @Value("${voot.serviceUrl}") String vootServiceUrl) {
    return new VootClient(vootService, vootServiceUrl) {
      @Override
      public boolean hasAccess(String allowedGroup) {
        return true;
      }
    };
  }

}
