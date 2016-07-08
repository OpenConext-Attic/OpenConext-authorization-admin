package authzadmin.model;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static authzadmin.WebApplication.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class OauthSettingsTest {

  private Validator validator;
  private OauthSettings oauthSettings;

  @Before
  public void setUp() throws Exception {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    oauthSettings = oauthSettings();
  }

  private OauthSettings oauthSettings() {
    oauthSettings = new OauthSettings();
    oauthSettings.setSecret("secret");
    oauthSettings.setConsumerKey( "consumerKey");
    oauthSettings.setCallbackUrls(singletonList(new RedirectURI("http://localhost:8080")));
    return oauthSettings;
  }

  @Test
  public void testValidScopeNames() throws Exception {
    oauthSettings.setScopes(asList(scope("foo"), scope("foo_bar"), scope("foo-bar"), scope("foo_bar1234345")));

    Set<ConstraintViolation<OauthSettings>> violations = validator.validate(oauthSettings);
    assertEquals(0, violations.size());
  }

  @Test
  public void testInvalidRedirect() throws Exception {
    oauthSettings.setCallbackUrls(asList(redirectURI("foo"), redirectURI("foo_bar"),redirectURI("http://localhost:8080")));

    Set<ConstraintViolation<OauthSettings>> violations = validator.validate(oauthSettings);
    assertEquals(2, violations.size());
  }

  @Test
  public void testNoSpacesInScopeNames() throws Exception {
    oauthSettings.setScopes(asList(scope("foo bar")));

    Set<ConstraintViolation<OauthSettings>> violations = validator.validate(oauthSettings);
    assertEquals(1, violations.size());
  }

  @Test
  public void testGrantTypes() throws Exception {
    oauthSettings.setAuthorizationCodeAllowed(true);
    oauthSettings.setRefreshTokenAllowed(true);
    oauthSettings.setImplicitGrantAllowed(true);
    oauthSettings.setClientCredentialsAllowed(true);
    assertEquals(String.join(",",asList(AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT, CLIENT_CREDENTIALS)),oauthSettings.grantTypes());
  }

  @Test
  public void testEquals() {
    OauthSettings other = oauthSettings();
    assertEquals(oauthSettings, other);
  }

  private Scope scope(String value) {
    return new Scope(value);
  }
  private RedirectURI redirectURI(String value) {
    return new RedirectURI(value);
  }
}
