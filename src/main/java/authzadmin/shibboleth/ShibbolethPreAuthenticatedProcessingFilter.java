package authzadmin.shibboleth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
    Optional<String> uid = Optional.of(getHeader(UID_HEADER_NAME, request));
    Optional<String> displayName = Optional.of(getHeader(DISPLAY_NAME_HEADER_NAME, request));
    if (!uid.isPresent() || !displayName.isPresent()) {
      throw new PreAuthenticatedCredentialsNotFoundException(
        String.format("Missing header information. Required headers are: %s and %s", UID_HEADER_NAME, DISPLAY_NAME_HEADER_NAME));
    }
    return new FederatedUser(uid.get(), displayName.get(), AuthorityUtils.createAuthorityList("USER"));
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  private String getHeader(String name, HttpServletRequest request) {
    String header = request.getHeader(name);
    try {
      return StringUtils.hasText(header) ?
        new String(header.getBytes("ISO8859-1"), "UTF-8") : header;
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
