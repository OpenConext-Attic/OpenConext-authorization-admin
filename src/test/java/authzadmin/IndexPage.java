package authzadmin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IndexPage {
  private final String serverUrl;
  private final WebDriver webDriver;

  public IndexPage(String serverUrl, WebDriver webDriver) {
    this.serverUrl = serverUrl;
    this.webDriver = webDriver;
    this.webDriver.get(serverUrl);
  }

  public CreatePage navigateToCreatePage() {
    webDriver.findElement(By.id("add-client")).click();
    return new CreatePage(this.serverUrl, webDriver);
  }
}
