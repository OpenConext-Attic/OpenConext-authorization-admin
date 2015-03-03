package authzadmin.voot;

import authzadmin.shibboleth.ShibbolethUserDetailService.ShibbolethUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.List;

public class VootClient {

  private final static Logger logger = LoggerFactory.getLogger(VootClient.class);

  private final String vootServiceUrl;

  private final RestOperations vootService;

  public VootClient(RestOperations vootService, String vootServiceUrl) {
    this.vootService = vootService;
    this.vootServiceUrl = vootServiceUrl;
  }

  public void enrichUser(ShibbolethUser principal) {
    List groups = vootService.getForObject(vootServiceUrl + "/me/groups", List.class);
    logger.info("Retrieved groups: {}", groups);
    principal.setAuthorities(groups);
  }
}
