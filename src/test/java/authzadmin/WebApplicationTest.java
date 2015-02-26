package authzadmin;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
@ActiveProfiles("dev")
public class WebApplicationTest {

  @Value("${local.server.port}")
  private int port;

  private String serverUrl;
  private static WebDriver webDriver;
  private Pages pages;
  private OauthSettings oauthSettings;

  @BeforeClass
  public static void beforeClass() throws IOException {
    Properties properties = new Properties();
    properties.load(new FileReader(new File("./target/selenium/display.properties")));
    String display = properties.getProperty("DISPLAY");
    FirefoxBinary firefoxBinary = new FirefoxBinary();
    firefoxBinary.setEnvironmentProperty("DISPLAY", display);
    webDriver = new FirefoxDriver(firefoxBinary, null);
  }

  @AfterClass
  public static void afterClass() {
    webDriver.quit();
  }

  @Before
  public void before() {
    serverUrl = "http://localhost:" + this.port;
    pages = new Pages(webDriver, serverUrl);
    oauthSettings = new OauthSettings("secret", UUID.randomUUID().toString(), this.serverUrl);
  }

  @Test
  public void testCanNavigateToCreateFromIndex() throws IOException {
    IndexPage page = pages.indexPage();
    CreatePage createPage = page.navigateToCreatePage();

    assertEquals(this.serverUrl + "/create", createPage.currentUrl());
  }

  @Test
  public void testShowsAnErrorMessageWhenFormIsEmpty() throws Exception {
    CreatePage page = pages.createPage();

    page.tryCreateClient();

    assertNotNull(page.elementWithFieldError("consumerKey"));
    assertNotNull(page.elementWithFieldError("secret"));
    assertNotNull(page.elementWithFieldError("callbackUrl"));
  }

  @Test
  public void testCreatesAClient() throws Exception {
    CreatePage page = new CreatePage(this.serverUrl, webDriver);

    page.tryCreateClient(oauthSettings);

    assertThat(page.currentPageSource(), containsString("Client added"));
  }

  @Test
  public void testCreateClientWithScopes() throws Exception {
    CreatePage page = new CreatePage(this.serverUrl, webDriver);
    oauthSettings.getScopes().add(new Scope("valid_scope"));

    page.tryCreateClient(oauthSettings);

    assertThat(page.currentPageSource(), containsString("Client added"));
  }
}
