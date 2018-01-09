package authzadmin.web;

import authzadmin.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static org.springframework.util.StringUtils.countOccurrencesOf;


public class IndexControllerTest extends AbstractIntegrationTest {

  @Test
  public void testClients() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/clients", String.class);

    assertEquals(200, response.getStatusCode().value());
    //one tr for the header and one for every resource server
    assertEquals(numberOfClients() + 1, countOccurrencesOf(response.getBody(), "</tr>"));
  }

  @Test
  public void testResourceServers() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/resource-servers", String.class);

    assertEquals(200, response.getStatusCode().value());
    //one tr for the header and one for every client
    assertEquals(numberOfResourceServers() + 1, countOccurrencesOf(response.getBody(), "</tr>"));
  }

  @Test
  public void testForbidden() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/forbidden", String.class);

    assertTrue(response.getBody().contains("Access denied"));
  }
}
