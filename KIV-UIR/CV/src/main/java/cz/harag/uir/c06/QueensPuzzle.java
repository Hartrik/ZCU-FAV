package cz.harag.uir.c06;

import cz.hartrik.common.Point;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Solves queens puzzle using simple genetic algorithm.
 *
 * @author Patrik Harag
 * @version 2017-03-30
 */
public class QueensPuzzle {

    private final Random random;

    private static final int MAX_GENERATIONS = 1000;
    private static final int POPULATION_SIZE = 150;

    private static final double MUTATION_RATIO = .25;
    private static final int TOURNAMENT_SIZE = 2;

    public QueensPuzzle(Random random) {
        this.random = random;
    }

    public PuzzleState fill(int n) {
        List<PuzzleState> population = createFirstPopulation(n, POPULATION_SIZE);

        PuzzleState best = null;
        int i = 0;

        do {
            if (i > 0) {
                // create next population
                population = nextPopulation(population, n);
            }

            // sort by fitness
            population.sort(Comparator.comparingDouble(PuzzleState::getFitness).reversed());

            // select best
            PuzzleState thisBest = population.get(0);
            best = (best != null && best.getFitness() >= thisBest.getFitness())
                    ? best
                    : thisBest;

            // print population
            // print(population);

        } while (++i < MAX_GENERATIONS && best.getFitness() != 1.);

        return best;
    }

    private List<PuzzleState> nextPopulation(List<PuzzleState> population, int n) {
        return Stream
                .generate(() -> tournamentSelect(population))
                .map(i -> mutate(i, n))
                .limit(POPULATION_SIZE)
                .collect(Collectors.toList());
    }

    private PuzzleState tournamentSelect(List<PuzzleState> population) {
        return Stream
                .generate(() -> population.get(random.nextInt(population.size())))
                .limit(TOURNAMENT_SIZE)
                .sorted(Comparator.comparingDouble(PuzzleState::getFitness).reversed())
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private PuzzleState mutate(PuzzleState puzzle, int n) {
        if (random.nextDouble() >= MUTATION_RATIO)
            return puzzle;

        List<Point> queens = new ArrayList<>(puzzle.getQueens());
        queens.remove(random.nextInt(n));
        queens.add(Point.of(random.nextInt(n), random.nextInt(n)));
        return new PuzzleState(n, queens);
    }

    private List<PuzzleState> createFirstPopulation(int n, int size) {
        List<PuzzleState> population = Stream
                .generate(() -> randomItems(n))
                .limit(size)
                .collect(Collectors.toList());

        return population;
    }

    private PuzzleState randomItems(int n) {
        List<Point> points = Stream
                .generate(() -> Point.of(random.nextInt(n), random.nextInt(n)))
                .limit(n)
                .collect(Collectors.toList());

        return new PuzzleState(n, points);
    }

    private void print(List<PuzzleState> population) {
        System.out.println();
        System.out.println("------------------------------");
        population.forEach(System.out::println);
    }

}
