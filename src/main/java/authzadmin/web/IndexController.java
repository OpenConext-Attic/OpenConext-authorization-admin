package authzadmin.web;

import authzadmin.ClientDetailsWrapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${defaultClientsAndResourceServers.config.path}")
    private String configFileLocation;

    @Autowired
    private ResourceLoader resourceLoader;


    private List<String> immutableClientIds = new ArrayList<>();

    @Autowired
    private ClientRegistrationService clientRegistrationService;

    @RequestMapping(value = "/clients", method = GET)
    public ModelAndView clients() {
        return doClients("clients", client -> !client.isResourceServer());
    }

    @RequestMapping(value = "/resource-servers", method = GET)
    public ModelAndView resourceServers() {
        return doClients("resource-servers", client -> client.isResourceServer());
    }

    private ModelAndView doClients(String viewName, Predicate<ClientDetailsWrapper> filter) {
        List<ClientDetails> clients = transactionTemplate.execute(transactionStatus -> clientRegistrationService
            .listClientDetails());
        clients.sort((l, r) -> l.getClientId().compareTo(r.getClientId()));
        List<ClientDetailsWrapper> wrappedClients = clients.stream()
            .map(client -> new ClientDetailsWrapper(client, isMutable(client.getClientId())))
            .filter(filter)
            .collect(Collectors.toList());
        ModelAndView model = new ModelAndView(viewName, "clients", wrappedClients);
        model.addObject("viewName", viewName);
        return model;
    }

    @RequestMapping(value = "/forbidden")
    public ModelAndView forbidden() {
        return new ModelAndView("forbidden");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            Resource resource = resourceLoader.getResource(configFileLocation);
            Config config = ConfigFactory.parseReader(new InputStreamReader(resource.getInputStream()));

            immutableClientIds.addAll(mapClientIds(config.getObjectList("clients")));
            immutableClientIds.addAll(mapClientIds(config.getObjectList("resourceServers")));

        } catch (IOException e) {
            throw new RuntimeException("Unable to read configuration", e);
        }

    }

    private List<String> mapClientIds(List<? extends ConfigObject> clients) {
        return clients.stream().map(clientConfigObj -> {
            final Map<String, Object> clientConfig = clientConfigObj.unwrapped();
            return (String) clientConfig.get("clientId");
        }).collect(Collectors.toList());
    }

    private boolean isMutable(String clientId) {
        return !this.immutableClientIds.contains(clientId);
    }

}
