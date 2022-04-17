<#setting locale="en_US">
<#macro page_body></#macro>

<#macro display_page>
<#compress>
<html>
  <head>
    <meta charset="UTF-8"/>
    <title>${page_title}</title>
    <style>
      <@page_style/>
    </style>
  </head>
  <body>
    <div>
      <h1>${page_title}</h1>
      <@page_body/>
    </div>
  </body>
</html>
</#compress>
</#macro>