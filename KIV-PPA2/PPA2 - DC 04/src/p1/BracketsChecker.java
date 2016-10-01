package p1;


/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class BracketsChecker {

    public static final String OPEN = "([{";
    public static final String CLOSE = ")]}";

    public static boolean check(String expression) {
        Stack<Character> stack = new Stack<>();

        for (char c : expression.toCharArray()) {
            if (OPEN.indexOf(c) > -1) {
                stack.push(c);
                continue;
            }

            int i = CLOSE.indexOf(c);
            if (i > -1) {
                if (stack.isEmpty() || OPEN.indexOf(stack.pop()) != i) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

}
