package authzadmin;

import authzadmin.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import authzadmin.shibboleth.ShibbolethUserDetailService;
import authzadmin.shibboleth.mock.MockShibbolethFilter;
import authzadmin.voot.EnsureAccessFilter;
import authzadmin.voot.VootClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class ShibbolethSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethSecurityConfig.class);

  @Autowired
  private VootClient vootClient;

  @Value("${allowed_group}")
  private String allowedGroup;

  @Bean
  @Profile("dev")
  public FilterRegistrationBean mockShibbolethFilter() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    filterRegistrationBean.setFilter(new MockShibbolethFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    return filterRegistrationBean;
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  protected AccessTokenRequest accessTokenRequest(@Value("#{request.parameterMap}")
                                                  Map<String, String[]> parameters, @Value("#{request.getAttribute('currentUri')}")
                                                  String currentUri) {
    DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(parameters);
    request.setCurrentUri(currentUri);
    return request;
  }

  @Configuration
  protected static class OAuth2ClientContextConfiguration {

    @Resource
    @Qualifier("accessTokenRequest")
    private AccessTokenRequest accessTokenRequest;

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    public OAuth2ClientContext oauth2ClientContext() {
      return new DefaultOAuth2ClientContext(accessTokenRequest);
    }

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
      .ignoring()
      .antMatchers("/css/**")
      .antMatchers("/javascripts/**")
      .antMatchers("/health");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .addFilterBefore(
        new ShibbolethPreAuthenticatedProcessingFilter(authenticationManagerBean()),
        AbstractPreAuthenticatedProcessingFilter.class
      )
      .addFilterBefore(
        new EnsureAccessFilter(
          vootClient, allowedGroup), ShibbolethPreAuthenticatedProcessingFilter.class
      )
      .addFilterBefore(
        new OAuth2ClientContextFilter(), EnsureAccessFilter.class
      )
      .authorizeRequests().anyRequest().authenticated();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    LOG.info("Configuring AuthenticationManager with a PreAuthenticatedAuthenticationProvider");
    PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
    authenticationProvider.setPreAuthenticatedUserDetailsService(new ShibbolethUserDetailService());
    auth.authenticationProvider(authenticationProvider);
  }

}
