package sp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public class MainTest {

    @Test
    public void test_1M() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Main.process(new Scanner(new File("data_1M.txt")), new PrintStream(os));

        assertThat(os.toString("UTF-8"), is("BFS(B): B, C, A"));
    }

    @Test
    public void test_1S() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Main.process(new Scanner(new File("data_1S.txt")), new PrintStream(os));

        assertThat(os.toString("UTF-8"), is("BFS(B): B, C, A"));
    }

    @Test
    public void test_2M() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Main.process(new Scanner(new File("data_2M.txt")), new PrintStream(os));

        assertThat(os.toString("UTF-8"), is("DFS(D): D, C, B, A"));
    }

    @Test
    public void test_2S() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Main.process(new Scanner(new File("data_2S.txt")), new PrintStream(os));

        assertThat(os.toString("UTF-8"), is("DFS(D): D, C, B, A"));
    }

}