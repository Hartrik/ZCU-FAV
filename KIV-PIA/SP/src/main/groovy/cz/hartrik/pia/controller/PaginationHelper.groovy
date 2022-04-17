package cz.hartrik.pia.controller

import org.springframework.ui.Model

/**
 * Helper class for working with pagination.
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
class PaginationHelper<T> {

    /** Numbers of rows per page that can be selected by user. */
    final List<Integer> paginationValues
    /** Default number of rows per page. */
    final int defaultPagination

    PaginationHelper(List<Integer> paginationValues, int defaultPagination) {
        this.paginationValues = new ArrayList<>(paginationValues)
        this.defaultPagination = defaultPagination
    }

    List<T> paginate(Model model, String baseUrl, List<T> items, Integer page, Integer count) {
        count = (!count || count < 1) ? defaultPagination : count
        page = (!page || page < 0) ? 0 : page
        int pages = (items.size() + count - 1) / count
        if (page >= pages && pages > 0) page = pages - 1
        def itemsView = items.subList(
                Math.min(page * count, items.size()),
                Math.min((page + 1) * count, items.size()))

        fillPaginationAttributes(model, baseUrl, pages, page, count)

        return itemsView
    }

    private void fillPaginationAttributes(Model model, String baseUrl, int pages, int currentPage, int count) {
        def pageUrls = (0..<pages).collect { "${baseUrl}?page=$it&count=$count" }
        model.addAttribute('pagination_pages', pageUrls)
        model.addAttribute('pagination_current_page', pageUrls[currentPage])

        def countUrls = paginationValues.collect { [it, "${baseUrl}?page=0&count=$it"] }
        model.addAttribute('pagination_count', countUrls)
        model.addAttribute('pagination_current_count', countUrls[paginationValues.indexOf(count)])
    }

}
