package cz.harag.uir.c06;

import cz.hartrik.common.Point;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Patrik Harag
 * @version 2017-03-30
 */
public class PuzzleState {

    private final int n;
    private final List<Point> queens;

    private final double fitness;

    public PuzzleState(int n, List<Point> queens) {
        this.n = n;
        this.queens = queens;
        this.fitness = fitness();
    }

    public List<Point> getQueens() {
        return Collections.unmodifiableList(queens);
    }

    public double getFitness() {
        return fitness;
    }

    private double fitness() {
        if (queens.size() > n)
            return 0;

        double r = (double) conflicts() / (2 * n);
        return 1 - r;
    }

    protected int conflicts() {
        return horConflicts() + verConflicts() + diagConflicts();
    }

    protected int horConflicts() {
        return IntStream.range(0, n)
                .map(i -> (int) queens.stream().mapToInt(Point::getX).filter(ii -> i == ii).count())
                .map(i -> i > 0 ? i - 1 : 0)  // dva na jedné řádce -> jedna kolize
                .sum();
    }

    protected int verConflicts() {
        return IntStream.range(0, n)
                .map(i -> (int) queens.stream().mapToInt(Point::getY).filter(ii -> i == ii).count())
                .map(i -> i > 0 ? i - 1 : 0)
                .sum();
    }

    protected int diagConflicts() {
        int conflicts = queens.stream().mapToInt(p -> {
                    return (int) queens.stream().filter(q -> {
                        int deltaRow = Math.abs(p.getX() - q.getX());
                        int deltaCol = Math.abs(p.getY() - q.getY());
                        return deltaRow == deltaCol;
                    }).count();
                })
                .map(i -> i > 0 ? i - 1 : 0)
                .sum();

        return (int) Math.sqrt(conflicts);
    }

    @Override
    public String toString() {
        return "PuzzleState{" +
                "fitness=" + fitness +
                ", queens=" + queens +
                '}';
    }
}
