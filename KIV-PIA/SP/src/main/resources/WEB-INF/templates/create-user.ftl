<#include "layout/layout.ftl">

<#assign page_title="Create User"/>

<#macro page_body>
<div class="panel panel-default">
  <div class="panel-heading">Create user</div>
  <div class="panel-body">
    <#assign form_user_details_action="/service/create-user/action"/>
    <#assign form_user_details_new=true/>
    <#include "part/form-user-details.ftl">
    <div style="text-align:center">
      <p>Press Submit to create a new user. All of the fields are mandatory.</p>
    </div>
  </div>
</div>
</#macro>

<@display_page/>