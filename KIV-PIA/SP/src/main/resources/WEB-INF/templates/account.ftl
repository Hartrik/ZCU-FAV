<#include "layout/layout.ftl">

<#assign page_title="Account"/>

<#macro page_body>
<#include "part/dialog-account-statement.ftl">

<div class="panel panel-default">
  <div class="panel-heading">Account Summary</div>
  <div class="panel-body">
    <div class="row">
      <div class="col-md-6">
        <dl class="container">
          <dt class="col-md-2">Account Number</dt>
          <dd class="col-md-10">${account.accountNumberFull}</dd>

          <dt class="col-md-2">Card Number</dt>
          <dd class="col-md-10">${account.cardNumber}</dd>

          <dt class="col-md-2">Balance</dt>
          <dd class="col-md-10">${account.balance?string["###,###,##0.00"]} ${account.currency}</dd>
        </dl>
      </div>
      <div class="col-md-6">
        <div class="container send-button-container">
          <a href="/ib/account/${account.id}/send" class="btn btn-primary btn-sm">Send Payment</a>
          <a href="#" class="btn btn-default btn-sm"
             data-toggle="modal" data-target="#dialog-account-statement">Create account statement</a>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="table-responsive">
  <table class="table table-striped">
    <thead>
    <tr>
      <th scope="col">Date</th>
      <th scope="col">Amount</th>
      <th scope="col">Account</th>
      <th scope="col">Note</th>
      <th scope="col"></th>
    </tr>
    </thead>
    <tbody>
      <#list transactions as transaction>
      <tr>
        <td>${transaction.dateAsIso8601}</td>
        <td>
          <#if transaction.senderAccountNumber == account.accountNumberFull>
          <span class="amount-dec">${transaction.amountSent?string["###,###,##0.00"]}</span>
          <#else>
          <span class="amount-inc">${transaction.amountReceived?string["###,###,##0.00"]}</span>
          </#if>
        </td>
        <td>
          <#if transaction.senderAccountNumber == account.accountNumberFull>
          ${transaction.receiverAccountNumber}
          <#else>
          ${transaction.senderAccountNumber}
          </#if>
        </td>
        <td>${transaction.description!''}</td>
        <td></td>
      </tr>
      </#list>
    </tbody>
  </table>
</div>
<div>
  <#include "part/pagination.ftl">
</div>

</#macro>

<@display_page/>