package cz.harag.uir.c03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Patrik Harag
 * @version 2017-03-11
 */
public class HanoiSearch {

    public static void doBFS(int n) {
        LinkedList<HanoiState> states = new LinkedList<>();  // queue
        states.add(HanoiState.createInitialState(n));

        while (!states.isEmpty()) {
            HanoiState state = states.removeFirst();
            System.out.println(state);

            if (state.isFinalState()) {
                System.out.println("Solution found!");
                break;
            }

            possibilities(state).forEach(states::addLast);
        }
    }

    public static void doDFS(int n, int limit, boolean randomized) {
        LinkedList<HanoiState> states = new LinkedList<>();  // stack
        states.add(HanoiState.createInitialState(n));

        while (!states.isEmpty()) {
            HanoiState state = states.removeFirst();
            System.out.println(state);

            if (state.isFinalState()) {
                System.out.println("Solution found!");
                break;
            }

            if (state.getLevel() == limit) continue;

            List<HanoiState> possibilities = possibilities(state);
            if (randomized)
                Collections.shuffle(possibilities);

            possibilities.forEach(states::addFirst);
        }
    }

    private static List<HanoiState> possibilities(HanoiState state) {
        List<HanoiState> states = new ArrayList<>();

        for (int from = 0; from < HanoiState.PEGS; from++)
            for (int to = 0; to < HanoiState.PEGS; to++)
                if (state.canMove(from, to))
                    states.add(state.move(from, to));

        return states;
    }

}
