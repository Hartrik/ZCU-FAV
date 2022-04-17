<form class="form-horizontal" action="/login" method="POST">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div class="form-group">
    <label class="control-label col-sm-3" for="username">Name</label>
    <div class="col-sm-9">
      <input type="text" id="username" name="username" required=""
             class="form-control" placeholder="Enter login" maxlength="30">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3" for="password">Pass</label>
    <div class="col-sm-9">
      <input type="password" id="password" name="password" required=""
             class="form-control" placeholder="Enter password" maxlength="30">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-3 col-sm-9">
      <button type="submit" class="btn btn-default">Send</button>
    </div>
  </div>
</form>