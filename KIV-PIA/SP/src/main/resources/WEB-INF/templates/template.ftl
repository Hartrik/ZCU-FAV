<#include "layout/layout.ftl">

<#assign page_title="Template"/>

<#macro page_body>

<div class="panel panel-default">
  <div class="panel-heading">Edit Template</div>
  <div class="panel-body">
    <#assign form_transaction_action="/ib/template/${template.id}/edit/action"/>
    <#assign form_transaction_is_template="true"/>
    <#assign default=template/>
    <#include "part/form-transaction.ftl">
  </div>
</div>
</#macro>

<@display_page/>