package authzadmin.voot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

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
    try {
      Map<String, ?> group = vootService.getForObject(vootServiceUrl + "/groups/{allowedGroup}", Map.class, allowedGroup);
      logger.debug("Retrieved group: {}", group);
      return true;
    } catch (HttpClientErrorException e) {
      logger.error(String.format("Unauthorized access. User does not belong to the group %s", allowedGroup), e);
      return false;
    }
  }
}
