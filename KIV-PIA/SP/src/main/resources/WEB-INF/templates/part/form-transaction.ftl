<form class="form-vertical" action="${form_transaction_action}" method="POST" data-toggle="validator">
  <fieldset>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <#if form_transaction_is_template??>
    <div class="form-row col-md-12">
      <div class="form-group col-md-12">
        <label for="name">Template name</label>
        <input id="name" name="name" type="text" class="form-control"
               maxlength="64" required
               value="<#if default??>${default.name!''}</#if>">
      </div>
    </div>
    </#if>

    <div class="form-row col-md-12">
      <div class="form-group col-md-4">
        <label for="amount">Amount</label>
        <input id="amount" name="amount" class="form-control"
               type="number"
               <#if !form_transaction_is_template??>required</#if>
               value="<#if default??>${default.amount!''}</#if>">
      </div>
      <div class="form-group col-md-3">
        <label for="currency">Currency</label>
        <select class="form-control" id="currency" name="currency">
        <#list currencies as currency>
          <option <#if default?? && default.currency?? && default.currency == currency>selected</#if> value="${currency}">${currency}</option>
        </#list>
        </select>
      </div>
    </div>

    <div class="form-row col-md-12">
      <div class="form-group col-md-4">
        <label for="accountNumber">Account number</label>
        <input id="accountNumber" name="accountNumber" type="text" class="form-control"
               pattern="^([0-9]{10}|[0-9]{16})$" maxlength="16"
               <#if !form_transaction_is_template??>required</#if>
               value="<#if default??>${default.accountNumber!''}</#if>">
      </div>
      <div class="form-group col-md-3">
        <label for="bankCode">Bank code</label>
        <input id="bankCode" name="bankCode" type="text" class="form-control"
               pattern="^([0-9]{4}$" maxlength="4"
               <#if !form_transaction_is_template??>required</#if>
               value="<#if default??>${default.bankCode!''}</#if>">
      </div>
    </div>

    <#if !form_transaction_is_template??>
    <div class="form-row col-md-12">
      <div class="form-group col-md-4">
        <label for="date">Due Date</label>
        <input id="date" name="date" type="text" class="form-control"
               pattern="^20[0-9]{2}-[01][0-9]-[0123][0-9]$" maxlength="10" required
               value="<#if default??>${default.date!''}</#if>">
      </div>
    </div>
    </#if>

    <div class="form-row col-md-12">
      <div class="form-group col-md-12">
        <label for="description">Note (optional)</label>
        <input id="description" name="description" type="text" class="form-control"
               maxlength="200"
               value="<#if default??>${default.description!''}</#if>">
      </div>
    </div>

    <#if !form_transaction_is_template??>
    <input type="hidden" id="turingTestQuestionId" name="turingTestQuestionId" value="${turing_test.id}">
    <div class="form-row col-md-12">
      <div class="form-group col-md-12">
        <label for="turingTestAnswer">Anti-Robot Test: ${turing_test.question}</label>
        <input id="turingTestAnswer" name="turingTestAnswer" type="text" class="form-control"
               required>
      </div>
    </div>
    </#if>

    <div class="form-row col-md-12">
      <div class="form-group col-md-12 text-center">
        <button id="apply" class="btn btn-primary">Submit</button>
      </div>
    </div>

  </fieldset>
</form>