package authzadmin.voot;

import authzadmin.shibboleth.ShibbolethUserDetailService.ShibbolethUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class VootFilter extends GenericFilterBean {
  private final VootClient vootClient;

  public VootFilter(VootClient vootClient) {
    if (vootClient == null) {
      throw new IllegalArgumentException("vootClient cannot be null");
    }
    this.vootClient = vootClient;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    vootClient.enrichUser((ShibbolethUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    chain.doFilter(request, response);
  }
}
