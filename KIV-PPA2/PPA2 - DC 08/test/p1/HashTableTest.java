package p1;

import java.util.function.BiFunction;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @version 2016-03-22
 * @author Patrik Harag
 */
public class HashTableTest {

    @Test
    public void testFindAll_hash1() {
        for (int i = 1; i < 10; i++)
            testFindAll_6(i, HashFunctions::hash1);
    }

    @Test
    public void testFindAll_hash2() {
        for (int i = 1; i < 10; i++)
            testFindAll_6(i, HashFunctions::hash2);
    }

    @Test
    public void testFindAll_hash3() {
        for (int i = 1; i < 10; i++)
            testFindAll_6(i, HashFunctions::hash3);
    }

    private void testFindAll_6(
            int capacity, BiFunction<String, Integer, Integer> hashFunc) {

        HashTable patients = new HashTable(capacity, hashFunc);

        patients.add("090806123", "Arnost");
        patients.add("8354141234", "Arnostka");
        patients.add("8512271234", "Fanda");
        patients.add("8907271234", "Jara");
        patients.add("215323030", "Marie");
        patients.add("510119233", "Franta");

        assertThat(patients.find("090806123").value, is("Arnost"));
        assertThat(patients.find("8354141234").value, is("Arnostka"));
        assertThat(patients.find("8512271234").value, is("Fanda"));
        assertThat(patients.find("8907271234").value, is("Jara"));
        assertThat(patients.find("215323030").value, is("Marie"));
        assertThat(patients.find("510119233").value, is("Franta"));

        patients.print(System.out);
        System.out.println();
    }

}
