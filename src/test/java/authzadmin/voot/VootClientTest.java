package authzadmin.voot;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VootClientTest {

  private VootClient subject = new VootClient(new RestTemplate(), "http://localhost:8889");

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

  @Test
  public void testHasNoAccess() throws Exception {
    stubFor(get(urlEqualTo("/me/groups/id1")).willReturn(aResponse().withStatus(404).withHeader("Content-Type", "application/json")));
    assertFalse(subject.hasAccess("id1"));
  }

  @Test
  public void testHasAccess() throws Exception {
    String response = StreamUtils.copyToString(new ClassPathResource("json/voot_group.json").getInputStream(), Charset.forName("UTF-8"));
    stubFor(get(urlEqualTo("/me/groups/id1")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(response)));
    assertTrue(subject.hasAccess("id1"));
  }
}
