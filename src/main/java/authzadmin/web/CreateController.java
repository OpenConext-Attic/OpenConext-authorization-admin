package authzadmin.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import authzadmin.OauthClientDetails;
import authzadmin.OauthSettings;

@Controller
@RequestMapping("/create")
public class CreateController extends BaseController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(method = GET)
  public ModelAndView get(OauthSettings oauthSettings) {
    return new ModelAndView("create");
  }

  @RequestMapping(method = POST)
  public String post(@Valid OauthSettings oauthSettings, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "create";
    }
    try {
      this.transactionTemplate.execute(transactionStatus -> {
          clientRegistrationService.addClientDetails(new OauthClientDetails(oauthSettings));
          return null;
        }
      );
      notice(redirectAttributes, "create.success");
      return "redirect:/";
    } catch (ClientAlreadyExistsException e) {
      bindingResult.rejectValue("consumerKey", "create.clientAlreadyExists");
      return "create";
    }
  }
}
