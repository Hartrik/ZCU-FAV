<#include "layout/layout.ftl">

<#assign page_title="Templates Overview"/>

<#macro page_body>
<#include "part/dialog-confirm-delete.ftl">

<#if templates?has_content>
<div class="panel panel-default">
  <div class="panel-heading">Templates</div>

  <div class="panel-body table-responsive">
    <table class="table table-striped">
      <thead>
      <tr>
        <th scope="col">Name</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <#list templates as template>
      <tr>
        <td width="100%">${template.name}</td>
        <td>
          <span style=" white-space: nowrap;">
            <a href="/ib/template/${template.id}" class="btn btn-primary btn-xs">Show</a>
            <a href="#" class="btn btn-danger btn-xs"
               data-href="/ib/template/${template.id}/remove/action"
               data-toggle="modal" data-target="#dialog-confirm-delete">Remove</a>
          </span>
        </td>
      </tr>
      </#list>
      </tbody>
    </table>
  </div>
</div>
</#if>

<div class="panel panel-default">
  <div class="panel-heading">Create Template</div>
  <div class="panel-body">
    <#assign form_transaction_action="/ib/create-template/action"/>
    <#assign form_transaction_is_template="true"/>
    <#include "part/form-transaction.ftl">
  </div>
</div>

</#macro>

<@display_page/>