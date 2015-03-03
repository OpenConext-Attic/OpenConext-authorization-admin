package authzadmin;

import authzadmin.voot.VootClient;
import authzadmin.voot.VootFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Arrays;

@Configuration
@EnableOAuth2Client
public class VootResourceConfig {

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


  @Bean
  public OAuth2ProtectedResourceDetails voot() {
    AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
    details.setId("voot");
    details.setClientId(clientId);
    details.setClientSecret(clientSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setUserAuthorizationUri(userAuthorizationUri);
    details.setUseCurrentUri(false);
    details.setScope(Arrays.asList("read"));
    return details;
  }

  @Bean
  public OAuth2RestTemplate vootRestTemplate(OAuth2ClientContext context) {
    return new OAuth2RestTemplate(voot(), context);
  }

  @Bean
  public VootClient vootClient(OAuth2RestTemplate vootService,
                               @Value("${voot.serviceUrl}") String vootServiceUrl) {
    return new VootClient(vootService, vootServiceUrl);
  }

  @Bean
  public VootFilter vootFilter(VootClient vootClient) {
    return new VootFilter(vootClient);
  }
}
