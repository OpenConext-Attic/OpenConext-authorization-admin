package authzadmin.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ResourceId implements ValueHolder{

  @NotNull
  private String value;

  public ResourceId() {
  }

  public ResourceId(String value) {
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

    ResourceId resourceId = (ResourceId) o;

    if (value != null ? !value.equals(resourceId.value) : resourceId.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

}
