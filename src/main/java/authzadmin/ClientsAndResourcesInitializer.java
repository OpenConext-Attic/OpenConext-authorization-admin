package authzadmin;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static authzadmin.WebApplication.ROLE_TOKEN_CHECKER;

public class ClientsAndResourcesInitializer implements ApplicationListener<ContextRefreshedEvent> {

  private final Logger LOG = LoggerFactory.getLogger(ClientsAndResourcesInitializer.class);

  private final ClientRegistrationService clientRegistrationService;
  private final Resource resource;
  private final TransactionTemplate transactionTemplate;

  public ClientsAndResourcesInitializer(ClientRegistrationService clientRegistrationService, Resource resource, TransactionTemplate transactionTemplate) {
    this.clientRegistrationService = clientRegistrationService;
    this.resource = resource;
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOG.debug("Initializing default oAuth clients and resource-servers from {}", resource);
    try {
      Config config = ConfigFactory.parseReader(new InputStreamReader(resource.getInputStream()));

      final List<? extends ConfigObject> clients = config.getObjectList("clients");
      final List<? extends ConfigObject> resourceServers = config.getObjectList("resourceServers");

      final List<ClientDetails> resourceServersAndClientsToPersist = new ArrayList<>();
      final List<ClientDetails> preExisting = transactionTemplate.execute(transactionStatus -> clientRegistrationService.listClientDetails());

      clients.forEach(clientConfigObj -> {
        final Map<String, Object> clientConfig = clientConfigObj.unwrapped();
        final String clientId = (String) clientConfig.get("clientId");

        final Optional<ClientDetails> preExistingClientDetails = findPreExistingClientDetails(clientId, preExisting);
        BaseClientDetails clientDetails = preExistingClientDetails.isPresent() ? (BaseClientDetails) preExistingClientDetails.get() : new BaseClientDetails(clientId, null, null, null, null);

        final String secret = (String) clientConfig.get("secret");
        clientDetails.setClientSecret(secret);

        @SuppressWarnings("unchecked")
        List<String> resourceIds = (List<String>) clientConfig.get("resourceIds");
        clientDetails.setResourceIds(resourceIds);

        @SuppressWarnings("unchecked")
        final List<String> scopes = (List<String>) clientConfig.get("scopes");
        clientDetails.setScope(scopes);

        @SuppressWarnings("unchecked")
        final List<String> grantTypes = (List<String>) clientConfig.get("grantTypes");
        clientDetails.setAuthorizedGrantTypes(grantTypes);

        String autoApprove = (String) clientConfig.get("autoApprove");
        if (autoApprove != null && Boolean.valueOf(autoApprove)) {
          clientDetails.setAutoApproveScopes(Collections.singletonList(OauthClientDetails.AUTO_APPROVE_SCOPE));
        }

        final List<String> redirectUris = (List<String>) clientConfig.get("redirectUris");
        clientDetails.setRegisteredRedirectUri(new HashSet<>(redirectUris));

        resourceServersAndClientsToPersist.add(clientDetails);
      });
      resourceServers.forEach(resourceServerConfigObj -> {

        final Map<String, Object> resourceServerConfig = resourceServerConfigObj.unwrapped();
        final String clientId = (String) resourceServerConfig.get("clientId");

        final Optional<ClientDetails> preExistingClientDetails = findPreExistingClientDetails(clientId, preExisting);
        BaseClientDetails clientDetails = preExistingClientDetails.isPresent() ? (BaseClientDetails) preExistingClientDetails.get() : new BaseClientDetails(clientId, null, null, null, null);

        // always add the token checker role
        clientDetails.setAuthorities(AuthorityUtils.createAuthorityList(ROLE_TOKEN_CHECKER));
        final String secret = (String) resourceServerConfig.get("secret");
        clientDetails.setClientSecret(secret);
        clientDetails.setAuthorizedGrantTypes(Collections.emptyList());

        resourceServersAndClientsToPersist.add(clientDetails);
      });
      transactionTemplate.execute(transactionStatus -> {
        resourceServersAndClientsToPersist.forEach(clientDetails -> {
          if (findPreExistingClientDetails(clientDetails.getClientId(), preExisting).isPresent()) {
            clientRegistrationService.updateClientDetails(clientDetails);
            clientRegistrationService.updateClientSecret(clientDetails.getClientId(), clientDetails.getClientSecret());
          } else {
            clientRegistrationService.addClientDetails(clientDetails);
          }
        });
        return null;
      });
    } catch (IOException e) {
      throw new RuntimeException("Unable to read configuration", e);
    }
  }

  private Optional<ClientDetails> findPreExistingClientDetails(final String clientId, final List<ClientDetails> preExisting) {
    return preExisting.stream()
      .filter(preExistingClient -> preExistingClient.getClientId().equals(clientId))
      .findFirst();
  }

}
