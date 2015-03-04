package authzadmin.voot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.List;
import java.util.Map;

public class VootClient {

  private final static Logger logger = LoggerFactory.getLogger(VootClient.class);

  private final String vootServiceUrl;

  private final RestOperations vootService;

  public VootClient(RestOperations vootService, String vootServiceUrl) {
    this.vootService = vootService;
    this.vootServiceUrl = vootServiceUrl;
  }

  public boolean hasAccess(String allowedGroup) {
    List<Map<String, ?>> groups = vootService.getForObject(vootServiceUrl + "/me/groups", List.class);
    logger.info("Retrieved groups: {}", groups);
    return groups.stream().anyMatch(g -> g.get("id").equals(allowedGroup));
  }
}
