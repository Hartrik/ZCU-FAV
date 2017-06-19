package cz.harag.uir.c04;


import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author Patrik Harag
 * @version 2017-03-19
 */
public class QueensPuzzle {

    public static Set<PuzzleState> solve(int n) {
        Set<PuzzleState> solutions = new LinkedHashSet<>();

        solve(n, PuzzleState.empty(n), 0, solutions::add);
        return solutions;
    }

    private static void solve(int n, PuzzleState state, int i,
                              Consumer<PuzzleState> consumer) {
        if (i >= n) {
            consumer.accept(state);

        } else {
            for (int j = 0; j < n; j++) {
                if (state.canSetAt(j, i)) {
                    PuzzleState newState = state.setAt(j, i);
                    solve(n, newState, i+1, consumer);
                }
            }
        }
    }

}
