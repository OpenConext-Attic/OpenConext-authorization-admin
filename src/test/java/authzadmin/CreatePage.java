package authzadmin;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static java.lang.String.format;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

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
    return webDriver.findElement(By.cssSelector(format("#%s.field-error", id)));
  }

  public String currentUrl() {
    return webDriver.getCurrentUrl();
  }

  public CreatePage tryCreateClient(OauthSettings oauthSettings) {
    webDriver.findElement(By.id("consumerKey")).sendKeys(oauthSettings.getConsumerKey());

    oauthSettings.getCallbackUrls().forEach(callbackUrl -> {
        webDriver.findElement(By.id("callbackUrl-name")).sendKeys(callbackUrl.getValue() + "\n");
        new WebDriverWait(webDriver, 5)
          .until(
            presenceOfElementLocated(By.cssSelector(format("span[data-value='%s']", callbackUrl.getValue())))
          );
      }
    );

    oauthSettings.getScopes().forEach(scope -> {
        webDriver.findElement(By.id("scope-name")).sendKeys(scope.getValue() + "\n");
        new WebDriverWait(webDriver, 5)
          .until(
            presenceOfElementLocated(By.cssSelector(format("span[data-value='%s']", scope.getValue())))
          );
      }
    );
    tryCreateClient();
    return this;
  }

  public String currentPageSource() {
    return webDriver.getPageSource();
  }
}
