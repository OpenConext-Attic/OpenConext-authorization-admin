package authzadmin.web;

import static java.net.URLDecoder.decode;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Controller
@RequestMapping("/clients/{id}")
public class DeleteController extends BaseController {

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(value = "/delete", method = POST)
  public String post(@PathVariable String id, RedirectAttributes redirectAttributes, HttpServletRequest request) throws UnsupportedEncodingException {
    String decoded = decode(id, "UTF-8");
    transactionTemplate.execute(transactionStatus -> {
        clientRegistrationService.removeClientDetails(decoded);
        return null;
      }
    );
    String clientType = request.getParameter("client-type");
    boolean isResourceServer = clientType != null && clientType.equals("resource-servers");
    notice(redirectAttributes, isResourceServer ? "delete-resource-server.success" : "delete.success" );
    return isResourceServer ? "redirect:/resource-servers" : "redirect:/clients";
  }
}
