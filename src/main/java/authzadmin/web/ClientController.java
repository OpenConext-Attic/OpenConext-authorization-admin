package authzadmin.web;

import authzadmin.OauthClientDetails;
import authzadmin.model.ClientValidator;
import authzadmin.model.OauthSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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

    private ClientValidator clientValidator = new ClientValidator();

    @RequestMapping(value = "/create", method = GET)
    public ModelAndView get(OauthSettings oauthSettings) {
        return new ModelAndView("create", "oauthSettings", oauthSettings);
    }

    @RequestMapping(value = "/create-resource-server", method = GET)
    public ModelAndView createResourceServer(OauthSettings oauthSettings) {
        oauthSettings.setResourceServer(true);
        oauthSettings.setAuthorizationCodeAllowed(false);
        oauthSettings.setRefreshTokenAllowed(false);
        return new ModelAndView("create-resource-server", "oauthSettings", oauthSettings);
    }


    @RequestMapping(value = "/create", method = POST)
    public String post(@Valid OauthSettings oauthSettings, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes, Authentication authentication) {
        if (clientValidator.hasErrors(oauthSettings, bindingResult)) {
            return oauthSettings.isResourceServer() ? "create-resource-server" : "create";
        }
        try {
            this.transactionTemplate.execute((transactionStatus) -> {
                    clientRegistrationService.addClientDetails(new OauthClientDetails(oauthSettings));
                    return null;
                }
            );
            notice(redirectAttributes, oauthSettings.isResourceServer() ? "create-resource-server.success" :
                "create.success");
            LOG.info("{} {} created by {}", oauthSettings.isResourceServer() ? "Resource Server" : "Client",
                oauthSettings.getConsumerKey(), authentication.getPrincipal());
            return oauthSettings.isResourceServer() ? "redirect:/resource-servers" : "redirect:/clients";
        } catch (ClientAlreadyExistsException e) {
            bindingResult.rejectValue("consumerKey", "create.clientAlreadyExists");
            return oauthSettings.isResourceServer() ? "create-resource-server" : "create";
        }
    }

    @RequestMapping(value = "/clients/{id}/edit", method = POST)
    public ModelAndView edit(@PathVariable(value = "id") String id) throws UnsupportedEncodingException {
        String decodedId = decode(id, "UTF-8");
        ClientDetails clientDetails =
            ((JdbcClientDetailsService) clientRegistrationService).loadClientByClientId(decodedId);
        return new ModelAndView("create", "oauthSettings", new OauthSettings(clientDetails));
    }

    @RequestMapping(value = "/edit", method = POST)
    public String update(@Valid OauthSettings oauthSettings, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes, Authentication authentication) {
        if (clientValidator.hasErrors(oauthSettings, bindingResult)) {
            return oauthSettings.isResourceServer() ? "create-resource-server" : "create";
        }
        this.transactionTemplate.execute((transactionStatus) -> {
                clientRegistrationService.updateClientDetails(new OauthClientDetails(oauthSettings));
                return null;
            }
        );
        LOG.info("{} {} updated by {}", oauthSettings.isResourceServer() ? "Resource Server" : "Client",
            oauthSettings.getConsumerKey(), authentication.getPrincipal());
        notice(redirectAttributes, "edit.success");
        return "redirect:/clients";
    }

    @RequestMapping(value = "/clients/{id}/reset", method = POST)
    public String reset(@PathVariable String id, RedirectAttributes redirectAttributes, HttpServletRequest request,
                        Authentication authentication)
        throws UnsupportedEncodingException {
        String decoded = decode(id, "UTF-8");
        String newSecret = UUID.randomUUID().toString();
        transactionTemplate.execute(transactionStatus -> {
                clientRegistrationService.updateClientSecret(decoded, newSecret);
                return null;
            }
        );
        notice(redirectAttributes, "reset.success", newSecret);
        LOG.info("{} {} secret reset by {}", "Client", id, authentication.getPrincipal());
        String clientType = request.getParameter("client-type");
        return clientType != null && clientType.equals("resource-servers") ? "redirect:/resource-servers" :
            "redirect:/clients";
    }

}
