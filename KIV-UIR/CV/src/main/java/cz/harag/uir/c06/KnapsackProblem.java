package cz.harag.uir.c06;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Solves knapsack problem using simple genetic algorithm.
 *
 * @author Patrik Harag
 * @version 2017-03-29
 */
public class KnapsackProblem {

    private final Random random;

    private static final int MAX_GENERATIONS = 20;
    private static final int POPULATION_SIZE = 50;

    private static final double MUTATION_RATIO = .25;
    private static final int TOURNAMENT_SIZE = 2;

    public KnapsackProblem(Random random) {
        this.random = random;
    }

    public List<KnapsackItem> fill(int capacity, List<KnapsackItem> items) {
        List<Knapsack> population = createFirstPopulation(items, POPULATION_SIZE);
        int maxPrice = items.stream().mapToInt(KnapsackItem::getPrice).sum();

        Knapsack best = null;
        int i = 0;

        do {
            if (i > 0) {
                // create next population
                population = nextPopulation(population, items);
            }

            // count fitness
            population.forEach(k -> {
                if (Double.isNaN(k.getFitness()))
                    k.setFitness(fitness(k.getItems(), capacity, maxPrice));
            });

            // sort by fitness
            population.sort(Comparator.comparingDouble(Knapsack::getFitness).reversed());

            // select best
            Knapsack thisBest = population.get(0);
            best = (best != null && best.getFitness() >= thisBest.getFitness())
                    ? best
                    : thisBest;

            // print population
            print(population);

        } while (++i < MAX_GENERATIONS && best.getFitness() != 1.);

        return best.getItems();
    }

    private List<Knapsack> nextPopulation(List<Knapsack> population, List<KnapsackItem> items) {
        return Stream
                .generate(() -> tournamentSelect(population))
                .map(i -> mutate(i, items))
                .limit(POPULATION_SIZE)
                .collect(Collectors.toList());
    }

    private Knapsack tournamentSelect(List<Knapsack> population) {
        return Stream
                .generate(() -> population.get(random.nextInt(population.size())))
                .limit(TOURNAMENT_SIZE)
                .sorted(Comparator.comparingDouble(Knapsack::getFitness).reversed())
                .findFirst()
                    .orElseThrow(NoSuchElementException::new);
    }

    private Knapsack mutate(Knapsack knapsack, List<KnapsackItem> possibilities) {
        if (random.nextDouble() >= MUTATION_RATIO)
            return knapsack;

        ArrayList<KnapsackItem> items = new ArrayList<>(knapsack.getItems());

        if (random.nextBoolean()) {
            // delete something
            if (items.size() > 0)
                items.remove(random.nextInt(items.size()));

        } else {
            // add something
            if (items.size() != possibilities.size()) {
                ArrayList<KnapsackItem> p = new ArrayList<>(possibilities);
                p.removeAll(items);
                items.add(p.get(random.nextInt(p.size())));
            }
        }
        return new Knapsack(items);
    }

    private double fitness(List<KnapsackItem> items, int capacity, int maxPrice) {
        int mass = items.stream().mapToInt(KnapsackItem::getMass).sum();
        int price = items.stream().mapToInt(KnapsackItem::getPrice).sum();

        if (mass > capacity)
            return 0;

        return (double) price / maxPrice;
    }

    private List<Knapsack> createFirstPopulation(List<KnapsackItem> items, int size) {
        List<Knapsack> population = Stream
                .generate(() -> randomItems(items))
                .limit(size)
                .collect(Collectors.toList());

        return population;
    }

    private Knapsack randomItems(List<KnapsackItem> items) {
        ArrayList<KnapsackItem> copy = new ArrayList<>(items);
        Collections.shuffle(copy, random);

        double rnd = random.nextDouble();
        return new Knapsack(copy.subList(0, (int) (rnd * copy.size())));
    }

    private void print(List<Knapsack> population) {
        System.out.println();
        System.out.println("------------------------------");
        population.forEach(System.out::println);
    }

}
