package authzadmin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(WebApplication.class, args);
  }

}
