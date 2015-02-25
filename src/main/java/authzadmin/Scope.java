package authzadmin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Scope {
  @NotNull
  @Pattern(regexp = "^[a-zA-Z0-9_-]*$")
  private String value;

  public Scope() {
  }

  public Scope(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Scope scope = (Scope) o;

    if (value != null ? !value.equals(scope.value) : scope.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Scope{" +
      "value='" + value + '\'' +
      '}';
  }
}
