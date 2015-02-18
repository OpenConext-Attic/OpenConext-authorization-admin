package authzadmin.web;

import authzadmin.OauthSettings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class IndexController {

  @RequestMapping(method = GET)
  public ModelAndView index() {
    return new ModelAndView("index");
  }

}
