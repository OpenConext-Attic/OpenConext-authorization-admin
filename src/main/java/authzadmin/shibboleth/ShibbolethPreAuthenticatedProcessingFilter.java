package authzadmin.shibboleth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static final String UID_HEADER_NAME = "uid";
  public static final String DISPLAY_NAME_HEADER_NAME = "displayname";

  public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager) {
    super();
    setAuthenticationManager(authenticationManager);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    Optional<String> uid = Optional.of(request.getHeader(UID_HEADER_NAME));
    Optional<String> displayName = Optional.of(request.getHeader(DISPLAY_NAME_HEADER_NAME));
    if (!uid.isPresent() || !displayName.isPresent()) {
      throw new PreAuthenticatedCredentialsNotFoundException("Missing header information");
    }
    return new FederatedUser(uid.get(), displayName.get(), AuthorityUtils.createAuthorityList("USER"));
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }
}
