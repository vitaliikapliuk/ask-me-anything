package com.github.vitaliikapliuk.ask;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.vitaliikapliuk.ask.core.Config;
import com.github.vitaliikapliuk.ask.core.ExceptionListener;
import com.github.vitaliikapliuk.ask.core.ResponseDataWrapper;
import com.github.vitaliikapliuk.ask.persistance.Mappings;
import com.github.vitaliikapliuk.ask.utils.JsonUtil;
import com.github.vitaliikapliuk.ask.utils.StopWordsDetector;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final static String services = "com.github.vitaliikapliuk.ask";
    private Config config;

    private HttpServer server;

    private Server(Config config) {
        this.config = config;
    }

    public void start() throws IOException, ExecutionException, InterruptedException {
        log.info("Starting http server...");

        Mappings.initMapping(); // init mapping of ElasticSearch database
        StopWordsDetector.init(); // init stop words list from the file
        startServer();
        log.info("Server started.");
    }

    public void shutdown() throws InterruptedException {
        log.info("Stopping http server.");
        if (server != null) {
            server.shutdown();
        }
    }

    public void startServer() throws IOException {
        final ResourceConfig rc = new ResourceConfig().packages(services, "com.wordnik.swagger.jersey.listing");
        JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
        jsonProvider.setMapper(JsonUtil.json());
        rc.register(jsonProvider);
        Map<String, Object> properties = new HashMap<>();
        properties.put(ServerProperties.WADL_FEATURE_DISABLE, true);
        rc.addProperties(properties);
        rc.register(ExceptionListener.class);
        rc.register(ResponseDataWrapper.class);
        rc.register(MultiPartFeature.class);

        // swagger config
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setResourcePackage(services);
        beanConfig.setScan(true);
        beanConfig.setBasePath("/");

        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(config.serverAddress), rc);
        // swagger resources
        CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "swagger-ui/");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/docs/");

    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public static void set(Config config) {
        InstanceHolder.server.setConfig(config);
    }

    public static Config getConfig() {
        return InstanceHolder.server.config;
    }

    public static Server getInstance() {
        return InstanceHolder.server;
    }

    public static class InstanceHolder {
        private static Server server = new Server(new Config());
    }

}
