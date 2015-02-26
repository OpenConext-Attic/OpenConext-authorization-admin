package authzadmin.web;

import authzadmin.OauthClientDetails;
import authzadmin.OauthSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/clients/{id}")
public class DeleteController extends BaseController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @RequestMapping(value = "/delete", method = POST)
  public String post(@PathVariable String id, RedirectAttributes redirectAttributes) {
    new TransactionTemplate(transactionManager).execute((TransactionStatus transactionStatus) -> {
        clientRegistrationService.removeClientDetails(id);
        return null;
      }
    );
    notice(redirectAttributes, "delete.success");
    return "redirect:/";
  }
}
