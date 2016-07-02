package authzadmin.model;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ClientValidatorTest {

  private ClientValidator subject = new ClientValidator();
  private OauthSettings settings;
  private BindingResult bindingResult;

  @Before
  public void setUp() throws Exception {
    settings = new OauthSettings();
    bindingResult = new MapBindingResult(new HashMap<>(), "subject");
  }

  @Test
  public void testHasNoGrants() throws Exception {
    hasError();

    String code = bindingResult.getFieldError("authorizationCodeAllowed").getCode();
    assertEquals("create.grantTypeRequired", code);
  }

  private void hasError() {
    boolean b = subject.hasErrors(settings, bindingResult);
    assertTrue(b);
  }

  @Test
  public void testCallbackHasErrors() throws Exception {
    bindingResult.addError(new FieldError("subject","callbackUrls[","Wrong"));
    hasError();

    String code = bindingResult.getFieldError("callbackUrls").getCode();
    assertEquals("create.callbackUrlsInvalid", code);
  }

  @Test
  public void testCallbackRequiredForAuthorizationCode() throws Exception {
    settings.setAuthorizationCodeAllowed(true);
    callbackRequired();
  }

  @Test
  public void testCallbackRequiredForImplicit() throws Exception {
    settings.setImplicitGrantAllowed(true);
    callbackRequired();
  }

  private void callbackRequired() {
    hasError();

    String code = bindingResult.getFieldError("callbackUrls").getCode();
    assertEquals("create.callbackUrlsRequired", code);
  }
}
