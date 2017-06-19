package cz.harag.uir.c04;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Patrik Harag
 * @version 2017-03-19
 */
public class PuzzleState {

    // true -> queen
    // false -> empty     y x
    private final Boolean[][] array;

    protected PuzzleState(Boolean[][] array) {
        this.array = Arrays.stream(array)
                .map(Boolean[]::clone)
                .toArray(length -> new Boolean[length][]);
    }

    public PuzzleState setAt(int x, int y) {
        if (!canSetAt(x, y))
            throw new IllegalArgumentException();

        PuzzleState newState = new PuzzleState(array);
        newState.array[y][x] = true;
        return newState;
    }

    public boolean canSetAt(int x, int y) {
        return checkHorizontal(x, y) && checkVertical(x, y) && checkDiagonals(x, y);
    }

    private boolean checkHorizontal(int x, int y) {
        return Stream.of(array[y]).noneMatch(s -> s);
    }

    private boolean checkVertical(int x, int y) {
        return Stream.of(array).noneMatch(v -> v[x]);
    }

    private boolean checkDiagonals(int x, int y) {
        int maxI = Math.max(array.length,
                            Math.max(Math.abs(array.length - x),
                                     Math.abs(array.length - y)));

        for (int i = 1; i < maxI; i++) {
            // top-right
            if (!check(x + i, y + i)) return false;
            if (!check(x - i, y - i)) return false;

            // top-left
            if (!check(x + i, y - i)) return false;
            if (!check(x - i, y + i)) return false;
        }
        return true;
    }

    private boolean check(int x, int y) {
        return !(valid(x, y) && array[y][x]);
    }

    private boolean valid(int x, int y) {
        return (x >= 0) && (y >= 0) && (y < array.length) && (x < array.length);
    }

    public static PuzzleState empty(int n) {
        Boolean[][] array = Stream.generate(() -> Stream.generate(() -> Boolean.FALSE)
                    .limit(n)
                    .toArray(Boolean[]::new))
                .limit(n)
                .toArray(length -> new Boolean[length][]);

        return new PuzzleState(array);
    }

    // Object

    @Override
    public String toString() {
        return Stream.of(array)
                .map(s -> Stream.of(s)
                        .map(b -> b ? " # " : " . ")
                        .collect(Collectors.joining("", "|", "|")))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuzzleState that = (PuzzleState) o;
        return Arrays.deepEquals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(array);
    }
}
