package authzadmin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class WebApplicationTest {

  @Value("${local.server.port}")
  private int port;

  @Test
  public void testCss() throws Exception {
    ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
      "http://localhost:" + this.port + "/css/application.css", String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals("css", entity.getHeaders().getContentType().getSubtype());
  }


}
