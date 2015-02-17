package authzadmin;

import authzadmin.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import authzadmin.shibboleth.ShibbolethUserDetailService;
import authzadmin.shibboleth.mock.MockShibbolethFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class ShibbolethSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethSecurityConfig.class);

  @Bean
  @Profile("dev")
  public FilterRegistrationBean mockShibbolethFilter() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    filterRegistrationBean.setFilter(new MockShibbolethFilter());
    filterRegistrationBean.addUrlPatterns("/*");
    return filterRegistrationBean;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.
      addFilterBefore(new ShibbolethPreAuthenticatedProcessingFilter(authenticationManagerBean()),
        AbstractPreAuthenticatedProcessingFilter.class)
      .authorizeRequests()
      .antMatchers("/css/**").permitAll()
      .anyRequest().hasAnyRole("USER");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    LOG.info("Configuring AuthenticationManager with a PreAuthenticatedAuthenticationProvider");
    PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
    authenticationProvider.setPreAuthenticatedUserDetailsService(new ShibbolethUserDetailService());
    auth.authenticationProvider(authenticationProvider);
  }

}
