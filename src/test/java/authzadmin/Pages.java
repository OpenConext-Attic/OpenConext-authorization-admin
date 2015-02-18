package authzadmin;

import org.openqa.selenium.WebDriver;

public class Pages {
  private final WebDriver webDriver;
  private final String serverUrl;


  public Pages(WebDriver webDriver, String serverUrl) {
    this.webDriver = webDriver;
    this.serverUrl = serverUrl;
  }

  public IndexPage indexPage() {
    return new IndexPage(this.serverUrl, this.webDriver);
  }

  public CreatePage createPage() {
    return new CreatePage(this.serverUrl, this.webDriver);
  }

}
