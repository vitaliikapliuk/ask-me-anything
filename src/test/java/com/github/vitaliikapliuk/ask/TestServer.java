package com.github.vitaliikapliuk.ask;

import com.github.vitaliikapliuk.ask.core.Config;
import com.github.vitaliikapliuk.ask.services.QuestionsServiceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RunWith(Suite.class)
@Suite.SuiteClasses({ QuestionsServiceTest.class})
public class TestServer {

    public static final String SERVER_ADDRESS = "http://127.0.0.1:7778";

    private static final Logger LOG = LoggerFactory.getLogger(TestServer.class);
    private static Server server;

    @BeforeClass
    public static void startServer() {
        // init server
        server = Server.getInstance();

        Config config = new Config();
        config.serverAddress = SERVER_ADDRESS; //override main server address with port
        server.setConfig(config);

        try {
            server.start();
        } catch (Throwable t) {
            LOG.error("Cannot run Test Server ", t);
        }
    }

    @AfterClass
    public static void stopServer() throws IOException, InterruptedException {
        server.shutdown();
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        startServer();
    }

}