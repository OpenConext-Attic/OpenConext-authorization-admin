package authzadmin.model;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

public class ClientValidator {

  public boolean hasErrors(OauthSettings oauthSettings, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      if (bindingResult.getFieldErrors().stream().anyMatch(err -> err.getField().startsWith("callbackUrls["))) {
        bindingResult.rejectValue("callbackUrls", "create.callbackUrlsInvalid");
      }
      return true;
    }
    if ((oauthSettings.isAuthorizationCodeAllowed() || oauthSettings.isImplicitGrantAllowed())
      && CollectionUtils.isEmpty(oauthSettings.getCallbackUrls())) {
      bindingResult.rejectValue("callbackUrls", "create.callbackUrlsRequired");
      return true;
    }
    if (oauthSettings.grantTypes().length() == 0 && !oauthSettings.isResourceServer()) {
      bindingResult.rejectValue("authorizationCodeAllowed", "create.grantTypeRequired");
      return true;
    }
    return false;
  }

}
