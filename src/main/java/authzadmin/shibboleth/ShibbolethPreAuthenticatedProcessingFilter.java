package authzadmin.shibboleth;

import authzadmin.voot.VootClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {


  public static class ShibbolethPrincipal {
    public final String uid;
    public final String displayName;

    public ShibbolethPrincipal(String uid, String displayName) {
      this.uid = uid;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return "ShibbolethPrincipal{" +
        "uid='" + uid + '\'' +
        ", displayName='" + displayName + '\'' +
        '}';
    }
  }

  public static final String UID_HEADER_NAME = "uid";
  public static final String DISPLAY_NAME_HEADER_NAME = "displayname";

  public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager) {
    super();
    setAuthenticationManager(authenticationManager);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    final Optional<String> uid = Optional.of(request.getHeader(UID_HEADER_NAME));
    final Optional<String> displayName = Optional.of(request.getHeader(DISPLAY_NAME_HEADER_NAME));
    return new ShibbolethPrincipal(uid.get(), displayName.get());
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }
}
