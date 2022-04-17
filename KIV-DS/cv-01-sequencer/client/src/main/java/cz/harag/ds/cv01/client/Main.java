package cz.harag.ds.cv01.client;

import cz.harag.ds.cv01.client.sequencer.ApiClient;
import cz.harag.ds.cv01.client.sequencer.ApiException;
import cz.harag.ds.cv01.client.sequencer.Configuration;
import cz.harag.ds.cv01.client.sequencer.api.DefaultApi;
import cz.harag.ds.cv01.client.sequencer.model.Operation;
import java.util.Random;

/**
 * @author Patrik Harag
 * @version 2020-10-15
 */
public class Main {

    private static final String DEFAULT_BASE_PATH = "http://localhost:8080";
    private static final int DEFAULT_N = 100;

    private static final int MIN_VALUE = 10000;
    private static final int MAX_VALUE = 50000;

    public static void main(String[] args) {
        String path = DEFAULT_BASE_PATH;
        if (args.length > 0) {
            path = args[0];
        } else {
            System.out.println("Using default base path: " + path);
        }

        int n = DEFAULT_N;
        if (args.length > 1) {
            try {
                n = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                printUsage();
                return;
            }
        } else {
            System.out.println("Using default n: " + DEFAULT_N);
        }

        generate(path, n);
    }

    private static void generate(String path, int n) {
        System.out.println("Generating " + n + " entries...");

        ApiClient configuration = Configuration.getDefaultApiClient();
        configuration.setBasePath(path);
        DefaultApi apiInstance = new DefaultApi(configuration);

        Random random = new Random();

        for (int i = 0; i < n; i++) {
            int val = random.nextInt(MAX_VALUE - MIN_VALUE) + MIN_VALUE;
            if (random.nextBoolean()) {
                // CREDIT / DEBIT
                val = -val;
            }

            System.out.println(val);

            Operation operation = new Operation();
            operation.setValue(val);
            try {
                apiInstance.sequencerPost(operation);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: <sequencer base path> <number of operations>");
    }

}
