package authzadmin.web;

import authzadmin.OauthClientDetails;
import authzadmin.OauthSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static java.net.URLDecoder.decode;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ClientController extends BaseController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(value = "/create", method = GET)
  public ModelAndView get(OauthSettings oauthSettings) {
    return new ModelAndView("create");
  }

  @RequestMapping(value = "/create", method = POST)
  public String post(@Valid OauthSettings oauthSettings, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "create";
    }
    try {
      this.transactionTemplate.execute((transactionStatus) -> {
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

  @RequestMapping(value = "/clients/{id}/edit", method = POST)
  public ModelAndView edit(@PathVariable(value = "id") String id, OauthSettings oauthSettings) throws UnsupportedEncodingException {
    String decodedId = decode(id, "UTF-8");
    ClientDetails clientDetails =
      ((JdbcClientDetailsService) clientRegistrationService).loadClientByClientId(decodedId);
    return new ModelAndView("create", "oauthSettings", new OauthSettings(clientDetails));
  }

  @RequestMapping(value = "/edit", method = POST)
  public String update(@Valid OauthSettings oauthSettings, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "create";
    }
    this.transactionTemplate.execute((transactionStatus) -> {
        clientRegistrationService.updateClientDetails(new OauthClientDetails(oauthSettings));
        return null;
      }
    );
    notice(redirectAttributes, "edit.success");
    return "redirect:/";
  }

  @RequestMapping(value = "/clients/{id}/reset", method = POST)
  public String post(@PathVariable String id, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
    String decoded = decode(id, "UTF-8");
    String newSecret = UUID.randomUUID().toString();
    transactionTemplate.execute(transactionStatus -> {
        clientRegistrationService.updateClientSecret(decoded, newSecret);
        return null;
      }
    );
    notice(redirectAttributes, "reset.success", newSecret);
    return "redirect:/";
  }

}
