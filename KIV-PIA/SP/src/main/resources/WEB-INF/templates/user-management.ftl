<#include "layout/layout.ftl">

<#assign page_title="User Management"/>

<#macro page_body>
<#include "part/dialog-confirm-delete.ftl">

<div class="panel panel-default">
  <div class="panel-heading">Customers</div>
  <div class="panel-body table-responsive">
    <table class="table table-striped">
      <thead>
        <tr>
          <th>#</th><th>Name</th><th>E-mail</th><th></th></tr>
      </thead>
      <tbody>
        <#list users as u>
        <tr>
          <td class="col-content">${u.id}</td>
          <td class="col-content">${u.firstName} ${u.lastName}</td>
          <td class="col-content"><a href="mailto:${u.email}">${u.email}</td>
          <td class="col-action">
            <a href="#" class="btn btn-danger btn-xs"
               data-href="/service/user/${u.id}/remove/action"
               data-toggle="modal" data-target="#dialog-confirm-delete">Remove</a>
          </td>
        </tr>
        </#list>
      </tbody>
    </table>
  </div>
</div>

<div class="panel panel-default">
  <div class="panel-heading">Administrators</div>
  <div class="panel-body table-responsive">
      <table class="table table-striped">
        <thead>
          <tr><th>#</th><th>Name</th><th>E-mail</th></tr>
        </thead>
        <tbody>
          <#list admins as u>
          <tr>
            <td class="col-content">${u.id}</td>
            <td class="col-content">${u.firstName} ${u.lastName}</td>
            <td class="col-content"><a href="mailto:${u.email}">${u.email}</td>
          </tr>
          </#list>
        </tbody>
      </table>
    </div>
</div>

</#macro>

<@display_page/>