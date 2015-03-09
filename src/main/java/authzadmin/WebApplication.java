package authzadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@SpringBootApplication
public class WebApplication {

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(WebApplication.class, args);
  }

  @Bean
  public ClientRegistrationService clientRegistrationService() {
    final JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(this.dataSource);
    jdbcClientDetailsService.setPasswordEncoder(new BCryptPasswordEncoder());
    return jdbcClientDetailsService;
  }


}
