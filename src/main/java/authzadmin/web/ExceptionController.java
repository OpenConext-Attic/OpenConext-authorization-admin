package authzadmin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {
  private final static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

  @ExceptionHandler(OAuth2AccessDeniedException.class)
  public ModelAndView onVootException(OAuth2AccessDeniedException exception) {
    logger.error("Access denied!", exception);
    if(exception.getCause() instanceof HttpClientErrorException) {
      HttpClientErrorException httpClientErrorException = (HttpClientErrorException) exception.getCause();
      logger.error("getStatusCode '{}'", httpClientErrorException.getStatusCode());
      logger.error("getStatusText '{}'", httpClientErrorException.getStatusText());
      logger.error("Responsebody '{}' ", httpClientErrorException.getResponseBodyAsString());
      logger.error("message '{}' ", httpClientErrorException.getMessage());
    }
    return new ModelAndView("error");
  }
}
