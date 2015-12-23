package authzadmin;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScopeTest {

  @Test
  public void testEquals() throws Exception {
    Scope s1 = new Scope("value");
    Scope s2 = new Scope("value");

    assertEquals(s1, s2);
  }
}
