<nav>
  <ul class="pagination">
    <#list pagination_count as index_and_page>
      <#if pagination_current_count?? && index_and_page[0] == pagination_current_count[0]>
        <li class="page-item active">
      <span class="page-link">
        ${(index_and_page[0])}
          <span class="sr-only">(current)</span>
      </span>
        </li>
      <#else>
        <li class="page-item">
          <a class="page-link" href="${index_and_page[1]}">${(index_and_page[0])}</a>
        </li>
      </#if>
    </#list>
  </ul>
  <#if pagination_pages?size gt 1>
  <ul class="pagination">
    <#list pagination_pages as page>
    <#if page == pagination_current_page>
    <li class="page-item active">
      <span class="page-link">
        ${(page?index + 1)}
        <span class="sr-only">(current)</span>
      </span>
    </li>
    <#else>
    <li class="page-item">
      <a class="page-link" href="${page}">${(page?index + 1)}</a>
    </li>
    </#if>
    </#list>
  </ul>
  </#if>
</nav>
