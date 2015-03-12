package authzadmin.web;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients/{id}")
public class DeleteController extends BaseController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(value = "/delete", method = POST)
  public String post(@PathVariable String id, RedirectAttributes redirectAttributes) {
    transactionTemplate.execute(transactionStatus -> {
        clientRegistrationService.removeClientDetails(id);
        return null;
      }
    );
    notice(redirectAttributes, "delete.success");
    return "redirect:/";
  }
}
