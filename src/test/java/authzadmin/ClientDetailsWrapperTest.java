package authzadmin;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import static authzadmin.WebApplication.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientDetailsWrapperTest {

  private ClientDetails clientDetails = mock(ClientDetails.class);
  private ClientDetailsWrapper wrapper = new ClientDetailsWrapper(clientDetails, true);

  @Before
  public void before() throws Exception {
    when(clientDetails.getAuthorizedGrantTypes()).thenReturn(new HashSet<>(Arrays.asList(
      CLIENT_CREDENTIALS,
      REFRESH_TOKEN,
      IMPLICIT,
      AUTHORIZATION_CODE)));
    when(clientDetails.getAuthorities()).thenReturn(Collections.singleton(ClientDetailsWrapper.ROLE_TOKEN_CHECKER_AUTHORITY));
    when(clientDetails.getClientId()).thenReturn("client id");
  }

  @Test
  public void testIsResourceServer() throws Exception {
    assertTrue(wrapper.isResourceServer());
  }

  @Test
  public void testIsClientCredentialsAllowed() throws Exception {
    assertTrue(wrapper.isClientCredentialsAllowed());
  }

  @Test
  public void testIsImplicitGrantAllowed() throws Exception {
    assertTrue(wrapper.isImplicitGrantAllowed());
  }

  @Test
  public void testIsRefreshTokenAllowed() throws Exception {
    assertTrue(wrapper.isRefreshTokenAllowed());
  }

  @Test
  public void testIsAuthorizationCodeAllowed() throws Exception {
    assertTrue(wrapper.isAuthorizationCodeAllowed());
  }

  @Test
  public void testGetClientIdEncoded() throws Exception {
    assertEquals("client+id", wrapper.getClientIdEncoded());
  }
}
