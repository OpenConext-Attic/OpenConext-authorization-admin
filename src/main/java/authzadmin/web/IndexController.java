package authzadmin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class IndexController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(method = GET)
  public ModelAndView index() {
    List<ClientDetails> clients = clientRegistrationService.listClientDetails();
    clients.sort((l, r) -> l.getClientId().compareTo(r.getClientId()));
    return new ModelAndView("index", "clients", clients);
  }

}
