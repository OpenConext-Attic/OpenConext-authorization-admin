$(function () {

  function addValueHolder(self) {
    if (self.val() !== undefined && self.val().trim().length > 0) {
      self.before("<span class='tag c-button' data-value='" + self.val() + "'><span>" + self.val() + "</span><a href='#'>x</a></span>");
      var parent = self.prop('id') === "scope-name" ? $("#scopes") : self.prop('id') === "resourceId-name" ? $("#resourceIds") : $("#callbackUrls");
      parent.append("<option selected='selected' value='" + self.val() + "'>" + self.val() + "</option>");
      self.val("");
    }
  }

  $("#scope-name, #callbackUrl-name, #resourceId-name").on("keyup", function (e) {
    var self = $(this);
    if (e.which == 13) {
      addValueHolder(self);
    }
    return false;
  });

  $("#scope-name, #callbackUrl-name, #resourceId-name").on("blur", function (e) {
    var self = $(this);
    if (self.val()) {
      addValueHolder(self);
    }
    return false;
  });

  $("#scope-name, #callbackUrl-name, #resourceId-name").on("keypress", function (e) {
    if (e.which == 13) {
      // Stop form submit by returning false
      return false;
    }
  });

  $(document).on("click", ".tag a", function (e) {
    var self = $(this);
    var value = self.parent().data("value");
    $("#scopes option[value='" + value + "']").remove();
    $("#resourceIds option[value='" + value + "']").remove();
    $("#callbackUrls option[value='" + value + "']").remove();
    self.parent().remove();
  });

  $(".flash-notice a").on("click", function () {
    $(".flash-notice").toggleClass("hidden");
  });

  $("form.delete-client").on("submit", function (e) {
    var clientId = $(this).data("client");
    return confirm("Are you sure you want to delete client: " + clientId + "?");
  });

  $("form.reset-secret").on("submit", function (e) {
    var clientId = $(this).data("client");
    return confirm("Are you sure you want to reset the secret for client: " + clientId + "?");
  });
});
