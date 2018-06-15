package authzadmin;

import authzadmin.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import authzadmin.shibboleth.ShibbolethUserDetailService;
import authzadmin.shibboleth.mock.MockShibbolethFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
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
    private Environment environment;

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    protected AccessTokenRequest accessTokenRequest(@Value("#{request.parameterMap}")
                                                        Map<String, String[]> parameters,
                                                    @Value("#{request.getAttribute('currentUri')}") String currentUri) {
        DefaultAccessTokenRequest request = new DefaultAccessTokenRequest(parameters);
        request.setCurrentUri(currentUri);
        return request;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
            .antMatchers("/css/**")
            .antMatchers("/javascripts/**")
            .antMatchers("/info")
            .antMatchers("/health");
    }
    /*
     * See http://stackoverflow.com/questions/22998731/httpsecurity-websecurity-and-authenticationmanagerbuilder
     * for a quick overview of the differences between the three configure overrides
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(
                new ShibbolethPreAuthenticatedProcessingFilter(authenticationManagerBean()),
                AbstractPreAuthenticatedProcessingFilter.class
            )
            .authorizeRequests().anyRequest().authenticated();

        //we want to specify the exact order and RegistrationBean#setOrder does not support pinpointing the order
        // before class
        //see https://github.com/spring-projects/spring-boot/issues/1640
        if (environment.acceptsProfiles("dev")) {
            http.addFilterBefore(new MockShibbolethFilter(), ShibbolethPreAuthenticatedProcessingFilter.class);
            http.csrf().disable();
        }

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //because Autowired this will end up in the global ProviderManager
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(new ShibbolethUserDetailService());
        auth.authenticationProvider(authenticationProvider);
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

}
