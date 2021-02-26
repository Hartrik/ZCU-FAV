package cz.zcu.kiv.pt.sp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Patrik Harag
 */
public class DictionaryCreatorTest {

    @Test
    public void testSpaces() {
        test("    slovo1 slovo2  ",
                "slovo1", "slovo2");
    }

    @Test
    public void testSentence() {
        test("A b c, d e f.",
                "a", "b", "c", "d", "e", "f");
    }

    private static void test(String text, String... expectedWords) {
        DictionaryCreator dictionaryCreator = new DictionaryCreator();
        Set<String> set = dictionaryCreator.createDictionary(text).asSet();

        assertThat(set, equalTo(new HashSet<>(Arrays.asList(expectedWords))));
    }

}
