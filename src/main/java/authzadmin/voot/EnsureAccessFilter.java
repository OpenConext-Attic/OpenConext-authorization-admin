package authzadmin.voot;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class EnsureAccessFilter extends GenericFilterBean {
  private final VootClient vootClient;
  private final String allowedGroup;

  public EnsureAccessFilter(VootClient vootClient, String allowedGroup) {
    this.allowedGroup = allowedGroup;
    if (vootClient == null) {
      throw new IllegalArgumentException("vootClient cannot be null");
    }
    this.vootClient = vootClient;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if(vootClient.hasAccess(allowedGroup)) {
      chain.doFilter(request, response);
    } else {
      throw new AccessDeniedException("no access");
    }
  }
}
