<#include "email-layout.ftl">

<#assign page_title="JavaBank"/>

<#macro page_style>
  table {
    width: 100%;
  }

  table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
  }

  .amount-inc {
    color: green;
    font-weight: bold;
  }

  .amount-dec {
    color: darkred;
    font-weight: bold;
  }
</#macro>

<#macro page_body>
<h2>Account info</h2>
<p>
  <strong>Owner:</strong> ${firstName} ${lastName}<br/>
  <strong>Account:</strong> ${accountNumberFull}<br/>
  <strong>Current balance:</strong> ${balance} ${currency}
</p>

<h2>Transactions</h2>
<p>
  <strong>Interval:</strong> ${from} - ${to}<br/>
  <strong>Turnover:</strong> ${turnover} ${currency}<br/>
  <strong>Change:</strong> <span class="<#if change lt 0>amount-dec<#else>amount-inc</#if>">${change}</span> ${currency}
</p>

<table>
  <thead>
  <tr>
    <th>Date</th>
    <th>Amount</th>
    <th>Account</th>
    <th>Note</th>
  </tr>
  </thead>
  <tbody>
    <#list transactions as transaction>
    <tr>
      <td>${transaction.dateAsIso8601}</td>
      <td>
        <#if transaction.senderAccountNumber == accountNumberFull>
          <span class="amount-dec">- ${transaction.amountSent?string["###,###,##0.00"]}</span>
        <#else>
          <span class="amount-inc">+ ${transaction.amountReceived?string["###,###,##0.00"]}</span>
        </#if>
      </td>
      <td>
        <#if transaction.senderAccountNumber == accountNumberFull>
          ${transaction.receiverAccountNumber}
          <#else>
        ${transaction.senderAccountNumber}
        </#if>
      </td>
      <td>${transaction.description!''}</td>
    </tr>
    </#list>
  </tbody>
</table>
</#macro>

<@display_page/>