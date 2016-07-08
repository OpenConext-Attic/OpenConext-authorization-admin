package authzadmin.web;

import authzadmin.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ClientControllerTest extends AbstractIntegrationTest {

  @Test
  public void createClient() throws Exception {
    doCreate(this::numberOfClients, "resourceServer=false&consumerKey=%s&secret=%s&callbackUrls=http://localhost:8080&_callbackUrls=1&scopes=groups&_scopes=1&resourceIds=groups&_resourceIds=1&_autoApprove=on&authorizationCodeAllowed=true&_authorizationCodeAllowed=on&refreshTokenAllowed=true&_refreshTokenAllowed=on&_implicitGrantAllowed=on&_clientCredentialsAllowed=on&create-client=Save",
      "clients");
  }

  @Test
  public void createResourceServer() throws Exception {
    String uuid = UUID.randomUUID().toString();
    doCreate(this::numberOfResourceServers, "resourceServer=true&consumerKey=%s&secret=%s&callbackUrls=http://localhost:8080&_callbackUrls=1&scopes=groups&_scopes=1&create-client=Save",
      uuid, "resource-servers");
    assertEquals(singleton("resource_server"), findByClientId(uuid).getAuthorizedGrantTypes());
  }

  @Test
  public void getCreateClient() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/create", String.class);

    assertEquals(200, response.getStatusCode().value());
    assertTrue(response.getBody().contains("<input type=\"hidden\" id=\"resourceServer\" name=\"resourceServer\" value=\"false\" />"));
  }

  @Test
  public void testCreateClientDuplicateKey() throws Exception {
    MultiValueMap<String, String> map = formMap("resourceServer=true&consumerKey=%s&secret=secret", "authz-admin");
    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/create", map, String.class);

    assertTrue(response.getBody().contains("must be unique"));
  }

  @Test
  public void testCreateClientErrors() throws Exception {
    MultiValueMap<String, String> map = formMap("resourceServer=true&secret=secret");
    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/create", map, String.class);

    assertTrue(response.getBody().contains("may not be null"));
  }

  @Test
  public void testEditClientErrors() throws Exception {
    MultiValueMap<String, String> map = formMap("resourceServer=true&secret=secret");
    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/edit", map, String.class);

    assertTrue(response.getBody().contains("may not be null"));
  }

  @Test
  public void getCreateResourceServer() throws Exception {
    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/create-resource-server", String.class);

    assertEquals(200, response.getStatusCode().value());
    assertTrue(response.getBody().contains("<input type=\"hidden\" id=\"resourceServer\" name=\"resourceServer\" value=\"true\" />"));
  }

  @Test
  public void editClient() throws Exception {
    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/clients/authz-admin/edit", null, String.class);

    assertTrue(response.getBody().contains("<input type=\"text\" class=\"large\" readonly=\"readonly\" id=\"consumerKey\" name=\"consumerKey\" value=\"authz-admin\" />"));

    String redirectUri = "http://localhost/changed";
    MultiValueMap<String, String> map = formMap(
      "resourceServer=%s&consumerKey=%s&secret=secret&callbackUrls=%s&_callbackUrls=1&scopes=groups&_scopes=1&resourceIds=groups&_resourceIds=1&_autoApprove=on&authorizationCodeAllowed=true&_authorizationCodeAllowed=on&refreshTokenAllowed=true&_refreshTokenAllowed=on&_implicitGrantAllowed=on&_clientCredentialsAllowed=on&create-client=Save"
      , false, "authz-admin", redirectUri);

    response = restTemplate.postForEntity("http://localhost:" + port + "/edit", map, String.class);

    assertEquals(302, response.getStatusCode().value());
    assertEquals("http://localhost:" + port + "/clients", response.getHeaders().getLocation().toString());

    ClientDetails clientDetails = findByClientId("authz-admin");

    Set<String> redirectUris = clientDetails.getRegisteredRedirectUri();
    assertEquals(1, redirectUris.size());
    assertEquals(redirectUri, redirectUris.iterator().next());
  }

  @Test
  public void reset() throws Exception {
    String clientId = "authz-admin";
    ClientDetails clientDetails = findByClientId(clientId);

    String secret = clientDetails.getClientSecret();
    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/clients/" + clientId + "/reset", null, String.class);
    assertEquals("http://localhost:" + port + "/clients", response.getHeaders().getLocation().toString());

    clientDetails = findByClientId(clientId);
    assertNotEquals(secret, clientDetails.getClientSecret());
  }

  private void doCreate(Supplier<Integer> numberOfClients, String params, final String redirect) {
    String uuid = UUID.randomUUID().toString();
    doCreate(numberOfClients, params, uuid, redirect);
  }

  private void doCreate(Supplier<Integer> numberOfClients, String params, String clientId, String redirect) {
    int currentClients = numberOfClients.get();
    MultiValueMap<String, String> map = formMap(params, clientId, clientId);

    ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/create", map, String.class);

    assertEquals(currentClients + 1, numberOfClients.get().intValue());
    assertEquals(302, response.getStatusCode().value());
    assertEquals(response.getHeaders().getLocation().toString(), "http://localhost:" + port + "/" + redirect);
  }
}
