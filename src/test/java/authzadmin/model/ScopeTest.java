package authzadmin.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ScopeTest {

  @Test
  public void testEquals() throws Exception {
    Scope s1 = new Scope("value");
    Scope s2 = new Scope();
    s2.setValue("value");

    assertEquals(s1, s2);

    assertEquals(1, new HashSet(Arrays.asList(s1, s2)).size());
  }
}
