package authzadmin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

public abstract class BaseController {

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  protected TransactionTemplate transactionTemplate;

  protected void notice(RedirectAttributes redirectAttributes, String key, Object... args) {
    redirectAttributes.addFlashAttribute("flash.notice", messageSource.getMessage(key, args, Locale.ENGLISH));
  }

}
