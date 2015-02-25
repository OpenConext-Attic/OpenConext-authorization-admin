$(function() {

  $("#scope-name").on("keyup", function(e) {
    var self = $(this);
    if(e.which == 13) {
      self.before("<span class='tag c-button' data-value='" + self.val() + "'><span>" + self.val() + "</span><a href='#'>x</a></span>")
      $("#scopes").append("<option selected='selected' value='" + self.val() + "'>" + self.val() + "</option>");
      self.val("");
    }
    return false;
  });

  $("#scope-name").on("keypress", function(e) {
    if(e.which == 13) {
      // Stop form submit by returning false
      return false;
    }
  });

  $(document).on("click", ".tag a", function(e) {
    var self = $(this);
    var optionValue = self.parent().data("value");
    $("#scopes option[value='" + optionValue+ "']").remove();
    self.parent().remove();
  });

  $(".flash-notice a").on("click", function() {
    $(".flash-notice").toggleClass("hidden");
  });
});
