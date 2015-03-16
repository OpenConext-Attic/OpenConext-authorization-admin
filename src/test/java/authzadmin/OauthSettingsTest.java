package authzadmin;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class OauthSettingsTest {

  private Validator validator;
  private OauthSettings oauthSettings;

  @Before
  public void setUp() throws Exception {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    oauthSettings = new OauthSettings("secret", "consumerKey", "http://localhost:8080");
  }

  @Test
  public void testValidScopeNames() throws Exception {
    oauthSettings.setScopes(asList(scope("foo"), scope("foo_bar"), scope("foo-bar"), scope("foo_bar1234345")));

    Set<ConstraintViolation<OauthSettings>> violations = validator.validate(oauthSettings);
    assertEquals(0, violations.size());
  }

  @Test
  public void testNoSpacesInScopeNames() throws Exception {
    oauthSettings.setScopes(asList(scope("foo bar")));

    Set<ConstraintViolation<OauthSettings>> violations = validator.validate(oauthSettings);
    assertEquals(1, violations.size());
  }

  private Scope scope(String value) {
    return new Scope(value);
  }
}
