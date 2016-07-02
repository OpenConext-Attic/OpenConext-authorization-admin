package authzadmin.model;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

public class ClientValidator {

  public boolean hasErrors(OauthSettings oauthSettings, BindingResult bindingResult) {
    boolean result = false;
    if (bindingResult.hasErrors()) {
      if (bindingResult.getFieldErrors().stream().anyMatch(err -> err.getField().startsWith("callbackUrls["))) {
        bindingResult.rejectValue("callbackUrls", "create.callbackUrlsInvalid");
      }
      if (bindingResult.getFieldErrors().stream().anyMatch(err -> err.getField().startsWith("scopes["))) {
        bindingResult.rejectValue("scopes", "create.scopesInvalid");
      }
      result = true;
    }
    if ((oauthSettings.isAuthorizationCodeAllowed() || oauthSettings.isImplicitGrantAllowed())
      && CollectionUtils.isEmpty(oauthSettings.getCallbackUrls())) {
      bindingResult.rejectValue("callbackUrls", "create.callbackUrlsRequired");
      result = true;
    }
    if (oauthSettings.grantTypes().length() == 0 && !oauthSettings.isResourceServer()) {
      bindingResult.rejectValue("authorizationCodeAllowed", "create.grantTypeRequired");
      result = true;
    }
    return result;
  }

}
