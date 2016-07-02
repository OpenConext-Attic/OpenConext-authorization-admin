package authzadmin.web;

import authzadmin.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class DeleteControllerTest extends AbstractIntegrationTest{

  @Test
  public void testPost() throws Exception {
    int numberClients = numberOfClients();

    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/clients/authz-admin/delete", null, String.class);
    assertEquals(302, response.getStatusCode().value());

    assertEquals(numberClients - 1, numberOfClients());
  }
}
