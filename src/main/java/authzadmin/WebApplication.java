package authzadmin;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootApplication
public class WebApplication {

  public static final String ROLE_TOKEN_CHECKER = "ROLE_TOKEN_CHECKER";

  public static void main(String[] args) throws Exception {
    SpringApplication.run(WebApplication.class, args);
  }

  @Autowired
  private DataSource dataSource;

  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  @Autowired
  public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
    return new TransactionTemplate(platformTransactionManager);
  }

  @Bean
  public ClientRegistrationService clientRegistrationService() {
    final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(this.dataSource);
    jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
    return jdbcClientDetailsService;
  }

  @Bean
  @Autowired
  public ClientsAndResourcesInitializer clientsAndResourcesInitializer(
    @Value("${defaultClientsAndResourceServers.config.path}") final String configFileLocation,
    TransactionTemplate transactionTemplate) {
    final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(this.dataSource);
    jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
    return new ClientsAndResourcesInitializer(jdbcClientDetailsService, resourceLoader.getResource(configFileLocation), transactionTemplate);
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer(){
    return new ErrorCustomizer();
  }

  private static class ErrorCustomizer implements EmbeddedServletContainerCustomizer {
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
      container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/forbidden"));
    }
  }
}
