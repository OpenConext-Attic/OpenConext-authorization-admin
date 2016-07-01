package authzadmin;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import static authzadmin.OauthClientDetails.AUTO_APPROVE_SCOPE;
import static authzadmin.WebApplication.*;
import static org.junit.Assert.*;

public class OauthClientDetailsTest {

  private OauthSettings oauthSettings;

  @Before
  public void before() {
    oauthSettings = new OauthSettings("secret", "client_id", "http://ignore");
  }

  @Test
  public void test_is_client_credentials_allowed() {
    oauthSettings.setClientCredentialsAllowed(true);
    OauthClientDetails detail = new OauthClientDetails( oauthSettings);
    assertTrue(detail.getAuthorizedGrantTypes().contains(CLIENT_CREDENTIALS));
  }

  @Test
  public void test_has_scopes() {
    List<Scope> scopes = Arrays.asList(new Scope("read"), new Scope("write"));
    oauthSettings.setScopes(scopes);
    OauthClientDetails detail = new OauthClientDetails( oauthSettings);
    assertTrue(detail.getScope().equals(new TreeSet<>(Arrays.asList("read","write"))));
  }

  @Test
  public void test_is_auto_approve() {
    oauthSettings.setAutoApprove(true);
    OauthClientDetails detail = new OauthClientDetails( oauthSettings);
    Set<String> autoApproveScopes = detail.getAutoApproveScopes();
    assertEquals(1, autoApproveScopes.size());
    assertEquals(AUTO_APPROVE_SCOPE, autoApproveScopes.iterator().next());
  }

  @Test
  public void test_is_resource_server() {
    oauthSettings.setResourceServer(true);
    OauthClientDetails detail = new OauthClientDetails( oauthSettings);
    Collection<GrantedAuthority> authorities = detail.getAuthorities();
    assertEquals(1, authorities.size());
    assertEquals(ROLE_TOKEN_CHECKER, authorities.iterator().next().getAuthority());
    assertTrue(detail.getAuthorizedGrantTypes().isEmpty());
  }

}
