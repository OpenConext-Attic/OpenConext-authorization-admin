package authzadmin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreatePage {
  private final WebDriver webDriver;

  public CreatePage(String serverUrl, WebDriver webDriver) {
    this.webDriver = webDriver;
    this.webDriver.get(serverUrl + "/create");
  }

  public CreatePage tryCreateClient() {
    webDriver.findElement(By.name("create-client")).click();
    return this;
  }

  public WebElement elementWithFieldError(String id) {
    return webDriver.findElement(By.cssSelector(String.format("#%s.field-error", id)));
  }

  public String currentUrl() {
    return webDriver.getCurrentUrl();
  }

  public CreatePage tryCreateClient(OauthSettings oauthSettings) {
    webDriver.findElement(By.id("consumerKey")).sendKeys(oauthSettings.getConsumerKey());
    webDriver.findElement(By.id("secret")).sendKeys(oauthSettings.getSecret());
    webDriver.findElement(By.id("callbackUrl")).sendKeys(oauthSettings.getCallbackUrl());
    tryCreateClient();
    return this;
  }

  public String currentPageSource() {
    return webDriver.getPageSource();
  }
}
