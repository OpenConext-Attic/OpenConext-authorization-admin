package authzadmin.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class RedirectURITest {

  @Test
  public void testEquals() {
    RedirectURI u1 = new RedirectURI("value");
    RedirectURI u2 = new RedirectURI();
    u2.setValue("value");

    assertEquals(u1, u2);

    assertEquals(1, new HashSet<>(Arrays.asList(u1, u2)).size());
  }
}
