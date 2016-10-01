package p2;

import java.util.Arrays;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @version 2016-03-11
 * @author Patrik Harag
 */
public class QueueTest {

    @Test
    public void testDynamicSize() {
        Queue<Integer> q = new Queue<>();

        assertThat(q.isEmpty(), is(true));

        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);

        assertThat(q.isEmpty(), is(false));
    }

    @Test
    public void testPutGet2() {
        Queue<Integer> q = new Queue<>();

        q.enqueue(1);
        q.enqueue(2);
        assertThat(q.dequeue(), is(1));
        assertThat(q.dequeue(), is(2));

        assertThat(q.isEmpty(), is(true));
    }

    @Test
    public void testPutGet4() {
        Queue<Integer> q = new Queue<>();

        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        assertThat(q.dequeue(), is(1));
        assertThat(q.dequeue(), is(2));
        assertThat(q.dequeue(), is(3));
        assertThat(q.dequeue(), is(4));

        assertThat(q.isEmpty(), is(true));
    }

    @Test
    public void testPutGetSeq() {
        Queue<Integer> q = new Queue<>();

        q.enqueue(1);
        assertThat(q.dequeue(), is(1));
        assertThat(q.isEmpty(), is(true));

        q.enqueue(2);
        assertThat(q.dequeue(), is(2));
        assertThat(q.isEmpty(), is(true));

        q.enqueue(3);
        assertThat(q.dequeue(), is(3));
        assertThat(q.isEmpty(), is(true));

        q.enqueue(4);
        assertThat(q.dequeue(), is(4));
        assertThat(q.isEmpty(), is(true));

        q.enqueue(5);
        assertThat(q.dequeue(), is(5));
        assertThat(q.isEmpty(), is(true));
    }

    @Test
    public void tetsFillClearFillClear() {
        Queue<Integer> q = new Queue<>();

        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        assertThat(q.dequeue(), is(1));
        assertThat(q.dequeue(), is(2));
        assertThat(q.dequeue(), is(3));
        assertThat(q.dequeue(), is(4));
        assertThat(q.isEmpty(), is(true));

        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);
        assertThat(q.dequeue(), is(1));
        assertThat(q.dequeue(), is(2));
        assertThat(q.dequeue(), is(3));
        assertThat(q.dequeue(), is(4));
        assertThat(q.dequeue(), is(5));
        assertThat(q.isEmpty(), is(true));
    }

    @Test
    public void tetsFillRemoveFill() {
        Queue<Integer> q = new Queue<>();

        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);

        assertThat(q.dequeue(), is(1));
        assertThat(q.dequeue(), is(2));

        q.enqueue(5);
        q.enqueue(6);

        assertThat(q.dequeue(), is(3));
        assertThat(q.dequeue(), is(4));
        assertThat(q.dequeue(), is(5));

        q.enqueue(7);

        assertThat(q.dequeue(), is(6));
        assertThat(q.dequeue(), is(7));

        assertThat(q.isEmpty(), is(true));
    }

//    @Test
    public void testQueue() {
        Queue<Character> q = new Queue<>();

        System.out.println("----------");

        for (int i = 0; i < 12; i++) {
            q.enqueue((char) ('A' + i));
            System.out.println(Arrays.toString(q.getArray()));
        }

        System.out.println("----------");

        for (int i = 0; i < 10; i++) {
            q.dequeue();
            System.out.println(Arrays.toString(q.getArray()));
        }

        System.out.println("----------");

        for (int i = 0; i < 10; i++) {
            q.enqueue((char) ('0' + i));
            System.out.println(Arrays.toString(q.getArray()));
        }

    }

}
