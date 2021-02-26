package cz.zcu.kiv.pt.sp;

import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Patrik Harag
 */
public class TrieSearchTest {

    private static String TEXT = "Either Javadoc documentation for this item "
            + "does not exist or you have not added specified Javadoc in the "
            + "Java Platform Manager or the Library Manager.";

    @Test
    public void testStartEnd() {
        SearchProvider s = new TrieSearch(TEXT);
        List<SearchResult> results = s.search("Javadoc");

        assertThat(results.size(), is(2));
        assertThat(results.get(0).getStart(), is(7));
        assertThat(results.get(0).getEnd(), is(7 + "Javadoc".length()));
    }

    @Test
    public void testNotSubstring() {
        SearchProvider s = new TrieSearch(TEXT);

        // nesmí najít Javadoc!
        assertThat(s.search("Java").size(), is(1));

        assertThat(s.search("a").size(), is(0));
        assertThat(s.search("r").size(), is(0));
    }

    @Test
    public void testIgnoreCase() {
        SearchProvider s = new TrieSearch(TEXT);

        assertEquals(s.search("or"), s.search("OR"));
    }

    // boundaries

    @Test
    public void testStartEndFirst() {
        SearchProvider s = new TrieSearch("test  ");
        List<SearchResult> results = s.search("test");

        assertThat(results.size(), is(1));
        assertThat(results.get(0).getStart(), is(0));
        assertThat(results.get(0).getEnd(), is("test".length()));
    }

    @Test
    public void testStartEndLast() {
        SearchProvider s = new TrieSearch("  test");
        List<SearchResult> results = s.search("test");

        assertThat(results.size(), is(1));
        assertThat(results.get(0).getStart(), is(2));
        assertThat(results.get(0).getEnd(), is(2 + "test".length()));
    }

    @Test
    public void testTwo() {
        SearchProvider s = new TrieSearch("test test");
        List<SearchResult> results = s.search("test");

        assertThat(results.size(), is(2));
    }

}
