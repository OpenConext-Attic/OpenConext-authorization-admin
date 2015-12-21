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
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController implements ApplicationListener<ContextRefreshedEvent> {

  @Value("${defaultClientsAndResourceServers.config.path}")
  private String configFileLocation;

  @Autowired
  private ResourceLoader resourceLoader;


  @Value("${allowed_group}")
  private String allowedGroup;

  private List<String> immutableClientIds = new ArrayList<>();

  @Autowired
  private ClientRegistrationService clientRegistrationService;

  @RequestMapping(value = "/", method = GET)
  public ModelAndView index() {
    List<ClientDetails> clients = transactionTemplate.execute(transactionStatus -> clientRegistrationService.listClientDetails());
    clients.sort((l, r) -> l.getClientId().compareTo(r.getClientId()));
    List<ClientDetailsWrapper> wrappedClients = clients.stream().map(client -> new ClientDetailsWrapper(client, isMutable(client.getClientId()))).collect(Collectors.toList());
    return new ModelAndView("index", "clients", wrappedClients);
  }

  @RequestMapping(value = "/forbidden")
  public ModelAndView forbidden() {
    return new ModelAndView("forbidden", "allowedGroup", allowedGroup);
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
