<#include "email-layout.ftl">

<#assign page_title="Welcome to JavaBank"/>

<#macro page_style>
</#macro>

<#macro page_body>
<p>Hi ${firstName}! Let's get started with JavaBank!</p>

<p>
  Login: ${login}<br>
  Password: ${rawPassword}
</p>

<p>JavaBank Team</p>
</#macro>

<@display_page/>