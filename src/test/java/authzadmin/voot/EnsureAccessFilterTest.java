package authzadmin.voot;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

public class EnsureAccessFilterTest {

  private boolean hasAccess = false;
  private EnsureAccessFilter subject = new EnsureAccessFilter(new VootClient(null, null) {
    @Override
    public boolean hasAccess(String allowedGroup) {
      return hasAccess;
    }
  }, "test");

  @Test
  public void testDoFilterInternal() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();
    subject.doFilterInternal(new MockHttpServletRequest(), response, new MockFilterChain());
    assertEquals(403, response.getStatus());

  }
}
