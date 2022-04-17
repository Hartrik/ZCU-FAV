package cz.harag.ds.cv01.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

    private static final int DEFAULT_PORT = 8080;
    public static String SHUFFLER_BASE_PATH = "http://localhost:8081";
    public static List<String> SERVER_BASE_PATHS = Arrays.asList("http://localhost:8082");

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelpAndExit();
        } else {
            ServerType serverType = null;
            try {
                serverType = ServerType.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println(e);
                printHelpAndExit();
            }

            int serverPort = DEFAULT_PORT;
            if (args.length >= 2) {
                try {
                    serverPort = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println(e);
                    printHelpAndExit();
                }
            }

            if (serverType == ServerType.SEQUENCER && args.length >= 3) {
                SHUFFLER_BASE_PATH = args[2];
            }
            if (serverType == ServerType.SHUFFLER && args.length >= 3) {
                SERVER_BASE_PATHS = new ArrayList<>();
                SERVER_BASE_PATHS.addAll(Arrays.asList(args).subList(2, args.length));
            }

            if (serverType == ServerType.SEQUENCER) {
                System.out.println("SHUFFLER_BASE_PATH: " + SHUFFLER_BASE_PATH);
            }
            if (serverType == ServerType.SHUFFLER) {
                System.out.println("SERVER_BASE_PATHS: " + SERVER_BASE_PATHS);
            }

            Server server = configureServer(serverPort, serverType);
            server.start();
            server.join();
        }
    }

    private static Server configureServer(int serverPort, ServerType serverType) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages(serverType.getServletClass().getPackage().getName());
        resourceConfig.register(JacksonFeature.class);
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder sh = new ServletHolder(servletContainer);
        Server server = new Server(serverPort);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");
        server.setHandler(context);
        return server;
    }

    private static void printHelpAndExit() {
        System.out.println("Usage:");
        System.out.println("- sequencer <port> <shuffler base path>");
        System.out.println("- shuffler <port> <server base path>...");
        System.out.println("- server <port>");
        System.exit(1);
    }

}
