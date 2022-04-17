package cz.hartrik.pia;

import cz.hartrik.pia.config.BaseConfig;
import java.io.IOException;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Jetty initializer.
 *
 * @version 2018-11-16
 * @author Patrik Harag
 */
class EmbeddedJetty {

    private static final int DEFAULT_PORT = 5000;

    private static final String CONTEXT_PATH = "/";
    private static final String MAPPING_URL = "/";
    private static final String WEB_APP_DIRECTORY = "WEB-INF";

    /**
     * Initializes Jetty with Spring MVC.
     */
    static void init() throws Exception {
        String envPort = System.getenv("PORT");
        int port = (envPort != null)
                ? Integer.valueOf(envPort)
                : DEFAULT_PORT;

        new EmbeddedJetty().startJetty(port);
    }

    private void startJetty(int port) throws Exception {
        Server server = new Server(port);

        server.setHandler(getServletContextHandler());

        addRuntimeShutdownHook(server);

        server.start();
        server.join();
    }

    private static ServletContextHandler getServletContextHandler() throws IOException {
        WebApplicationContext webAppContext = getWebApplicationContext();
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webAppContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);  // handling 404
        ServletHolder springServletHolder = new ServletHolder("mvc-dispatcher", dispatcherServlet);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setErrorHandler(null);
        contextHandler.setResourceBase(new ClassPathResource(WEB_APP_DIRECTORY).getURI().toString());
        contextHandler.setContextPath(CONTEXT_PATH);
        contextHandler.addServlet(springServletHolder, MAPPING_URL);
        contextHandler.addEventListener(new ContextLoaderListener(webAppContext));

        // spring security will not work without this
        // https://stackoverflow.com/questions/30927761/embedded-jetty-doesnt-recognise-spring-mvc-security
        contextHandler.addFilter(
                new FilterHolder(new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)),
                "/*",
                EnumSet.allOf(DispatcherType.class));

        return contextHandler;
    }

    private static void addRuntimeShutdownHook(Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server.isStarted()) {
                server.setStopAtShutdown(true);
                try {
                    server.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));
	}

    private static WebApplicationContext getWebApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(BaseConfig.class);
        return context;
    }

}