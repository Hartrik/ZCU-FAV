<form class="form-horizontal" action="${form_create_account_action}" method="POST">
  <fieldset>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div class="form-group">
      <label class="col-md-4 control-label" for="currency">Currency</label>
      <div class="col-md-4">
        <select class="form-control" id="currency" name="currency">
        <#list currencies as currency>
          <option value="${currency}">${currency}</option>
        </#list>
        </select>
      </div>
    </div>

    <div class="form-group">
      <label class="col-md-4 control-label" for="apply"></label>
      <div class="col-md-4">
        <button id="apply" class="btn btn-primary">Submit</button>
      </div>
    </div>

  </fieldset>
</form>