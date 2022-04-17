<div class="modal fade" id="dialog-account-statement" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        Send account statement to <u>${user.email}</u>
      </div>
      <div class="modal-body">
        <#assign form_account_statement_action="/ib/account/${account.id}/create-statement/action"/>
        <#include "form-account-statement.ftl">
      </div>
    </div>
  </div>
</div>