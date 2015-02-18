package authzadmin;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.lift.Finders;
import org.openqa.selenium.lift.Matchers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.OK;
import static org.openqa.selenium.lift.Matchers.*;
import static org.openqa.selenium.lift.Finders.*;
import static org.hamcrest.Matchers.*;

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
  private WebDriver webDriver;
  private Pages pages;


  @Before
  public void before() {
    serverUrl = "http://localhost:" + this.port;
    webDriver = new HtmlUnitDriver();
    pages = new Pages(webDriver, serverUrl);
  }

  @After
  public void after() {
    webDriver.quit();
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

    page.tryCreateClient(new OauthSettings("secret", UUID.randomUUID().toString(), this.serverUrl));

    assertThat(page.currentPageSource(), containsString("Client added"));
  }

}
