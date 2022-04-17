<form class="form-vertical" action="${form_account_statement_action}" method="POST" data-toggle="validator">
  <fieldset>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div class="form-row col-md-12">
      <div class="form-group">
        <label for="date-from">Date From</label>
        <input id="date-from" name="date-from" type="text" class="form-control"
               pattern="^20[0-9]{2}-[01][0-9]-[0123][0-9]$" maxlength="10" required
               value="<#if default??>${default.statementFrom!''}</#if>">
      </div>
    </div>

    <div class="form-row col-md-12">
      <div class="form-group">
        <label for="date-to">Date To</label>
        <input id="date-to" name="date-to" type="text" class="form-control"
               pattern="^20[0-9]{2}-[01][0-9]-[0123][0-9]$" maxlength="10" required
               value="<#if default??>${default.statementTo!''}</#if>">
      </div>
    </div>

    <div class="form-row col-md-12">
      <div class="form-group col-md-12 text-center">
        <button id="apply" class="btn btn-primary">Submit</button>
      </div>
    </div>

  </fieldset>
</form>