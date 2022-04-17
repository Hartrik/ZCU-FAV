<#include "layout/layout.ftl">

<#assign page_title="Settings"/>

<#macro page_body>
<div class="panel panel-default">
  <div class="panel-heading">Edit User Details</div>
  <div class="panel-body">
    <#assign form_user_details_action="/ib/settings/user/${user.id}/edit/action"/>
    <#include "part/form-user-details.ftl">
    <div style="text-align:center">
      <p>Press Submit to save. All of the fields are mandatory.</p>
    </div>
  </div>
</div>
</#macro>

<@display_page/>