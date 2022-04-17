<#include "layout/layout.ftl">

<#assign page_title="Accounts Overview"/>

<#macro page_body>

<#if accounts?has_content>
<div class="panel panel-default">
  <div class="panel-heading">Accounts</div>

  <div class="panel-body table-responsive">
    <table class="table table-striped">
      <thead>
      <tr>
        <th scope="col">Account Number</th>
        <th scope="col">Card Number</th>
        <th scope="col">Balance</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <#list accounts as account>
      <tr>
        <td>${account.accountNumberFull}</td>
        <td>${account.cardNumber}</td>
        <td>${account.balance} ${account.currency}</td>
        <td>
          <a href="/ib/account/${account.id}" class="btn btn-primary btn-xs">Show</a>
        </td>
      </tr>
      </#list>
      </tbody>
    </table>
  </div>
</div>
</#if>

<div class="panel panel-default">
  <div class="panel-heading">Create Account</div>
  <div class="panel-body">
    <#assign form_create_account_action="/ib/create-account/action"/>
    <#include "part/form-create-account.ftl">
  </div>
</div>

</#macro>

<@display_page/>