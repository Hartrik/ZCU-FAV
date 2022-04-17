package cz.harag.ds.cv02.server;

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

    public static Bank BANK;

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            printHelpAndExit();
        } else {
            int serviceRestApiPort;
            try {
                serviceRestApiPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                printHelpAndExit();
                return;
            }
            List<String> bindAddresses = Arrays.asList(args[1].split(","));
            List<String> connectAddresses = Arrays.asList(args[2].split(","));
            if (bindAddresses.size() != connectAddresses.size()) {
                System.err.println("Bind and connect addresses cannot be paired");
                printHelpAndExit();
                return;
            }

            List<BankConnection> connections = new ArrayList<>();
            for (int i = 0; i < bindAddresses.size(); i++) {
                connections.add(new BankConnection(bindAddresses.get(i), connectAddresses.get(i)));
            }

            BANK = new Bank(connections);
            BANK.bindAll();
            BANK.connectAll();
            BANK.startGenerator();
            BANK.startWorker();

            System.out.println("Starting service REST server at port " + serviceRestApiPort);
            Server server = configureServer(serviceRestApiPort);
            server.start();
            server.join();
        }
    }

    private static Server configureServer(int serverPort) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("cz.harag.ds.cv02.server.rest");
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
        System.out.println("Parameters:");
        System.out.println("  <SERVICE_REST_API_PORT>");
        System.out.println("  <BIND PORT>:<BIND ADDRESS>,...");
        System.out.println("  <CONNECT PORT>:<CONNECT ADDRESS>,...");
        System.exit(1);
    }

}
