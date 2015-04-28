package authzadmin.voot;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class EnsureAccessFilter extends OncePerRequestFilter {
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
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (vootClient.hasAccess(allowedGroup)) {
      chain.doFilter(request, response);
    } else {
      response.sendError(403, "Forbidden");
    }
  }
}
