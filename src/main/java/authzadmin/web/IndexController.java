package authzadmin.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(method = GET)
  public ModelAndView index() {
    List<ClientDetails> clients = transactionTemplate.execute(transactionStatus -> clientRegistrationService.listClientDetails());
    clients.sort((l, r) -> l.getClientId().compareTo(r.getClientId()));
    return new ModelAndView("index", "clients", clients);
  }

}
