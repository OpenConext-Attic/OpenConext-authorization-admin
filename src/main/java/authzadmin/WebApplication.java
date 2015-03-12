package authzadmin;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootApplication
public class WebApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(WebApplication.class, args);
  }

  @Autowired
  private DataSource dataSource;

  @Bean
  @Autowired
  public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
    return new TransactionTemplate(platformTransactionManager);
  }


  @Bean
  public ClientRegistrationService clientRegistrationService() {
    final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(this.dataSource);
    jdbcClientDetailsService.setPasswordEncoder(new BCryptPasswordEncoder());
    return jdbcClientDetailsService;
  }

}
